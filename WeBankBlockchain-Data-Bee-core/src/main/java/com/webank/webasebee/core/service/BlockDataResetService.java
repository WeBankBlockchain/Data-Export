/*
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
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.webank.webasebee.common.enums.TxInfoStatusEnum;
import com.webank.webasebee.common.tools.ResponseUtils;
import com.webank.webasebee.common.vo.CommonResponse;
import com.webank.webasebee.db.entity.BlockTaskPool;
import com.webank.webasebee.db.repository.BlockTaskPoolRepository;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * BlockDataResetService
 *
 * @Description: BlockDataResetService
 * @author graysonzhang
 * @author maojiayu
 * @data 2019-03-21 14:51:50
 *
 */
@Service
@Slf4j
public class BlockDataResetService {
    @Autowired
    private RollBackService rollBackService;
    @Autowired
    private BlockCrawlService singleBlockCrawlerService;
    @Autowired
    private BlockTaskPoolRepository blockTaskPoolRepository;

    public CommonResponse resetBlockDataByBlockId(long blockHeight) throws IOException {

        Optional<BlockTaskPool> blockTaskPool = blockTaskPoolRepository.findByBlockHeight(blockHeight);
        if (!blockTaskPool.isPresent()) {
            return CommonResponse.NOBLOCK;
        }
        if (blockTaskPool.get().getSyncStatus() == TxInfoStatusEnum.DOING.getStatus()) {
            return ResponseUtils.error("Some task is still running. please resend the request later.");
        }
        if (blockTaskPool.get().getSyncStatus() == TxInfoStatusEnum.RESET.getStatus()) {
            if (DateUtil.between(blockTaskPool.get().getDepotUpdatetime(), DateUtil.date(), DateUnit.SECOND) < 60) {
                return ResponseUtils.error("The block is already in progress to reset. please send the request later");
            }
        }
        log.info("begin to refetch block {}", blockHeight);
        blockTaskPoolRepository.setSyncStatusByBlockHeight((short) TxInfoStatusEnum.RESET.getStatus(), new Date(),
                blockHeight);
        rollBackService.rollback(blockHeight, blockHeight + 1);
        singleBlockCrawlerService.parse(blockHeight);
        blockTaskPoolRepository.setSyncStatusByBlockHeight((short) TxInfoStatusEnum.DONE.getStatus(), new Date(),
                blockHeight);
        log.info("block {} is reset!", blockHeight);
        return ResponseUtils.success();
    }
}
