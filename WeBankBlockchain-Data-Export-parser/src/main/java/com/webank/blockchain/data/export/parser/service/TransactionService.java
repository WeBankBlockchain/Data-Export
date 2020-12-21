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
package com.webank.blockchain.data.export.parser.service;

import com.webank.blockchain.data.export.common.bo.contract.MethodMetaInfo;
import com.webank.blockchain.data.export.common.constants.ContractConstants;
import com.webank.blockchain.data.export.common.tools.JacksonUtils;
import lombok.extern.slf4j.Slf4j;
import org.fisco.bcos.sdk.client.protocol.model.JsonTransactionResponse;

import java.util.Map;

/**
 * TransactionService
 *
 * @Description: TransactionService
 * @author maojiayu
 * @data Jul 23, 2019 10:15:46 AM
 *
 */
@Slf4j
public class TransactionService {

    public static String getContractAddressByTransaction(JsonTransactionResponse transaction,
            Map<String, String> txHashContractAddressMapping) {
        log.debug("blocknumber: {} , to: {}, map: {}", transaction.getBlockNumber(), transaction.getTo(),
                JacksonUtils.toJson(txHashContractAddressMapping));
        if (transaction.getTo() == null || transaction.getTo().equals(ContractConstants.EMPTY_ADDRESS)) {
            return txHashContractAddressMapping.get(transaction.getHash());
        } else {
            return transaction.getTo();
        }
    }


    public static MethodMetaInfo getMethodMetaInfo(JsonTransactionResponse transaction, String contractName) {
        if (transaction.getTo() == null || transaction.getTo().equals(ContractConstants.EMPTY_ADDRESS)) {
            MethodMetaInfo methodMetaInfo = new MethodMetaInfo();
            methodMetaInfo.setContractName(contractName).setMethodName("constructor");
            return methodMetaInfo;
        }
        return null;
    }
}
