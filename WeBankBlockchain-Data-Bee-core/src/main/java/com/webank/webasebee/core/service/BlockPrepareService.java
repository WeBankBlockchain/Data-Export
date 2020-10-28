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
package com.webank.webasebee.core.service;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import org.fisco.bcos.sdk.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.webank.webasebee.common.constants.BlockConstants;
import com.webank.webasebee.common.enums.BlockCertaintyEnum;
import com.webank.webasebee.common.enums.TxInfoStatusEnum;
import com.webank.webasebee.db.entity.BlockTaskPool;
import com.webank.webasebee.db.repository.BlockTaskPoolRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * BlockPrepareService
 *
 * @Description: BlockPrepareService
 * @author maojiayu
 * @data Jul 5, 2019 3:32:19 PM
 *
 */
@Service
@Slf4j
public class BlockPrepareService {

    @Autowired
    private BlockTaskPoolRepository blockTaskPoolRepository;
    @Autowired
    private Client client;

    public long getTaskPoolHeight() {
        Optional<BlockTaskPool> item = blockTaskPoolRepository.findTopByOrderByBlockHeightDesc();
        long height = 0;
        if (item.isPresent()) {
            height = item.get().getBlockHeight() + 1;
        }
        return height;
    }

    public long getCurrentBlockHeight() throws IOException {
        BigInteger blockNumber = client.getBlockNumber().getBlockNumber();
        long total = blockNumber.longValue();
        log.debug("Current chain block number is:{}", blockNumber);
        return total;
    }

    @Transactional
    public void prepareTask(long begin, long end, boolean certainty) {
        log.info("Begin to prepare sync blocks from {} to {}", begin, end);
        List<BlockTaskPool> list = Lists.newArrayList();
        for (long i = begin; i <= end; i++) {
            BlockTaskPool pool =
                    new BlockTaskPool().setBlockHeight(i).setSyncStatus((short) TxInfoStatusEnum.INIT.getStatus());
            if (certainty) {
                pool.setCertainty((short) BlockCertaintyEnum.FIXED.getCertainty());
            } else {
                if (i <= end - BlockConstants.MAX_FORK_CERTAINTY_BLOCK_NUMBER) {
                    pool.setCertainty((short) BlockCertaintyEnum.FIXED.getCertainty());
                } else {
                    pool.setCertainty((short) BlockCertaintyEnum.UNCERTAIN.getCertainty());
                }
            }
            list.add(pool);
        }
        blockTaskPoolRepository.saveAll(list);
        log.info("Sync blocks from {} to {} are prepared.", begin, end);
    }

}
