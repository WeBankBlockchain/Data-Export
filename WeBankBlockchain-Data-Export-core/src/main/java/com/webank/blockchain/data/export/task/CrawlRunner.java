/**
 * Copyright 2020 Webank.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.webank.blockchain.data.export.task;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.db.DaoTemplate;
import cn.hutool.db.Db;
import cn.hutool.db.meta.MetaUtil;
import com.google.common.collect.Lists;
import com.webank.blockchain.data.export.common.bo.contract.ContractDetail;
import com.webank.blockchain.data.export.common.bo.contract.ContractMapsInfo;
import com.webank.blockchain.data.export.common.bo.data.BlockInfoBO;
import com.webank.blockchain.data.export.common.bo.data.ContractInfoBO;
import com.webank.blockchain.data.export.common.constants.BlockConstants;
import com.webank.blockchain.data.export.common.constants.ContractConstants;
import com.webank.blockchain.data.export.common.entity.DataExportContext;
import com.webank.blockchain.data.export.common.entity.ExportConstant;
import com.webank.blockchain.data.export.common.entity.ExportDataSource;
import com.webank.blockchain.data.export.common.entity.MysqlDataSource;
import com.webank.blockchain.data.export.common.entity.TableSQL;
import com.webank.blockchain.data.export.common.enums.DataType;
import com.webank.blockchain.data.export.db.dao.BlockDetailInfoDAO;
import com.webank.blockchain.data.export.db.dao.BlockRawDataDAO;
import com.webank.blockchain.data.export.db.dao.BlockTxDetailInfoDAO;
import com.webank.blockchain.data.export.db.dao.ContractInfoDAO;
import com.webank.blockchain.data.export.db.dao.DeployedAccountInfoDAO;
import com.webank.blockchain.data.export.db.dao.ESHandleDao;
import com.webank.blockchain.data.export.db.dao.MethodAndEventDao;
import com.webank.blockchain.data.export.db.dao.SaveInterface;
import com.webank.blockchain.data.export.db.dao.TxRawDataDAO;
import com.webank.blockchain.data.export.db.dao.TxReceiptRawDataDAO;
import com.webank.blockchain.data.export.db.repository.BlockDetailInfoRepository;
import com.webank.blockchain.data.export.db.repository.BlockRawDataRepository;
import com.webank.blockchain.data.export.db.repository.BlockTaskPoolRepository;
import com.webank.blockchain.data.export.db.repository.BlockTxDetailInfoRepository;
import com.webank.blockchain.data.export.db.repository.ContractInfoRepository;
import com.webank.blockchain.data.export.db.repository.DeployedAccountInfoRepository;
import com.webank.blockchain.data.export.db.repository.RollbackInterface;
import com.webank.blockchain.data.export.db.repository.TxRawDataRepository;
import com.webank.blockchain.data.export.db.repository.TxReceiptRawDataRepository;
import com.webank.blockchain.data.export.db.service.DataStoreService;
import com.webank.blockchain.data.export.db.service.ESStoreService;
import com.webank.blockchain.data.export.db.service.MysqlStoreService;
import com.webank.blockchain.data.export.parser.contract.ContractParser;
import com.webank.blockchain.data.export.service.BlockAsyncService;
import com.webank.blockchain.data.export.service.BlockCheckService;
import com.webank.blockchain.data.export.service.BlockDepotService;
import com.webank.blockchain.data.export.service.BlockIndexService;
import com.webank.blockchain.data.export.service.BlockPrepareService;
import com.webank.blockchain.data.export.tools.DataSourceUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.driver.api.ShardingSphereDataSourceFactory;
import org.apache.shardingsphere.infra.config.algorithm.ShardingSphereAlgorithmConfiguration;
import org.apache.shardingsphere.sharding.api.config.ShardingRuleConfiguration;
import org.apache.shardingsphere.sharding.api.config.rule.ShardingTableRuleConfiguration;
import org.apache.shardingsphere.sharding.api.config.strategy.sharding.StandardShardingStrategyConfiguration;
import org.elasticsearch.client.transport.TransportClient;
import org.fisco.bcos.sdk.client.protocol.response.BcosBlock.Block;
import org.fisco.bcos.sdk.transaction.codec.decode.TransactionDecoderService;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.webank.blockchain.data.export.common.entity.ExportConstant.BLOCK_DETAIL_DAO;
import static com.webank.blockchain.data.export.common.entity.ExportConstant.BLOCK_RAW_DAO;
import static com.webank.blockchain.data.export.common.entity.ExportConstant.BLOCK_TASK_POOL_DAO;
import static com.webank.blockchain.data.export.common.entity.ExportConstant.BLOCK_TX_DETAIL_DAO;
import static com.webank.blockchain.data.export.common.entity.ExportConstant.CONTRACT_INFO_DAO;
import static com.webank.blockchain.data.export.common.entity.ExportConstant.DEPLOYED_ACCOUNT_DAO;
import static com.webank.blockchain.data.export.common.entity.ExportConstant.TX_RAW_DAO;
import static com.webank.blockchain.data.export.common.entity.ExportConstant.TX_RECEIPT_RAW_DAO;
import static com.webank.blockchain.data.export.common.entity.ExportConstant.tables;
import static com.webank.blockchain.data.export.common.entity.ExportConstant.threadLocal;

/**
 * GenerateCodeApplicationRunner
 *
 * @author maojiayu
 * @Description: GenerateCodeApplicationRunner
 * @date 2018年11月29日 下午4:37:38
 */

@Slf4j
@Data
public class CrawlRunner {

    private DataExportContext context;

    private long startBlockNumber;

    private BlockTaskPoolRepository blockTaskPoolRepository;
    private BlockDetailInfoRepository blockDetailInfoRepository;
    private BlockRawDataRepository blockRawDataRepository;
    private BlockTxDetailInfoRepository blockTxDetailInfoRepository;
    private TxRawDataRepository txRawDataRepository;
    private TxReceiptRawDataRepository txReceiptRawDataRepository;
    private DeployedAccountInfoRepository deployedAccountInfoRepository;
    private ContractInfoRepository contractInfoRepository;

    private List<DataStoreService> dataStoreServiceList = new ArrayList<>();
    private List<RollbackInterface> rollbackOneInterfaceList = new ArrayList<>();

    private AtomicBoolean runSwitch = new AtomicBoolean(false);

    public static CrawlRunner create(DataExportContext context){
        return new CrawlRunner(context);
    }

    private CrawlRunner(DataExportContext context) {
        this.context = context;
    }


    public void export() {
        checkConfig();
        if (!runSwitch.get()){
            log.info("data export config check failed, task already stop");
            return;
        }
        //abi、bin parse
        ContractParser.initContractMaps(threadLocal.get().getConfig().getContractInfoList());
        buildDataStore();
        handle();
    }

    private void checkConfig() {
        if (CollectionUtil.isEmpty(context.getExportDataSource().getMysqlDataSources())) {
            log.error("mysqlDataSources is not set，please set it ！！！");
            return;
        }
        if (context.getExportDataSource().isSharding()) {
            if (context.getExportDataSource().getShardingNumberPerDatasource() == 0) {
                log.error("shardingNumberPerDatasource is zero, please set it to a number greater than 0 ");
                return;
            }
            if (context.getExportDataSource().getMysqlDataSources().size() < 2) {
                log.error("isSharding is true, mysqlDataSources size must >= 2 ");
                return;
            }
        }
        if (context.getChainInfo().getNodeStr() == null) {
            log.error("nodeStr is not set，please set it ！！！ ");
            return;
        }
        if (context.getChainInfo().getCertPath() == null) {
            log.error("certPath is not set，please set it ！！！ ");
            return;
        }
        if (CollectionUtil.isEmpty(context.getConfig().getDataTypeBlackList())) {
            context.getConfig().setDataTypeBlackList(DataType.getDefault());
        }
        if (context.getConfig().getCrawlBatchUnit() < 1) {
            log.error("The batch unit threshold can't be less than 1!!");
            return;
        }
        runSwitch.getAndSet(true);
    }

    public long getHeight(long height) {
        return Math.max(height, startBlockNumber);
    }

    /**
     * The key driving entrance of single instance depot: 1. check timeout txs and process errors; 2. produce tasks; 3.
     * consume tasks; 4. check the fork status; 5. rollback; 6. continue and circle;
     *
     */
    public void handle() {
        try{
            threadLocal.get().setDecoder(new TransactionDecoderService(
                    threadLocal.get().getClient().getCryptoSuite()));
            saveContractInfo();
        }catch (Exception e) {
            log.error("save Contract Info, {}", e.getMessage());
        }

        try {
            startBlockNumber = BlockIndexService.getStartBlockIndex();
            log.info("Start succeed, and the block number is {}", startBlockNumber);
        } catch (Exception e) {
            log.error("depot Error, {}", e.getMessage());
        }
        while (!Thread.currentThread().isInterrupted() && runSwitch.get()) {
            try {
                long currentChainHeight = BlockPrepareService.getCurrentBlockHeight();
                long fromHeight = getHeight(BlockPrepareService.getTaskPoolHeight());
                // control the batch unit number
                long end = fromHeight + context.getConfig().getCrawlBatchUnit() - 1;
                long toHeight = Math.min(currentChainHeight, end);
                log.info("Current depot status: {} of {}, and try to process block from {} to {}", fromHeight - 1,
                        currentChainHeight, fromHeight, toHeight);
                boolean certainty = toHeight + 1 < currentChainHeight - BlockConstants.MAX_FORK_CERTAINTY_BLOCK_NUMBER;
                if (fromHeight <= toHeight) {
                    log.info("Try to sync block number {} to {} of {}", fromHeight, toHeight, currentChainHeight);
                    BlockPrepareService.prepareTask(fromHeight, toHeight, certainty);
                } else {
                    // single circle sleep time is read from the application.properties
                    log.info("No sync block tasks to prepare, begin to sleep {} s",
                            context.getConfig().getFrequency());
                    try {
                        Thread.sleep(context.getConfig().getFrequency() * 1000);
                    }catch (InterruptedException e){
                        Thread.currentThread().interrupt();
                    }
                }
                log.info("Begin to fetch at most {} tasks", context.getConfig().getCrawlBatchUnit());
                List<Block> taskList = BlockDepotService.fetchData(context.getConfig().getCrawlBatchUnit());
                for (Block b : taskList) {
                    BlockAsyncService.handleSingleBlock(b, currentChainHeight);
                }
                if (!certainty) {
                    BlockCheckService.checkForks(currentChainHeight);
                    BlockCheckService.checkTaskCount(startBlockNumber, currentChainHeight);
                }
                BlockCheckService.checkTimeOut();
                BlockCheckService.processErrors();
            } catch (Exception e) {
                log.error("CrawlRunner run failed ", e);
                try {
                    Thread.sleep(60 * 1000L);
                } catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        log.info("DataExportExecutor already ended ！！！");
    }

    private void saveContractInfo() {
        if (CollectionUtil.isEmpty(context.getConfig().getContractInfoList())) {
            return;
        }
        ContractMapsInfo mapsInfo = ContractConstants.contractMapsInfo.get();
        Map<String, ContractDetail> contractBinaryMap = mapsInfo.getContractBinaryMap();
        if (CollectionUtil.isEmpty(contractBinaryMap)) {
            return;
        }
        for (Map.Entry<String, ContractDetail> entry : contractBinaryMap.entrySet()){
            ContractInfoBO contractInfoBO = entry.getValue().getContractInfoBO();
            for (DataStoreService storeService : dataStoreServiceList) {
                storeService.storeContractInfo(contractInfoBO);
            }
        }
    }

    public void buildDataStore() {
        context.setDataSource(buildDataSource(context.getExportDataSource(),
                DataType.getTables(context.getConfig().getDataTypeBlackList())));
        buildRepository();
        buildDao();
        buildESStore();
    }

    private void buildESStore(){
        if (context.getEsConfig() != null && context.getEsConfig().isEnable()) {
            TransportClient esClient = ESHandleDao.create();
            context.setEsClient(esClient);
            dataStoreServiceList.add(new ESStoreService());
        }
    }

    private void buildDao(){
        List<SaveInterface<BlockInfoBO>> saveInterfaceList = new ArrayList<>();
        MysqlStoreService mysqlStoreService = new MysqlStoreService();
        mysqlStoreService.setSaveInterfaceList(saveInterfaceList);
        dataStoreServiceList.add(mysqlStoreService);

        if (blockDetailInfoRepository != null) {
            BlockDetailInfoDAO blockDetailInfoDao = new BlockDetailInfoDAO(blockDetailInfoRepository);
            saveInterfaceList.add(blockDetailInfoDao);
        }
        if (blockTxDetailInfoRepository != null) {
            BlockTxDetailInfoDAO blockTxDetailInfoDao = new BlockTxDetailInfoDAO(blockTxDetailInfoRepository);
            saveInterfaceList.add(blockTxDetailInfoDao);
        }
        if (blockRawDataRepository != null) {
            BlockRawDataDAO blockRawDataDao = new BlockRawDataDAO(blockRawDataRepository);
            saveInterfaceList.add(blockRawDataDao);
        }
        if (txRawDataRepository != null) {
            TxRawDataDAO txRawDataDao = new TxRawDataDAO(txRawDataRepository);
            saveInterfaceList.add(txRawDataDao);
        }
        if (txReceiptRawDataRepository != null) {
            TxReceiptRawDataDAO txReceiptRawDataDao = new TxReceiptRawDataDAO(txReceiptRawDataRepository);
            saveInterfaceList.add(txReceiptRawDataDao);
        }
        if (deployedAccountInfoRepository != null){
            DeployedAccountInfoDAO deployedAccountInfoDAO = new DeployedAccountInfoDAO(deployedAccountInfoRepository);
            saveInterfaceList.add(deployedAccountInfoDAO);
        }
        if (contractInfoRepository != null){
            mysqlStoreService.setContractInfoDAO(new ContractInfoDAO(contractInfoRepository));
        }
        MethodAndEventDao methodAndEventDao = new MethodAndEventDao();
        saveInterfaceList.add(methodAndEventDao);
    }

    private void buildRepository(){
        Map<String, DaoTemplate> daoTemplateMap = buildDaoMap(context);
        blockTaskPoolRepository = new BlockTaskPoolRepository(
                daoTemplateMap.get(BLOCK_TASK_POOL_DAO));
        rollbackOneInterfaceList.add(blockTaskPoolRepository);
        List<DataType> blackTables = context.getConfig().getDataTypeBlackList();

        if (!blackTables.contains(DataType.BLOCK_DETAIL_INFO_TABLE)) {
            blockDetailInfoRepository = new BlockDetailInfoRepository(
                    daoTemplateMap.get(BLOCK_DETAIL_DAO));
            rollbackOneInterfaceList.add(blockDetailInfoRepository);
        }
        if (!blackTables.contains(DataType.BLOCK_RAW_DATA_TABLE)) {
            blockRawDataRepository = new BlockRawDataRepository(daoTemplateMap.get(
                    BLOCK_RAW_DAO));
            rollbackOneInterfaceList.add(blockRawDataRepository);
        }
        if (!blackTables.contains(DataType.BLOCK_TX_DETAIL_INFO_TABLE)) {
            blockTxDetailInfoRepository = new BlockTxDetailInfoRepository(
                    daoTemplateMap.get(BLOCK_TX_DETAIL_DAO));
            rollbackOneInterfaceList.add(blockTxDetailInfoRepository);
        }
        if (!blackTables.contains(DataType.TX_RAW_DATA_TABLE)) {
            txRawDataRepository = new TxRawDataRepository(
                    daoTemplateMap.get(TX_RAW_DAO));
            rollbackOneInterfaceList.add(txRawDataRepository);
        }
        if (!blackTables.contains(DataType.TX_RECEIPT_RAW_DATA_TABLE)) {
            txReceiptRawDataRepository = new TxReceiptRawDataRepository(
                    daoTemplateMap.get(TX_RECEIPT_RAW_DAO));
            rollbackOneInterfaceList.add(txReceiptRawDataRepository);
        }
        if (!blackTables.contains(DataType.DEPLOYED_ACCOUNT_INFO_TABLE)) {
            deployedAccountInfoRepository = new DeployedAccountInfoRepository(
                    daoTemplateMap.get(DEPLOYED_ACCOUNT_DAO));
        }
        if (!blackTables.contains(DataType.CONTRACT_INFO_TABLE)) {
            contractInfoRepository = new ContractInfoRepository(
                    daoTemplateMap.get(CONTRACT_INFO_DAO));
        }

    }

    private Map<String, DaoTemplate> buildDaoMap(DataExportContext context) {
        Db db = Db.use(context.getDataSource());
        Map<String, DaoTemplate> daoTemplateMap = new ConcurrentHashMap<>();
        tables.forEach(table -> {
            if (DataType.getTables(context.getConfig().getDataTypeBlackList()).contains(table)){
                return;
            }
            DaoTemplate daoTemplate = new DaoTemplate(table, "pk_id", db);
            daoTemplateMap.put(table + "_dao", daoTemplate);
        });
        return daoTemplateMap;
    }

    private static DataSource buildDataSource(ExportDataSource exportDataSource, List<String> blackTables) {
        if (!exportDataSource.isSharding()) {
            return buildSingleDataSource(exportDataSource.getMysqlDataSources().get(0),
                    exportDataSource.isAutoCreateTable(), blackTables);
        } else {
            return buildShardingDataSource(exportDataSource.getMysqlDataSources(),
                    exportDataSource.getShardingNumberPerDatasource(),
                    exportDataSource.isAutoCreateTable(), blackTables);
        }
    }

    private static DataSource buildSingleDataSource(MysqlDataSource mysqlDataSource) {
        return buildSingleDataSource(mysqlDataSource, false, null);
    }


    private static DataSource buildSingleDataSource(MysqlDataSource mysqlDataSource, boolean autoCreateTable,
                                                    List<String> blackTables) {
        DataSource dataSource = DataSourceUtils.createDataSource(mysqlDataSource.getJdbcUrl(),
                null,
                mysqlDataSource.getUser(),
                mysqlDataSource.getPass());
        if (autoCreateTable) {
            createTable(dataSource, blackTables);
        }
        return dataSource;
    }

    private static DataSource buildShardingDataSource(List<MysqlDataSource> mysqlDataSources,
                                                      int shardingNumberPerDatasource,
                                                      boolean autoCreateTable, List<String> blackTables) {
        Map<String, DataSource> dataSourceMap = new HashMap<>();
        String dsName = "ds";
        int i = 0;
        for (MysqlDataSource dataSource : mysqlDataSources) {
            DataSource ds = buildSingleDataSource(dataSource);
            dataSourceMap.put(dsName + i++, ds);
            if (autoCreateTable) {
                creatShardingTables(ds, shardingNumberPerDatasource, blackTables);
            }
        }
        // 配置分片规则
        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();

        List<String> tables = Lists.newArrayList();
        tables.addAll(ExportConstant.tables);
        //event and method tables
        addMethodAndEventTables(tables);

        for (String table : tables) {
            if (table.equals(ExportConstant.BLOCK_TASK_POOL_TABLE) || table.equals(ExportConstant.CONTRACT_INFO_TABLE)) {
                continue;
            }
            // table rule
            ShardingTableRuleConfiguration orderTableRuleConfig = new
                    ShardingTableRuleConfiguration(table, "ds${0..1}." + table + "${0..1}");
            // database rule
            orderTableRuleConfig.setDatabaseShardingStrategy(
                    new StandardShardingStrategyConfiguration("block_height", table + "_dbShardingAlgorithm"));
            // table order rule
            orderTableRuleConfig.setTableShardingStrategy(
                    new StandardShardingStrategyConfiguration("block_height", table + "_tableShardingAlgorithm"));
            shardingRuleConfig.getTables().add(orderTableRuleConfig);

            // database Algorithm
            Properties dbShardingAlgorithmrProps = new Properties();
            dbShardingAlgorithmrProps.setProperty("algorithm-expression",
                    dsName + "${block_height % " + mysqlDataSources.size() + "}");
            // table Algorithm
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

    private static void addMethodAndEventTables(List<String> tables){
        //event and method tables
        ContractMapsInfo mapsInfo = ContractConstants.contractMapsInfo.get();
        if (mapsInfo != null) {
            for (Map.Entry<String, ContractDetail> contractDetailMap : mapsInfo.getContractBinaryMap().entrySet()) {
                ContractDetail contractDetail = contractDetailMap.getValue();
                if (CollectionUtil.isNotEmpty(contractDetail.getEventMetaInfos())) {
                    contractDetail.getEventMetaInfos().forEach(eventMetaInfo ->
                            tables.add(TableSQL.getTableName(eventMetaInfo.getContractName(), eventMetaInfo.getEventName())));
                }
                if (CollectionUtil.isNotEmpty(contractDetail.getMethodMetaInfos())) {
                    contractDetail.getMethodMetaInfos().forEach(methodMetaInfo ->
                            tables.add(TableSQL.getTableName(methodMetaInfo.getContractName(), methodMetaInfo.getMethodName())));
                }
            }
        }
    }

    private static void creatShardingTables(DataSource ds, int shardingNumberPerDatasource, List<String> blackTables) {
        log.info("export data auto create table begin....");
        Db db = Db.use(ds);
        List<String> tables = MetaUtil.getTables(ds);
        try {
            for (Map.Entry<String, String> entry : TableSQL.tableSqlMap.entrySet()) {
                if (blackTables.contains(entry.getKey())) {
                    continue;
                }
                if (entry.getKey().equals(ExportConstant.BLOCK_TASK_POOL_TABLE)
                        || entry.getKey().equals(ExportConstant.CONTRACT_INFO_TABLE)) {
                    if (!tables.contains(entry.getKey())) {
                        db.execute(entry.getValue());
                    }
                    continue;
                }
                for (int i = 0; i < shardingNumberPerDatasource; i++) {
                    if (!tables.contains(entry.getKey() + i)) {
                        db.execute(entry.getValue().replaceFirst(entry.getKey(), entry.getKey() + i));
                    }
                }
            }
            createMethodAndEventShardingTable(db,blackTables,tables,shardingNumberPerDatasource);
        } catch (SQLException e) {
            log.error("export data table create failed, reason is : ", e);
            Thread.currentThread().interrupt();
        }
        log.info("export data auto create table success !");
    }

    private static void createTable(DataSource ds, List<String> blackTables) {
        log.info("export data auto create table begin....");
        try {
            Db db = Db.use(ds);
            List<String> tables = MetaUtil.getTables(ds);
            for (Map.Entry<String, String> entry : TableSQL.tableSqlMap.entrySet()) {
                if (blackTables.contains(entry.getKey())) {
                    continue;
                }
                if (!tables.contains(entry.getKey())) {
                    db.execute(entry.getValue());
                }
            }
            createMethodAndEventTable(db, blackTables, tables);
        } catch (SQLException e) {
            log.error("export data table create failed, reason is : ", e);
            Thread.currentThread().interrupt();
        }
        log.info("export data auto create table success !");
    }

    private static void createMethodAndEventTable(Db db, List<String> blackTables, List<String> tables) {
        ContractMapsInfo mapsInfo = ContractConstants.contractMapsInfo.get();

        for (Map.Entry<String, ContractDetail> contractDetailMap : mapsInfo.getContractBinaryMap().entrySet()) {
            ContractDetail contractDetail = contractDetailMap.getValue();
            if (!blackTables.contains(DataType.METHOD_TABLE.getTableName()) && CollectionUtil.isNotEmpty(contractDetail.getMethodMetaInfos())) {
                contractDetail.getMethodMetaInfos().forEach(methodMetaInfo -> {
                    String tableSql = TableSQL.createMethodTableSql(methodMetaInfo);
                    try {
                        if (!tables.contains(TableSQL.getTableName(methodMetaInfo.getContractName(),
                                methodMetaInfo.getMethodName()))) {
                            db.execute(tableSql);
                        }
                    } catch (SQLException e) {
                        log.error("export data table create failed, reason is : ", e);
                    }
                });
            }
            if (!blackTables.contains(DataType.EVENT_TABLE.getTableName()) && CollectionUtil.isNotEmpty(contractDetail.getEventMetaInfos())) {
                contractDetail.getEventMetaInfos().forEach(eventMetaInfo -> {
                    String tableSql = TableSQL.createEventTableSql(eventMetaInfo);
                    try {
                        if (!tables.contains(TableSQL.getTableName(eventMetaInfo.getContractName(),
                                eventMetaInfo.getEventName()))) {
                            db.execute(tableSql);
                        }
                    } catch (SQLException e) {
                        log.error("export data table create failed, reason is : ", e);
                    }
                });
            }
        }
    }

    private static void createMethodAndEventShardingTable(Db db, List<String> blackTables,
                                                          List<String> tables, int shardingNumberPerDatasource) {
        ContractMapsInfo mapsInfo = ContractConstants.contractMapsInfo.get();
        for (Map.Entry<String, ContractDetail> contractDetailMap : mapsInfo.getContractBinaryMap().entrySet()) {
            ContractDetail contractDetail = contractDetailMap.getValue();
            if (!blackTables.contains(DataType.METHOD_TABLE.getTableName()) && CollectionUtil.isNotEmpty(contractDetail.getMethodMetaInfos())) {
                contractDetail.getMethodMetaInfos().forEach(methodMetaInfo -> {
                    String tableSql = TableSQL.createMethodTableSql(methodMetaInfo);
                    try {
                        for (int i = 0; i < shardingNumberPerDatasource; i++) {
                            String tableName = TableSQL.getTableName(methodMetaInfo.getContractName(),
                                    methodMetaInfo.getMethodName());
                            if (!tables.contains(tableName + i)) {
                                db.execute(tableSql.replaceFirst(tableName, tableName + i));
                            }
                        }
                    } catch (SQLException e) {
                        log.error("export data table create failed, reason is : ", e);
                    }
                });
            }
            if (!blackTables.contains(DataType.EVENT_TABLE.getTableName()) && CollectionUtil.isNotEmpty(contractDetail.getEventMetaInfos())) {
                contractDetail.getEventMetaInfos().forEach(eventMetaInfo -> {
                    String tableSql = TableSQL.createEventTableSql(eventMetaInfo);
                    try {
                        for (int i = 0; i < shardingNumberPerDatasource; i++) {
                            String tableName = TableSQL.getTableName(eventMetaInfo.getContractName(),
                                    eventMetaInfo.getEventName());
                            if (!tables.contains(tableName + i)) {
                                db.execute(tableSql.replaceFirst(tableName, tableName + i));
                            }
                        }
                    } catch (SQLException e) {
                        log.error("export data table create failed, reason is : ", e);
                    }
                });
            }
        }


    }


}