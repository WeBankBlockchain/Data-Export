/**
 * Copyright 2014-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.webank.blockchain.data.export.task;

import com.webank.blockchain.data.export.common.bo.contract.ContractMapsInfo;
import com.webank.blockchain.data.export.common.client.ChainClient;
import com.webank.blockchain.data.export.common.client.ChannelClient;
import com.webank.blockchain.data.export.common.client.RpcHttpClient;
import com.webank.blockchain.data.export.common.client.StashClient;
import com.webank.blockchain.data.export.common.constants.BlockConstants;
import com.webank.blockchain.data.export.common.constants.ContractConstants;
import com.webank.blockchain.data.export.common.entity.ChainInfo;
import com.webank.blockchain.data.export.common.entity.DataExportContext;
import com.webank.blockchain.data.export.common.entity.ExportConstant;
import com.webank.blockchain.data.export.common.entity.StashInfo;
import com.webank.blockchain.data.export.parser.contract.ContractParser;
import com.webank.blockchain.data.export.service.BlockCheckService;
import com.webank.blockchain.data.export.service.BlockIndexService;
import com.webank.blockchain.data.export.service.BlockPrepareService;
import com.webank.blockchain.data.export.tools.DataSourceUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.simple.job.SimpleJob;
import org.fisco.bcos.sdk.config.exceptions.ConfigException;
import org.fisco.bcos.sdk.transaction.codec.decode.TransactionDecoderService;

import javax.sql.DataSource;
import java.io.IOException;
import java.math.BigInteger;
import java.net.MalformedURLException;

/**
 * PrepareTaskJob
 *
 * @Description: PrepareTaskJob
 * @author maojiayu
 * @data Jan 11, 2019 10:03:29 AM
 *
 */
@Slf4j
public class PrepareTaskJob implements SimpleJob {

    private final DataExportContext context;

    private final DataPersistenceManager dataPersistenceManager;

    private final ContractMapsInfo mapsInfo;

    public PrepareTaskJob(DataExportContext context) {
        this.context = context;
        this.dataPersistenceManager = DataPersistenceManager.create(context);
        DataPersistenceManager.setCurrentManager(dataPersistenceManager);
        ExportConstant.setCurrentContext(context);
        try {
            buildClient();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ConfigException e) {
            e.printStackTrace();
        }
        mapsInfo = ContractParser.initContractMaps(context.getConfig().getContractInfoList());
        ContractConstants.setCurrentContractMaps(mapsInfo);
        dataPersistenceManager.buildDataStore();
        try{
            this.context.setDecoder(new TransactionDecoderService(context.getClient().getCryptoSuite()));
            dataPersistenceManager.saveContractInfo();
        }catch (Exception e) {
            log.error("save Contract Info, {}", e.getMessage());
        }
    }

    public ContractMapsInfo getMapsInfo(){
        return mapsInfo;
    }

    public DataPersistenceManager getDataPersistenceManager() {
        return dataPersistenceManager;
    }

    @Override
    public void execute(ShardingContext shardingContext) {
        ExportConstant.setCurrentContext(context);
        DataPersistenceManager.setCurrentManager(dataPersistenceManager);
        ContractConstants.setCurrentContractMaps(mapsInfo);
        long startBlockNumber = 0;
        try {
            startBlockNumber = BlockIndexService.getStartBlockIndex();
            log.info("Start succeed, and the block number is {}", startBlockNumber);
        } catch (Exception e) {
            log.error("depot Error, {}", e.getMessage());
        }
        try {
            BigInteger blockNumber = context.getClient().getBlockNumber();
            long total = blockNumber.longValue();
            log.info("Current chain block number is:{}", total);
            long height = BlockPrepareService.getTaskPoolHeight();
            height = Math.max(height, startBlockNumber);
            long end = height + context.getConfig().getCrawlBatchUnit() - 1;
            long batchNo = Math.min(total, end);
            boolean certainty = end < total - BlockConstants.MAX_FORK_CERTAINTY_BLOCK_NUMBER;
            BlockPrepareService.prepareTask(height, batchNo, certainty);
            if (!certainty) {
                BlockCheckService.checkForks(total);
            }
            BlockCheckService.checkTimeOut();
            BlockCheckService.processErrors();
        } catch (IOException e) {
            log.error("Job {}, exception occur in job processing: {}", shardingContext.getTaskId(), e.getMessage());
        }
    }

    private void buildClient() throws MalformedURLException, ConfigException {
        ChainClient chainClient;
        ChainInfo chainInfo = context.getChainInfo();
        StashInfo stashInfo = context.getStashInfo();
        if (stashInfo != null) {
            DataSource dataSource = DataSourceUtils.createDataSource(stashInfo.getJdbcUrl(),
                    null,
                    stashInfo.getUser(),
                    stashInfo.getPass());
            context.setStashDataSource(dataSource);
            chainClient = new StashClient();
            context.setClient(chainClient);
            return;
        }
        if (chainInfo.getRpcUrl() != null) {
            chainClient = new RpcHttpClient();
        } else {
            chainClient = new ChannelClient();
        }
        context.setClient(chainClient);
    }

}
