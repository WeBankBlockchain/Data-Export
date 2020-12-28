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
package com.webank.blockchain.data.export.service;

import com.webank.blockchain.data.export.common.bo.data.BlockInfoBO;
import com.webank.blockchain.data.export.common.enums.TxInfoStatusEnum;
import com.webank.blockchain.data.export.db.entity.BlockTaskPool;
import com.webank.blockchain.data.export.task.DataExportExecutor;
import lombok.extern.slf4j.Slf4j;
import org.fisco.bcos.sdk.client.protocol.response.BcosBlock.Block;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * BlockSyncService
 *
 * @Description: BlockSyncService
 * @author maojiayu
 * @data Apr 1, 2019 5:02:39 PM
 *
 */
@Slf4j
public class BlockDepotService {

    public static List<Block> fetchData(int count) {
        List<BlockTaskPool> tasks = DataExportExecutor.crawler.get().getBlockTaskPoolRepository()
                .findBySyncStatusOrderByBlockHeightLimit((short) TxInfoStatusEnum.INIT.getStatus(), count);
        return getTasks(tasks);
    }

    public static List<Block> getTasks(List<BlockTaskPool> tasks) {
        List<Block> result = new ArrayList<>();
        List<BlockTaskPool> pools = new ArrayList<>();
        for (BlockTaskPool task : tasks) {
            task.setSyncStatus((short) TxInfoStatusEnum.DOING.getStatus()).setDepotUpdatetime(new Date());
            BigInteger bigBlockHeight = new BigInteger(Long.toString(task.getBlockHeight()));
            Block block;
            try {
                block = BlockCrawlService.getBlock(bigBlockHeight);
                result.add(block);
                pools.add(task);
            } catch (IOException e) {
                log.error("Block {},  exception occur in job processing: {}", task.getBlockHeight(), e.getMessage());
                DataExportExecutor.crawler.get().getBlockTaskPoolRepository()
                        .setSyncStatusByBlockHeight((short) TxInfoStatusEnum.ERROR.getStatus(),
                        new Date(), task.getBlockHeight());
            }
        }
        DataExportExecutor.crawler.get().getBlockTaskPoolRepository().saveAll(pools);
        log.info("Successful fetch {} Blocks.", result.size());
        return result;
    }

    public static void processDataSequence(List<Block> data, long total) {
        for (Block b : data) {
            process(b, total);
        }
    }

    public static void process(Block b, long total) {
        try {
            BlockInfoBO blockInfo = BlockCrawlService.parse(b);
            BlockStoreService.store(blockInfo);
            DataExportExecutor.crawler.get().getBlockTaskPoolRepository()
                    .setSyncStatusByBlockHeight((short) TxInfoStatusEnum.DONE.getStatus(), new Date(),
                    b.getNumber().longValue());
            log.info("Block {} of {} sync block succeed.", b.getNumber().longValue(), total);
        } catch (IOException e) {
            log.error("block {}, exception occur in job processing: {}", b.getNumber().longValue(), e.getMessage());
            DataExportExecutor.crawler.get().getBlockTaskPoolRepository()
                    .setSyncStatusByBlockHeight((short) TxInfoStatusEnum.ERROR.getStatus(), new Date(),
                    b.getNumber().longValue());
        }
    }

}
