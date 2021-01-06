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
package com.webank.blockchain.data.export.parser.contract;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import com.webank.blockchain.data.export.common.bo.contract.ContractMapsInfo;
import com.webank.blockchain.data.export.common.bo.contract.FieldVO;
import com.webank.blockchain.data.export.common.bo.contract.MethodMetaInfo;
import com.webank.blockchain.data.export.common.constants.AbiTypeConstants;
import com.webank.blockchain.data.export.common.constants.ContractConstants;
import com.webank.blockchain.data.export.common.entity.DataExportContext;
import com.webank.blockchain.data.export.common.entity.ExportConstant;
import com.webank.blockchain.data.export.parser.tools.ABIUtils;
import com.webank.blockchain.data.export.parser.tools.SolJavaTypeMappingUtils;
import com.webank.blockchain.data.export.parser.tools.SolSqlTypeMappingUtils;
import com.webank.blockchain.data.export.parser.tools.SolTypeMethodMappingUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.fisco.bcos.sdk.abi.wrapper.ABIDefinition;
import org.fisco.bcos.sdk.abi.wrapper.ABIDefinition.NamedType;

import java.util.ArrayList;
import java.util.List;

/**
 * MethodParser can parse contract files of java to standard method data structures.
 *
 * @Description: MethodParser
 * @author graysonzhang
 * @author maojiayu
 * @data 2018-12-4 23:04:50
 *
 */
@Slf4j
public class MethodParser {

    public static List<MethodMetaInfo> parseToInfoList(String abiStr, String contractName) {
        ABIDefinition[] abiDefinitions = ABIUtils.getContractAbiList(abiStr);
        if (ArrayUtil.isEmpty(abiDefinitions)) {
            return null;
        }
        List<MethodMetaInfo> lists = Lists.newArrayList();
        for (ABIDefinition abiDefinition : abiDefinitions) {
            String abiType = abiDefinition.getType();
            if (abiType.equals(AbiTypeConstants.ABI_EVENT_TYPE) || abiDefinition.isConstant()) {
                continue;
            }
            List<NamedType> inputs = abiDefinition.getInputs();
            if (CollectionUtil.isEmpty(inputs) || StringUtils.isEmpty(inputs.get(0).getName())) {
                continue;
            }
            List<NamedType> outputs = abiDefinition.getOutputs();
            MethodMetaInfo method = new MethodMetaInfo();
            method.setType("method").setContractName(contractName);
            log.debug("method name : {}", abiDefinition.getName());
            if (abiType.equals(AbiTypeConstants.ABI_CONSTRUCTOR_TYPE)) {
                method.setMethodName("constructor");
            } else {
                method.setMethodName(abiDefinition.getName());
            }
            method.setMethodId(abiDefinition.getMethodId(ExportConstant.threadLocal.get().getClient().getCryptoSuite())
                    + "_" + contractName);
            method.setFieldsList(getFieldList(inputs))
                    .setOutputList(getOutputList(outputs));
            lists.add(method);
        }
        return lists;
    }

    public static List<FieldVO> getOutputList(List<NamedType> outputs) {
        if (CollectionUtil.isEmpty(outputs)) {
            return new ArrayList<>();
        }
        List<FieldVO> list = Lists.newArrayListWithExpectedSize(outputs.size());
        for (int i = 0; i < outputs.size(); i++) {
            String javaName = "output" + (i + 1);
            String solType = outputs.get(i).getType();
            String sqlName = StrUtil.toUnderlineCase(javaName);
            FieldVO vo = new FieldVO();
            vo.setJavaName(javaName).setJavaCapName(StringUtils.capitalize(javaName)).setSqlName(sqlName)
                    .setSqlType(SolSqlTypeMappingUtils.fromSolBasicTypeToSqlType(solType)).setSolidityType(solType)
                    .setJavaType(SolJavaTypeMappingUtils.fromSolBasicTypeToJavaType(solType))
                    .setTypeMethod(SolTypeMethodMappingUtils.fromSolBasicTypeToTypeMethod(solType));
            list.add(vo);
        }
        return list;
    }

    public static List<FieldVO> getFieldList(List<NamedType> inputs) {
        ArrayList<FieldVO> fieldList = Lists.newArrayList();
        for (NamedType namedType : inputs) {
            FieldVO vo = new FieldVO();
            String solName = namedType.getName();
            // 增加is前缀变量的特殊处理
            if (StringUtils.startsWith(solName, "is") && solName.length() > 2
                    && Character.isUpperCase(solName.charAt(2))) {
                solName = StringUtils.uncapitalize(StringUtils.substring(solName, 2));
            }
            String solType = namedType.getType();
            String sqlName = StrUtil.toUnderlineCase(solName);
            vo.setSolidityName(solName).setSqlName(sqlName).setJavaName(solName)
                    .setSqlType(SolSqlTypeMappingUtils.fromSolBasicTypeToSqlType(solType)).setSolidityType(solType)
                    .setJavaType(SolJavaTypeMappingUtils.fromSolBasicTypeToJavaType(solType))
                    .setTypeMethod(SolTypeMethodMappingUtils.fromSolBasicTypeToTypeMethod(solType))
                    .setJavaCapName(StringUtils.capitalize(solName));
            log.debug("java name {}, java type {}, solidity type {}, type method {}", vo.getJavaName(),
                    vo.getJavaType(), vo.getSolidityType(), vo.getTypeMethod());
            fieldList.add(vo);
        }
        return fieldList;
    }

    
}
