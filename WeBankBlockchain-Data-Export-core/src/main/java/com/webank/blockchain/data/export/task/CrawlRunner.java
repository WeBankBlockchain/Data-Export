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

import cn.hutool.db.DaoTemplate;
import com.webank.blockchain.data.export.common.constants.BlockConstants;
import com.webank.blockchain.data.export.common.entity.DataExportContext;
import com.webank.blockchain.data.export.common.entity.ExportConstant;
import com.webank.blockchain.data.export.service.BlockAsyncService;
import com.webank.blockchain.data.export.service.BlockCheckService;
import com.webank.blockchain.data.export.service.BlockDepotService;
import com.webank.blockchain.data.export.service.BlockIndexService;
import com.webank.blockchain.data.export.service.BlockPrepareService;
import com.webank.blockchain.data.export.db.dao.BlockDetailInfoDAO;
import com.webank.blockchain.data.export.db.dao.BlockRawDataDAO;
import com.webank.blockchain.data.export.db.dao.BlockTxDetailInfoDAO;
import com.webank.blockchain.data.export.db.dao.DeployedAccountInfoDAO;
import com.webank.blockchain.data.export.db.dao.TxRawDataDAO;
import com.webank.blockchain.data.export.db.dao.TxReceiptRawDataDAO;
import com.webank.blockchain.data.export.db.repository.BlockDetailInfoRepository;
import com.webank.blockchain.data.export.db.repository.BlockRawDataRepository;
import com.webank.blockchain.data.export.db.repository.BlockTaskPoolRepository;
import com.webank.blockchain.data.export.db.repository.BlockTxDetailInfoRepository;
import com.webank.blockchain.data.export.db.repository.DeployedAccountInfoRepository;
import com.webank.blockchain.data.export.db.repository.RollbackInterface;
import com.webank.blockchain.data.export.db.repository.TxRawDataRepository;
import com.webank.blockchain.data.export.db.repository.TxReceiptRawDataRepository;
import com.webank.blockchain.data.export.db.service.DataStoreService;
import com.webank.blockchain.data.export.db.service.MysqlStoreService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.fisco.bcos.sdk.client.protocol.response.BcosBlock.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    private List<DataStoreService> dataStoreServiceList = new ArrayList<>();
    private List<RollbackInterface> rollbackOneInterfaceMap = new ArrayList<>();


    public CrawlRunner(DataExportContext context) {
        this.context = context;
    }


    public void run(DataExportContext context) throws InterruptedException {
        if (context.getConfig().getCrawlBatchUnit() < 1) {
            log.error("The batch unit threshold can't be less than 1!!");
            System.exit(1);
        }
        buildDataStore();
        handle();
    }

    public long getHeight(long height) {
        return Math.max(height, startBlockNumber);
    }

    /**
     * The key driving entrance of single instance depot: 1. check timeout txs and process errors; 2. produce tasks; 3.
     * consume tasks; 4. check the fork status; 5. rollback; 6. continue and circle;
     *
     * @throws InterruptedException
     */
    public void handle() throws InterruptedException {
        try {
            startBlockNumber = BlockIndexService.getStartBlockIndex();
            log.info("Start succeed, and the block number is {}", startBlockNumber);
        } catch (Exception e) {
            log.error("depot Error, {}", e.getMessage());
        }
        while (!Thread.currentThread().isInterrupted()) {
            try {
                long currentChainHeight = BlockPrepareService.getCurrentBlockHeight();
                long fromHeight = getHeight(BlockPrepareService.getTaskPoolHeight(blockTaskPoolRepository));
                // control the batch unit number
                long end = fromHeight + context.getConfig().getCrawlBatchUnit() - 1;
                long toHeight = Math.min(currentChainHeight, end);
                log.info("Current depot status: {} of {}, and try to process block from {} to {}", fromHeight - 1,
                        currentChainHeight, fromHeight, toHeight);
                boolean certainty = toHeight + 1 < currentChainHeight - BlockConstants.MAX_FORK_CERTAINTY_BLOCK_NUMBER;
                if (fromHeight <= toHeight) {
                    log.info("Try to sync block number {} to {} of {}", fromHeight, toHeight, currentChainHeight);
                    BlockPrepareService.prepareTask(fromHeight, toHeight, certainty, blockTaskPoolRepository);
                } else {
                    // single circle sleep time is read from the application.properties
                    log.info("No sync block tasks to prepare, begin to sleep {} s",
                            context.getConfig().getFrequency());
                    Thread.sleep(context.getConfig().getFrequency() * 1000);
                }
                log.info("Begin to fetch at most {} tasks", context.getConfig().getCrawlBatchUnit());
                List<Block> taskList = BlockDepotService.fetchData(context.getConfig().getCrawlBatchUnit(), blockTaskPoolRepository);
                for (Block b : taskList) {
                    BlockAsyncService.handleSingleBlock(b, currentChainHeight, blockTaskPoolRepository, dataStoreServiceList);
                }
                if (!certainty) {
                    BlockCheckService.checkForks(currentChainHeight, blockTaskPoolRepository,
                            blockDetailInfoRepository, rollbackOneInterfaceMap);
                    BlockCheckService.checkTaskCount(startBlockNumber, currentChainHeight, blockTaskPoolRepository);
                }
                BlockCheckService.checkTimeOut(blockTaskPoolRepository);
                BlockCheckService.processErrors(blockTaskPoolRepository, rollbackOneInterfaceMap);
            } catch (Exception e) {
                log.error("{}", e);
                Thread.sleep(60 * 1000L);
            }
        }

    }

    public void buildDataStore() {
        Map<String, DaoTemplate> daoTemplateMap = ExportConstant.daoThreadLocal.get();

        blockTaskPoolRepository = new BlockTaskPoolRepository(
                daoTemplateMap.get(ExportConstant.BLOCK_TASK_POOL_DAO));
        blockDetailInfoRepository = new BlockDetailInfoRepository(
                daoTemplateMap.get(ExportConstant.BLOCK_DETAIL_DAO));
        blockRawDataRepository = new BlockRawDataRepository(daoTemplateMap.get(
                ExportConstant.BLOCK_RAW_DAO));
        blockTxDetailInfoRepository = new BlockTxDetailInfoRepository(
                daoTemplateMap.get(ExportConstant.BLOCK_TX_DETAIL_DAO));
        txRawDataRepository = new TxRawDataRepository(
                daoTemplateMap.get(ExportConstant.TX_RAW_DAO));
        txReceiptRawDataRepository = new TxReceiptRawDataRepository(
                daoTemplateMap.get(ExportConstant.TX_RECEIPT_RAW_DAO));
        deployedAccountInfoRepository = new DeployedAccountInfoRepository(
                daoTemplateMap.get(ExportConstant.DEPLOYED_ACCOUNT_INFO_TABLE));

        rollbackOneInterfaceMap.add(blockTaskPoolRepository);
        rollbackOneInterfaceMap.add(blockDetailInfoRepository);
        rollbackOneInterfaceMap.add(blockRawDataRepository);
        rollbackOneInterfaceMap.add(txRawDataRepository);
        rollbackOneInterfaceMap.add(txReceiptRawDataRepository);
        rollbackOneInterfaceMap.add(blockTxDetailInfoRepository);


        BlockDetailInfoDAO blockDetailInfoDao = new BlockDetailInfoDAO(blockDetailInfoRepository);
        BlockTxDetailInfoDAO blockTxDetailInfoDao = new BlockTxDetailInfoDAO(blockTxDetailInfoRepository);
        BlockRawDataDAO blockRawDataDao = new BlockRawDataDAO(blockRawDataRepository);
        TxRawDataDAO txRawDataDao = new TxRawDataDAO(txRawDataRepository);
        TxReceiptRawDataDAO txReceiptRawDataDao = new TxReceiptRawDataDAO(txReceiptRawDataRepository);
        DeployedAccountInfoDAO deployedAccountInfoDao = new DeployedAccountInfoDAO(deployedAccountInfoRepository);
        MysqlStoreService mysqlStoreService = new MysqlStoreService();
        mysqlStoreService.setBlockDetailInfoDao(blockDetailInfoDao);
        mysqlStoreService.setBlockRawDataDao(blockRawDataDao);
        mysqlStoreService.setBlockTxDetailInfoDao(blockTxDetailInfoDao);
        mysqlStoreService.setDeployedAccountInfoDao(deployedAccountInfoDao);
        mysqlStoreService.setTxReceiptRawDataDao(txReceiptRawDataDao);
        mysqlStoreService.setTxRawDataDao(txRawDataDao);

        dataStoreServiceList.add(mysqlStoreService);

    }

}