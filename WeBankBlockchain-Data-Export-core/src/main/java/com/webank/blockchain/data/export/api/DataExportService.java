package com.webank.blockchain.data.export.api;

import com.webank.blockchain.data.export.common.entity.ChainInfo;
import com.webank.blockchain.data.export.common.entity.DataExportContext;
import com.webank.blockchain.data.export.common.entity.ExportConfig;
import com.webank.blockchain.data.export.common.entity.ExportDataSource;
import com.webank.blockchain.data.export.common.entity.MysqlDataSource;
import com.webank.blockchain.data.export.task.DataExportExecutor;
import com.webank.blockchain.data.export.tools.ClientUtil;
import com.webank.blockchain.data.export.tools.DataSourceUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.driver.api.ShardingSphereDataSourceFactory;
import org.apache.shardingsphere.infra.config.RuleConfiguration;
import org.apache.shardingsphere.infra.config.algorithm.ShardingSphereAlgorithmConfiguration;
import org.apache.shardingsphere.sharding.api.config.ShardingRuleConfiguration;
import org.apache.shardingsphere.sharding.api.config.rule.ShardingTableRuleConfiguration;
import org.apache.shardingsphere.sharding.api.config.strategy.keygen.KeyGenerateStrategyConfiguration;
import org.apache.shardingsphere.sharding.api.config.strategy.sharding.StandardShardingStrategyConfiguration;
import org.fisco.bcos.sdk.config.exceptions.ConfigException;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
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
        return new DataExportExecutor(buildContext(dataSource,chainInfo,config));
    }

    public static void start(DataExportExecutor exportExecutor){
        exportExecutor.start();
    }

    public static void stop(DataExportExecutor exportExecutor){
        exportExecutor.stop();
    }

    private static DataExportContext buildContext(ExportDataSource dataSource, ChainInfo chainInfo, ExportConfig config) throws ConfigException {
        DataExportContext context = new DataExportContext();
        context.setClient(ClientUtil.getClient(chainInfo));
        context.setChainInfo(chainInfo);
        context.setConfig(config);
        context.setDataSource(dataSource.isSharding() ? buildDataSource(dataSource.getMysqlDataSources()) :
                buildDataSource(dataSource.getMysqlDataSources().get(0)));
        context.setEsConfig(dataSource.getEsDataSource());
        context.setAutoCreateTable(dataSource.isAutoCreateTable());
        return context;
    }

    private static DataSource buildDataSource(MysqlDataSource mysqlDataSource) {
        return DataSourceUtils.createDataSource(mysqlDataSource.getJdbcUrl(),
                null,
                mysqlDataSource.getUser(),
                mysqlDataSource.getPass());
    }

    private static DataSource buildDataSource(List<MysqlDataSource> mysqlDataSources){
        Map<String, DataSource> dataSourceMap = new HashMap<>();
        String ds = "ds";
        int i = 0;
        for(MysqlDataSource dataSource : mysqlDataSources){
            dataSourceMap.put(ds + i,buildDataSource(dataSource));
        }
        // 配置表规则
        ShardingTableRuleConfiguration orderTableRuleConfig = new
                ShardingTableRuleConfiguration("t_order", "ds${0..1}.t_order${0..1}");
        // 配置分库策略
        orderTableRuleConfig.setDatabaseShardingStrategy(
                new StandardShardingStrategyConfiguration("block_height", "dbShardingAlgorithm"));

        // 配置分表策略
        orderTableRuleConfig.setTableShardingStrategy(
                new StandardShardingStrategyConfiguration("block_height", "tableShardingAlgorithm"));

        // 配置分片规则
        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
        shardingRuleConfig.getTables().add(orderTableRuleConfig);

        // 配置分库算法
        Properties dbShardingAlgorithmrProps = new Properties();
        dbShardingAlgorithmrProps.setProperty("algorithm-expression",
                "t_order${block_height % "+ mysqlDataSources.size()  + "}");

        shardingRuleConfig.getShardingAlgorithms().put("dbShardingAlgorithm",
                new ShardingSphereAlgorithmConfiguration("INLINE", dbShardingAlgorithmrProps));

        // 配置分表算法
        Properties tableShardingAlgorithmrProps = new Properties();
        tableShardingAlgorithmrProps.setProperty("algorithm-expression",
                "t_order${block_height % "+ mysqlDataSources.size()  + "}");

        shardingRuleConfig.getShardingAlgorithms().put("tableShardingAlgorithm",
                new ShardingSphereAlgorithmConfiguration("INLINE", tableShardingAlgorithmrProps));

        try {
            return ShardingSphereDataSourceFactory.createDataSource(dataSourceMap,
                    Collections.singleton(shardingRuleConfig), new Properties());
        } catch (SQLException e) {
            log.error("ShardingSphereDataSourceFactory createDataSource failed ", e);
        }
        return null;
    }




}
