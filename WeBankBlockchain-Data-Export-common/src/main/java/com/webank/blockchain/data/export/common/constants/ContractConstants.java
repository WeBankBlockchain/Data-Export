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
package com.webank.blockchain.data.export.common.constants;

import com.webank.blockchain.data.export.common.bo.contract.ContractMapsInfo;

/**
 * ContractConstants
 *
 * @Description: ContractConstants
 * @author maojiayu
 * @data Jul 23, 2019 10:19:34 AM
 *
 */
public class ContractConstants {

    public static final String EMPTY_ADDRESS = "0x0000000000000000000000000000000000000000";

    public final static ThreadLocal<ContractMapsInfo> contractMapsInfo = new ThreadLocal<>();


    public static ContractMapsInfo getCurrentContractMaps() {
        return contractMapsInfo.get();
    }

    public static void setCurrentContractMaps(ContractMapsInfo maps) {
        contractMapsInfo.set(maps);
    }

}
