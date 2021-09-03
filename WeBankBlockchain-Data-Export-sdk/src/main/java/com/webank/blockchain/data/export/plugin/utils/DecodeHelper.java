package com.webank.blockchain.data.export.plugin.utils;

import org.fisco.bcos.sdk.abi.wrapper.ABIDefinition;
import org.fisco.bcos.sdk.model.TransactionReceipt;

import java.util.Map;

/**
 * @author aaronchu
 * @Description
 * @date 2021/09/03
 */
public class DecodeHelper {


    public static Map<String, Object> decodeEvents(TransactionReceipt.Logs logs, ABIDefinition abi){
        //indexed字段的解析：先根据topics中
        //非indexed字段的解析：从data中解析
    }
}
