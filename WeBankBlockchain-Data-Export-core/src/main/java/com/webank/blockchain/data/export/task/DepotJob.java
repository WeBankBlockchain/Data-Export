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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import cn.hutool.core.collection.CollectionUtil;
import com.webank.blockchain.data.export.common.entity.ExportConstant;
import com.webank.blockchain.data.export.db.entity.BlockTaskPool;
import com.webank.blockchain.data.export.service.BlockDepotService;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.dataflow.job.DataflowJob;
import org.fisco.bcos.sdk.client.protocol.response.BcosBlock.Block;

import com.webank.blockchain.data.export.common.enums.TxInfoStatusEnum;

import lombok.extern.slf4j.Slf4j;

/**
 * MyDataflowJob
 *
 * @Description: MyDataflowJob
 * @author maojiayu
 * @data Jan 10, 2019 12:03:37 PM
 *
 */
@Slf4j
public class DepotJob implements DataflowJob<Block> {

    @Override
    public List<Block> fetchData(ShardingContext shardingContext) {
        List<BlockTaskPool> tasks =
                DataExportExecutor.dataPersistenceManager.get().getBlockTaskPoolRepository()
                        .findBySyncStatusModByBlockHeightLimit(shardingContext.getShardingTotalCount(),
                        shardingContext.getShardingItem(), (short) TxInfoStatusEnum.INIT.getStatus(), 1);
        if (CollectionUtil.isEmpty(tasks)) {
            return new ArrayList<Block>();
        }
        return BlockDepotService.getTasks(tasks);
    }

    @Override
    public void processData(ShardingContext shardingContext, List<Block> data) {
        BigInteger blockNumber = ExportConstant.threadLocal.get().getClient()
                .getBlockNumber().getBlockNumber();
        BlockDepotService.processDataSequence(data, blockNumber.longValue());
    }

}