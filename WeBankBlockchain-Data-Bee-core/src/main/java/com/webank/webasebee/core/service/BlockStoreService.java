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

import com.webank.webasebee.common.bo.contract.ContractMapsInfo;
import com.webank.webasebee.common.bo.contract.ContractDetail;
import com.webank.webasebee.common.bo.data.BlockInfoBO;
import com.webank.webasebee.common.bo.data.ContractInfoBO;
import com.webank.webasebee.db.service.DataStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

/**
 * BlockStoreService
 *
 * @Description: BlockStoreService
 * @author maojiayu
 * @data Jul 5, 2019 4:09:23 PM
 *
 */
@Service
@DependsOn("contractParser")
public class BlockStoreService {

    @Autowired
    private List<DataStoreService> dataStoreServiceList;
    @Autowired
    private ContractMapsInfo contractMapsInfo;

    public void store(BlockInfoBO blockInfo) {
        for (DataStoreService dataStoreService : dataStoreServiceList) {
            dataStoreService.storeBlockInfoBO(blockInfo);
        }
    }

    @PostConstruct
    public void saveContractInfo() {
        Map<String, ContractDetail> contractMethodInfoMap =  contractMapsInfo.getContractBinaryMap();
        for (Map.Entry<String, ContractDetail> entry : contractMethodInfoMap.entrySet()){
            ContractInfoBO contractInfoBO = entry.getValue().getContractInfoBO();
            for (DataStoreService storeService : dataStoreServiceList) {
                storeService.storeContractInfo(contractInfoBO);
            }
        }
    }

}
