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
package com.webank.blockchain.data.export.parser.service;

import cn.hutool.core.util.HexUtil;
import com.webank.blockchain.data.export.common.bo.contract.ContractDetail;
import com.webank.blockchain.data.export.common.bo.contract.ContractMapsInfo;
import com.webank.blockchain.data.export.common.constants.BinConstant;
import com.webank.blockchain.data.export.common.constants.ContractConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.fisco.bcos.sdk.utils.Hex;

import java.math.BigInteger;
import java.util.Map;

/**
 * ContractConstructorService for querying contract constructor name by input.
 *
 * @Description: ContractConstructorService
 * @author graysonzhang
 * @data 2018-12-19 18:16:59
 *
 */
@Slf4j
public class ContractConstructorService {

    /**
     * get constuctor name by transaction input code.
     *
     * @param code
     * @return key:contract binary, value:contract name
     */
    public static Map.Entry<String, ContractDetail> getConstructorNameByCode(String code) {
        ContractMapsInfo contractMapsInfo = ContractConstants.getCurrentContractMaps();
        if (contractMapsInfo == null) {
            return null;
        }
        Map<String, ContractDetail> binaryMap = contractMapsInfo.getContractBinaryMap();
        for (Map.Entry<String, ContractDetail> entry : binaryMap.entrySet()) {
            String key = entry.getKey();

            if (code.length() > BinConstant.META_DATA_HASH_LENGTH
                    && key.length() > BinConstant.META_DATA_HASH_LENGTH) {
                String hashLengthStr = code.substring(code.length() - 4);
                if ("0029".equals(hashLengthStr)){
                    code = code.substring(2, code.length() - 68);
                }
                if ("0037".equals(hashLengthStr)){
                    code = code.substring(2, code.length() - 114);
                }
                if (code.contains("a264697066735822")){
                    code = code.substring(2, code.indexOf("a264697066735822"));
                }
            } else {
                continue;
            }
            if (StringUtils.containsIgnoreCase(key, code)) {
                return entry;
            }
        }
        return null;
    }

}
