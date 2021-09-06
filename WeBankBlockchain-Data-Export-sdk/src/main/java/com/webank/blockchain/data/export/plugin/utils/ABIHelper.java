package com.webank.blockchain.data.export.plugin.utils;

import org.fisco.bcos.sdk.abi.wrapper.ABIDefinition;
import org.fisco.bcos.sdk.abi.wrapper.ContractABIDefinition;
import org.fisco.bcos.sdk.crypto.CryptoSuite;
import org.fisco.bcos.sdk.utils.Numeric;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author aaronchu
 * @Description
 * @date 2021/09/03
 */
public class ABIHelper {

    public static String resolveEventTopic(CryptoSuite cryptoSuite, ABIDefinition abiDefinition){
        return Numeric.toHexString(cryptoSuite.hash(abiDefinition.getMethodSignatureAsString().getBytes()));
    }

    public static Map<String, ABIDefinition> resolveEventTopicsToEvents(CryptoSuite cryptoSuite, ContractABIDefinition contractABIDefinition){
        Map<String, ABIDefinition> result = new HashMap<>();
        for(ABIDefinition abiDefinition:contractABIDefinition.getEventTopicToEvents().values()){
            String topic = resolveEventTopic(cryptoSuite, abiDefinition);
            result.put(topic, abiDefinition);
        }
        return result;
    }


}
