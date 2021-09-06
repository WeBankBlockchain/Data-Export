package com.webank.blockchain.data.export.plugin.utils;

import com.webank.blockchain.data.export.plugin.model.DecodedEvent;
import org.fisco.bcos.sdk.abi.wrapper.ABIDefinition;
import org.fisco.bcos.sdk.abi.wrapper.ABIObject;
import org.fisco.bcos.sdk.abi.wrapper.ABIObjectFactory;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.utils.Numeric;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author aaronchu
 * @Description
 * @date 2021/09/06
 */
public class DecoderHelper {

    /**
     * 解析事件数据
     * @param log 原始事件日志
     * @param abiDefinition 事件ABI
     * @return 事件数据
     */
    public static Map<String, Object> decodeEvent(TransactionReceipt.Logs log, ABIDefinition abiDefinition){
        //1. Decode indexed fields
        List<ABIDefinition.NamedType> indexedFields = abiDefinition.getInputs().stream().filter(a->a.isIndexed()).collect(Collectors.toList());
        StringBuilder indexedDataBuilder = new StringBuilder();
        for(int i=1;i<log.getTopics().size();i++){
            indexedDataBuilder.append(Numeric.cleanHexPrefix(log.getTopics().get(i)));
        }
        String indexedData = indexedDataBuilder.toString();
        List<Object> indexedFieldValues = decode(indexedFields, indexedData);

        //2. Decode none indexed fields
        List<ABIDefinition.NamedType> nonIndexedFields = abiDefinition.getInputs().stream().filter(a->!a.isIndexed()).collect(Collectors.toList());
        List<Object> nonIndexedFieldValues = decode(nonIndexedFields, log.getData());
        //3. Combine
        Map<String, Object> result = new HashMap<>();
        for(int i=0;i<indexedFields.size();i++){
            String key = indexedFields.get(i).getName();
            Object value = indexedFieldValues.get(i);
            result.put(key, value);
        }
        for(int i=0;i<nonIndexedFields.size();i++){
            String key = nonIndexedFields.get(i).getName();
            Object value = nonIndexedFieldValues.get(i);
            result.put(key, value);
        }
        return result;
    }


    public static List<Object> decode(List<ABIDefinition.NamedType> fieldDefinitions, String input){
        //Prepare template
        ABIObject template =  new ABIObject(ABIObject.ObjectType.STRUCT);
        for(ABIDefinition.NamedType namedType: fieldDefinitions){
            template.getStructFields().add(ABIObjectFactory.buildTypeObject(namedType));
        }
        //Decode to complex solidity object
        ABIObject decoded = template.decode(input);
        return (List)convertABIObjectToJavaObjects(decoded);
    }

    private static Object convertABIObjectToJavaObjects(ABIObject abiObject){
        ABIObject.ObjectType objectType = abiObject.getType();
        //VALUE
        if(objectType == ABIObject.ObjectType.VALUE){
            ABIObject.ValueType valueType  = abiObject.getValueType();
            Function<ABIObject, Object> getter = valueGetters.get(valueType);
            if(getter == null){
                throw new UnsupportedOperationException("Unsupported value type "+abiObject.getValueType());
            }
            Object fieldValue = getter.apply(abiObject);
            return fieldValue;
        }
        List<Object> result = new ArrayList<>();
        List<ABIObject> subAbiObjects;
        //LIST
        if(objectType == ABIObject.ObjectType.LIST){
            subAbiObjects = abiObject.getListValues();
        }
        //STRUCT
        else if(objectType == ABIObject.ObjectType.STRUCT){
            subAbiObjects = abiObject.getStructFields();
        }
        else{
            throw new UnsupportedOperationException("Unsupported object type "+objectType);
        }
        List<Object> fieldValue = new ArrayList<>();
        for(ABIObject subAbiObject: subAbiObjects){
            fieldValue.add(convertABIObjectToJavaObjects(subAbiObject));
        }
        result.add(fieldValue);
        return result;
    }



    private static Map<ABIObject.ValueType, Function<ABIObject, Object>> valueGetters = new HashMap<>(){
        {
            put(ABIObject.ValueType.BOOL, a->a.getBoolValue().getValue());
            put(ABIObject.ValueType.UINT, a->a.getNumericValue().getValue());
            put(ABIObject.ValueType.INT, a->a.getNumericValue().getValue());
            put(ABIObject.ValueType.ADDRESS, a->a.getAddressValue().getValue().toLowerCase());
            put(ABIObject.ValueType.BYTES, a->a.getBytesValue().getValue());
            put(ABIObject.ValueType.DBYTES, a->a.getDynamicBytesValue().getValue());
            put(ABIObject.ValueType.STRING, a->a.getStringValue().getValue());
        }
    };
}
