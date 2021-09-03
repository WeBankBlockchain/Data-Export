package com.webank.blockchain.data.export.plugin;

import cn.hutool.json.JSON;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.webank.blockchain.data.export.common.bo.data.TxRawDataBO;
import com.webank.blockchain.data.export.common.bo.data.TxReceiptRawDataBO;
import com.webank.blockchain.data.export.common.subscribe.face.SubscriberInterface;
import com.webank.blockchain.data.export.common.tools.JacksonUtils;
import com.webank.blockchain.data.export.plugin.constants.Constants;
import com.webank.blockchain.data.export.plugin.enums.ContractEnum;
import com.webank.blockchain.data.export.plugin.handler.EventHandlerInterface;
import com.webank.blockchain.data.export.plugin.utils.JsonHelper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.fisco.bcos.sdk.abi.wrapper.ABIDefinition;
import org.fisco.bcos.sdk.model.TransactionReceipt;

import java.util.*;

/**
 * 专门拉取数据治理相关事件
 * @author aaronchu
 * @Description
 * @date 2021/09/03
 */
@Slf4j
public class DataGovTransactionHandler implements SubscriberInterface<TxReceiptRawDataBO> {
    //合约地址与合约名的映射
    private Map<String, ContractEnum> contractAddresses;
    //合约名+事件主题 与 事件处理器 的映射（需要纳入合约名，避免不同合约事件定义相同的情况）
    private Map<ContractEnum, Map<String, EventHandlerInterface>> eventHandlers;

    public DataGovTransactionHandler(Map<String, ContractEnum> contractAddresses,  Map<ContractEnum, Map<String, EventHandlerInterface>> eventHandlers){
        this.contractAddresses = contractAddresses;
        this.eventHandlers = eventHandlers;
    }

    @Override
    public boolean shouldProcess(TxReceiptRawDataBO receipt, Object context) {
        //是否和数据治理相关的主题
        if(!contractAddresses.containsKey(receipt.getTo())){
            return false;
        }
        //交易是否包含事件
        if(StringUtils.isBlank(receipt.getLogs())){
            return false;
        }
        return false;
    }

    @Override
    public void process(TxReceiptRawDataBO receipt) {
        //1. 反序列化成Logs类型
        List<TransactionReceipt.Logs> logs = JacksonUtils.fromJsonList(receipt.getLogs(), TransactionReceipt.Logs.class);
        //2. 针对每个Log(事件)，选择对应的处理器
        for(TransactionReceipt.Logs log: logs){
            ContractEnum contract = this.contractAddresses.get(log.getAddress());
            String eventTopic = log.getTopics().get(0);
            EventHandlerInterface eventHandler = fetchEventHandler(contract, eventTopic);
            eventHandler.handleEvent(log);
        }
    }

    private EventHandlerInterface fetchEventHandler(ContractEnum contract, String topic){
        if(contract == null || topic == null ) return null;
        Map<String, EventHandlerInterface> eventHandlerInterfaceMap =  this.eventHandlers.getOrDefault(contract, Collections.EMPTY_MAP);
        return eventHandlerInterfaceMap.get(topic);
    }


}
