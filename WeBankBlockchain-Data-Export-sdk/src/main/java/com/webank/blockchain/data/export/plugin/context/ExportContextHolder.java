package com.webank.blockchain.data.export.plugin.context;

import com.webank.blockchain.data.export.plugin.enums.ContractEnum;
import org.fisco.bcos.sdk.abi.wrapper.ABIDefinition;

import java.util.Map;

/**
 * @author aaronchu
 * @Description
 * @date 2021/09/03
 */
public class ExportContextHolder {

    private static ThreadLocal<Map<ContractEnum, Map<String, ABIDefinition>>> holder
            = new ThreadLocal<>();

    public static void addContractAbi(ContractEnum contract, String contractABI){

    }

    public static void getABIDefinition(ContractEnum contract, String topic){

    }
}
