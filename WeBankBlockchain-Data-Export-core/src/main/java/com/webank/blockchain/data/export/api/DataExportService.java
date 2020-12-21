package com.webank.blockchain.data.export.api;

import cn.hutool.db.Db;
import cn.hutool.db.meta.MetaUtil;
import com.webank.blockchain.data.export.common.entity.ChainInfo;
import com.webank.blockchain.data.export.common.entity.DataExportContext;
import com.webank.blockchain.data.export.common.entity.ExportConfig;
import com.webank.blockchain.data.export.common.entity.ExportConstant;
import com.webank.blockchain.data.export.common.entity.ExportDataSource;
import com.webank.blockchain.data.export.common.entity.MysqlDataSource;
import com.webank.blockchain.data.export.common.entity.TableSQL;
import com.webank.blockchain.data.export.task.DataExportExecutor;
import com.webank.blockchain.data.export.tools.ClientUtil;
import com.webank.blockchain.data.export.tools.DataSourceUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.driver.api.ShardingSphereDataSourceFactory;
import org.apache.shardingsphere.infra.config.algorithm.ShardingSphereAlgorithmConfiguration;
import org.apache.shardingsphere.sharding.api.config.ShardingRuleConfiguration;
import org.apache.shardingsphere.sharding.api.config.rule.ShardingTableRuleConfiguration;
import org.apache.shardingsphere.sharding.api.config.strategy.sharding.StandardShardingStrategyConfiguration;
import org.fisco.bcos.sdk.config.exceptions.ConfigException;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author wesleywang
 * @Description:
 * @date 2020/12/16
 */
@Slf4j
public class DataExportService {

    public static DataExportExecutor create(ExportDataSource dataSource, ChainInfo chainInfo, ExportConfig config) throws ConfigException {
        return new DataExportExecutor(buildContext(dataSource, chainInfo, config));
    }

    public static void start(DataExportExecutor exportExecutor) {
        exportExecutor.start();
    }

    public static void stop(DataExportExecutor exportExecutor) {
        exportExecutor.stop();
    }

    private static DataExportContext buildContext(ExportDataSource dataSource, ChainInfo chainInfo, ExportConfig config) throws ConfigException {
        DataExportContext context = new DataExportContext();
        context.setClient(ClientUtil.getClient(chainInfo));
        context.setChainInfo(chainInfo);
        context.setConfig(config);
        context.setDataSource(buildDataSource(dataSource));
        context.setEsConfig(dataSource.getEsDataSource());
        context.setAutoCreateTable(dataSource.isAutoCreateTable());
        return context;
    }

    private static DataSource buildDataSource(ExportDataSource exportDataSource) {
        if (!exportDataSource.isSharding()){
            return buildSingleDataSource(exportDataSource.getMysqlDataSources().get(0),
                    exportDataSource.isAutoCreateTable());
        } else {
            return buildShardingDataSource(exportDataSource.getMysqlDataSources(),
                    exportDataSource.getShardingNumberPerDatasource(),
                    exportDataSource.isAutoCreateTable());
        }
    }


    private static DataSource buildSingleDataSource(MysqlDataSource mysqlDataSource, boolean autoCreateTable) {
        DataSource dataSource = DataSourceUtils.createDataSource(mysqlDataSource.getJdbcUrl(),
                null,
                mysqlDataSource.getUser(),
                mysqlDataSource.getPass());
        if (autoCreateTable){
            createTable(dataSource);
        }
        return dataSource;
    }

    private static DataSource buildShardingDataSource(List<MysqlDataSource> mysqlDataSources,
                                                      int shardingNumberPerDatasource,
                                                      boolean autoCreateTable) {
        Map<String, DataSource> dataSourceMap = new HashMap<>();
        String dsName = "ds";
        int i = 0;
        for (MysqlDataSource dataSource : mysqlDataSources) {
            DataSource ds = buildSingleDataSource(dataSource, false);
            dataSourceMap.put(dsName + i++, ds);
            if(autoCreateTable) {
                creatShardingTables(ds, shardingNumberPerDatasource);
            }
        }
        // 配置分片规则
        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();

        for (String table : ExportConstant.tables) {
            if (table.equals(ExportConstant.BLOCK_TASK_POOL_TABLE)){
                continue;
            }
            // 配置表规则
            ShardingTableRuleConfiguration orderTableRuleConfig = new
                    ShardingTableRuleConfiguration(table, "ds${0..1}." + table + "${0..1}");
            // 配置分库策略
            orderTableRuleConfig.setDatabaseShardingStrategy(
                    new StandardShardingStrategyConfiguration("block_height", table + "_dbShardingAlgorithm"));
            // 配置分表策略
            orderTableRuleConfig.setTableShardingStrategy(
                    new StandardShardingStrategyConfiguration("block_height", table + "_tableShardingAlgorithm"));
            shardingRuleConfig.getTables().add(orderTableRuleConfig);

            // 配置分库算法
            Properties dbShardingAlgorithmrProps = new Properties();
            dbShardingAlgorithmrProps.setProperty("algorithm-expression",
                    dsName + "${block_height % " + mysqlDataSources.size() + "}");
            // 配置分表算法
            Properties tableShardingAlgorithmrProps = new Properties();
            tableShardingAlgorithmrProps.setProperty("algorithm-expression",
                    table + "${block_height % " + shardingNumberPerDatasource + "}");
            shardingRuleConfig.getShardingAlgorithms().put(table + "_dbShardingAlgorithm",
                    new ShardingSphereAlgorithmConfiguration("INLINE", dbShardingAlgorithmrProps));
            shardingRuleConfig.getShardingAlgorithms().put(table + "_tableShardingAlgorithm",
                    new ShardingSphereAlgorithmConfiguration("INLINE", tableShardingAlgorithmrProps));
        }
        try {
            return ShardingSphereDataSourceFactory.createDataSource(dataSourceMap,
                    Collections.singleton(shardingRuleConfig), new Properties());
        } catch (SQLException e) {
            log.error("ShardingSphereDataSourceFactory createDataSource failed ", e);
        }
        return null;
    }

    private static void creatShardingTables(DataSource ds, int shardingNumberPerDatasource) {
        log.info("export data auto create table begin....");
        Db db = Db.use(ds);
        List<String> tables = MetaUtil.getTables(ds);
        try {
            for (Map.Entry<String, String> entry : TableSQL.tableSqlMap.entrySet()) {
                if (entry.getKey().equals(ExportConstant.BLOCK_TASK_POOL_TABLE)){
                    if (!tables.contains(entry.getKey())){
                        db.execute(entry.getValue());
                    }
                    continue;
                }
                for (int i = 0; i < shardingNumberPerDatasource; i++) {
                    if (!tables.contains(entry.getKey() + i)) {
                        db.execute(entry.getValue().replaceFirst(entry.getKey(),entry.getKey() + i));
                    }
                }
            }
        } catch (SQLException e) {
            log.error("export data table create failed, reason is : ", e);
            Thread.currentThread().interrupt();
        }
        log.info("export data auto create table success !");
    }

    private static void createTable(DataSource ds) {
        log.info("export data auto create table begin....");
        try {
            Db db = Db.use(ds);
            List<String> tables = MetaUtil.getTables(ds);
            for (Map.Entry<String, String> entry : TableSQL.tableSqlMap.entrySet()) {
                if (!tables.contains(entry.getKey())) {
                    db.execute(entry.getValue());
                }
            }
        } catch (SQLException e) {
            log.error("export data table create failed, reason is : ", e);
            Thread.currentThread().interrupt();
        }
        log.info("export data auto create table success !");
    }

}
