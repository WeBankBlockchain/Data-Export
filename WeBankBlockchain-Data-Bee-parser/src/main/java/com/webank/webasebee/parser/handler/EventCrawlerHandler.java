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
package com.webank.webasebee.parser.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.fisco.bcos.sdk.client.protocol.model.JsonTransactionResponse;
import org.fisco.bcos.sdk.client.protocol.response.BcosBlock.Block;
import org.fisco.bcos.sdk.client.protocol.response.BcosBlock.TransactionObject;
import org.fisco.bcos.sdk.client.protocol.response.BcosBlock.TransactionResult;
import org.fisco.bcos.sdk.client.protocol.response.BcosTransactionReceipt;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.webank.webasebee.common.bo.data.EventBO;
import com.webank.webasebee.common.constants.ContractConstants;
import com.webank.webasebee.common.tools.DateUtils;
import com.webank.webasebee.extractor.ods.EthClient;
import com.webank.webasebee.parser.crawler.face.BcosEventCrawlerInterface;

import lombok.extern.slf4j.Slf4j;

/**
 * EventCrawlerHandler
 *
 * @Description: EventCrawlerHandler
 * @author maojiayu
 * @data Jul 3, 2019 10:00:38 AM
 *
 */
@Service
@Slf4j
public class EventCrawlerHandler {
    @Autowired
    private EthClient ethClient;
    @Autowired
    private Map<String, BcosEventCrawlerInterface> bcosEventCrawlerMap;

    @SuppressWarnings({ "rawtypes", "unused" })
    public List<EventBO> crawl(Block block, Map<String, String> txHashContractNameMapping) throws IOException {
        List<EventBO> boList = new ArrayList<>();
        List<TransactionResult> transactionResults = block.getTransactions();
        for (TransactionResult result : transactionResults) {
            TransactionObject to = (TransactionObject) result;
            JsonTransactionResponse transaction = to.get();
            BcosTransactionReceipt bcosTransactionReceipt = ethClient.getTransactionReceipt(transaction.getHash());
            Optional<TransactionReceipt> opt = bcosTransactionReceipt.getTransactionReceipt();
            if (opt.isPresent()) {
                TransactionReceipt tr = opt.get();
                String contractName = txHashContractNameMapping.get(transaction.getHash());
                if (transaction.getTo() != null && !transaction.getTo().equals(ContractConstants.EMPTY_ADDRESS)) {
                    tr.setContractAddress(transaction.getTo());
                }

                if (ContractConstants.EXPORT_INNER_CALL_EVENT == false && StringUtils.isEmpty(contractName)) {
                    log.error("TxHash {} is Empty, and the blockNumber is {}! Please check it. ",
                            tr.getTransactionHash(), block.getNumber());
                    continue;
                }
                bcosEventCrawlerMap.forEach((k, v) -> {
                    if (ContractConstants.EXPORT_INNER_CALL_EVENT == false
                            && !StringUtils.startsWithIgnoreCase(k, contractName)) {
                        return;
                    }
                    boList.addAll(v.handleReceipt(tr, DateUtils.hexStrToDate(block.getTimestamp())));
                });
            }
        }
        return boList;
    }

}
