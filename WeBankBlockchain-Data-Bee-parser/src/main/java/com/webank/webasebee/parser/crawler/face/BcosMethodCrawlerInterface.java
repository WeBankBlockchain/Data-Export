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
package com.webank.webasebee.parser.crawler.face;

import java.util.Date;
import java.util.Map;

import org.fisco.bcos.sdk.client.protocol.model.JsonTransactionResponse;
import org.fisco.bcos.sdk.model.TransactionReceipt;

import com.webank.webasebee.common.bo.data.MethodBO;

/**
 * BcosMethodCrawlerInterface
 *
 * @Description: BcosMethodCrawlerInterface
 * @author graysonzhang
 * @data 2018-12-5 11:23:40
 *
 */
public interface BcosMethodCrawlerInterface {
    /**
     * Get method input data by parsing transaction object and storage method input data into db.
     * 
     * @param transaction
     * @param blockTimeStamp
     * @param entry
     * @param methodName
     * @param txHashContractAddressMapping (to get the constructor method address)
     * @return void
     */
    MethodBO transactionHandler(JsonTransactionResponse transaction, TransactionReceipt receipt, Date blockTimeStamp,
            String methodName);
}
