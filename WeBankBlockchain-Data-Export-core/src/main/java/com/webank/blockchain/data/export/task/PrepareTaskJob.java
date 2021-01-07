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

import java.io.IOException;
import java.math.BigInteger;

import com.webank.blockchain.data.export.common.entity.ExportConstant;
import com.webank.blockchain.data.export.service.BlockCheckService;
import com.webank.blockchain.data.export.service.BlockIndexService;
import com.webank.blockchain.data.export.service.BlockPrepareService;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.simple.job.SimpleJob;

import com.webank.blockchain.data.export.common.constants.BlockConstants;

import lombok.extern.slf4j.Slf4j;

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

    @Override
    public void execute(ShardingContext shardingContext) {
        long startBlockNumber = 0;
        try {
            startBlockNumber = BlockIndexService.getStartBlockIndex();
            log.info("Start succeed, and the block number is {}", startBlockNumber);
        } catch (Exception e) {
            log.error("depot Error, {}", e.getMessage());
        }
        try {
            BigInteger blockNumber = ExportConstant.getCurrentContext().getClient()
                    .getBlockNumber().getBlockNumber();
            long total = blockNumber.longValue();
            log.info("Current chain block number is:{}", total);
            long height = BlockPrepareService.getTaskPoolHeight();
            height = Math.max(height, startBlockNumber);
            long end = height + ExportConstant.getCurrentContext().getConfig().getCrawlBatchUnit() - 1;
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

}
