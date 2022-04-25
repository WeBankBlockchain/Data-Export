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
package com.webank.blockchain.data.export.common.tools;

import com.webank.blockchain.data.export.common.client.ChainClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.fisco.bcos.sdk.v3.codec.abi.tools.ContractAbiUtil;
import org.fisco.bcos.sdk.v3.codec.wrapper.ABIDefinition;
import org.fisco.bcos.sdk.v3.codec.wrapper.ABIDefinitionFactory;
import org.fisco.bcos.sdk.v3.codec.wrapper.ABIObject;
import org.fisco.bcos.sdk.v3.codec.wrapper.ABIObjectFactory;
import org.fisco.bcos.sdk.v3.codec.wrapper.ContractABIDefinition;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.utils.Numeric;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * MethodUtils
 *
 * @Description: MethodUtils
 * @author graysonzhang
 * @data 2018-12-10 15:12:38
 *
 */
@Slf4j
public class MethodUtils {

    /**
     * Get contract binary.
     * 
     * @param clazz
     * @return String
     */
    public static String getClassField(Class<?> clazz, String fieldName) {
        String binary = null;
        try {
            Field field = clazz.getDeclaredField(fieldName);
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            binary = (String) field.get(fieldName);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            log.error("Read {} error: {}", fieldName, e.getMessage());
        }
        return binary;
    }

    /**
     * Get contract abi definitions.
     * 
     * @param clazz
     * @return AbiDefinition[]
     */
    public static List<ABIDefinition> getContractAbiList(Class<?> clazz) {
        String abi = null;
        try {
            Field field = clazz.getDeclaredField("ABI");
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            abi = (String) field.get("ABI");
        } catch (NoSuchFieldException | SecurityException e) {
            log.error("{}", e.getMessage());
        } catch (IllegalArgumentException | IllegalAccessException e) {
            log.error("{}", e.getMessage());
        }
        return ContractAbiUtil.getFuncABIDefinition(abi);
    }

    @SuppressWarnings("static-access")
    public static List<Object> decodeMethodInput(String ABI, String methodName, TransactionReceipt tr, ChainClient client)
            throws Exception {
        ABIDefinitionFactory abiDefinitionFactory = new ABIDefinitionFactory(client.getCryptoSuite());
        ContractABIDefinition contractABIDefinition = abiDefinitionFactory.loadABI(ABI);
        List<ABIDefinition> methods;
        ABIObjectFactory abiObjectFactory = new ABIObjectFactory();
        if (StringUtils.equals(methodName, "constructor")) {
            String code = client.getCode(tr.getContractAddress());
            String lastCode = StringUtils.substring(code, code.length() - 32, code.length());
            String paramsInput = StringUtils.substringAfter(tr.getInput(), lastCode);
            // remove methodId of input
            return decodeJavaObject(
                    abiObjectFactory.createInputObject(contractABIDefinition.getConstructor()), paramsInput);
        } else {
            methods = contractABIDefinition.getFunctions().get(methodName);
        }
        for (ABIDefinition abiDefinition : methods) {
            ABIObject outputABIObject = abiObjectFactory.createInputObject(abiDefinition);
            try {
                return decodeJavaObject(outputABIObject, tr.getInput().substring(10));
            } catch (Exception e) {
                log.warn(" exception in decodeMethodToObject : {}", e.getMessage());
            }
        }
        String errorMsg = " cannot decode in decodeMethodToObject with appropriate interface ABI";
        log.error(errorMsg);
        throw new Exception(errorMsg);
    }

    public static List<Object> decodeJavaObject(ABIObject template, String input) throws ClassNotFoundException {

        input = Numeric.cleanHexPrefix(input);

        ABIObject abiObject = template.decode(Numeric.hexStringToByteArray(input),false);

        // ABIObject -> java List<Object>
        List<Object> result = decodeJavaObject(abiObject);

        return result;
    }

    private static List<Object> decodeJavaObject(ABIObject template) throws UnsupportedOperationException {
        List<Object> result = new ArrayList<Object>();
        List<ABIObject> argObjects;
        if (template.getType() == ABIObject.ObjectType.STRUCT) {
            argObjects = template.getStructFields();
        } else {
            argObjects = template.getListValues();
        }
        for (int i = 0; i < argObjects.size(); ++i) {
            ABIObject argObject = argObjects.get(i);
            switch (argObject.getType()) {
                case VALUE:
                {
                    switch (argObject.getValueType()) {
                        case BOOL:
                        {
                            result.add(argObject.getBoolValue().getValue());
                            break;
                        }
                        case UINT:
                        case INT:
                        {
                            result.add(argObject.getNumericValue().getValue());
                            break;
                        }
                        case ADDRESS:
                        {
                            result.add(argObject.getAddressValue().toString());
                            break;
                        }
                        case BYTES:
                        {
                            result.add(new String(argObject.getBytesValue().getValue()));
                            break;
                        }
                        case DBYTES:
                        {
                            result.add(
                                    new String(
                                            argObject.getDynamicBytesValue().getValue()));
                            break;
                        }
                        case STRING:
                        {
                            result.add(argObject.getStringValue().toString());
                            break;
                        }
                        default:
                        {
                            throw new UnsupportedOperationException(
                                    " Unsupported valueType: " + argObject.getValueType());
                        }
                    }
                    break;
                }
                case LIST:
                case STRUCT:
                {
                    List<Object> node = decodeJavaObject(argObject);
                    result.add(node);
                    break;
                }
                default:
                {
                    throw new UnsupportedOperationException(
                            " Unsupported objectType: " + argObject.getType());
                }
            }
        }

        return result;
    }

}
