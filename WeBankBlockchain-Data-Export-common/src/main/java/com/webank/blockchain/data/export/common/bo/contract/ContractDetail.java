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
package com.webank.blockchain.data.export.common.bo.contract;

import com.webank.blockchain.data.export.common.bo.data.ContractInfoBO;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * ContractMethodInfo
 *
 * @Description: ContractMethodInfo storages all contracts info for parsing method data.
 * @author graysonzhang
 * @data 2018-12-17 11:48:31
 *
 */
@Data
@Accessors(chain = true)
public class ContractDetail {

	private ContractInfoBO contractInfoBO;
	
	private List<MethodMetaInfo> methodMetaInfos;
	
	private List<EventMetaInfo> eventMetaInfos;
	
}
