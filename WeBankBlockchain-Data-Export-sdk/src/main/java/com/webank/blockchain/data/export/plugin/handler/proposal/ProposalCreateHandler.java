package com.webank.blockchain.data.export.plugin.handler.proposal;

import com.webank.blockchain.data.export.common.tools.JacksonUtils;
import com.webank.blockchain.data.export.plugin.handler.EventHandlerInterface;
import com.webank.blockchain.data.export.plugin.model.DecodedEvent;
import com.webank.blockchain.data.export.plugin.utils.ABIHelper;
import com.webank.blockchain.data.export.plugin.utils.DecoderHelper;
import org.fisco.bcos.sdk.abi.wrapper.ABIDefinition;
import org.fisco.bcos.sdk.abi.wrapper.ContractABIDefinition;
import org.fisco.bcos.sdk.crypto.CryptoSuite;
import org.fisco.bcos.sdk.model.TransactionReceipt;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Map;

/**
 * @author aaronchu
 * @Description
 * @date 2021/09/03
 */
public class ProposalCreateHandler implements EventHandlerInterface<TransactionReceipt.Logs> {

    private CryptoSuite cryptoSuite;
    private Map<String, ABIDefinition> topicToEvents;

    public ProposalCreateHandler(Map<String, ABIDefinition> topicToEvents,CryptoSuite cryptoSuite){
        this.topicToEvents = topicToEvents;
        this.cryptoSuite = cryptoSuite;
    }

    public ProposalCreateHandler(ContractABIDefinition contractAbiDef, CryptoSuite cryptoSuite){
        this.cryptoSuite = cryptoSuite;
        this.topicToEvents = ABIHelper.resolveEventTopicsToEvents(cryptoSuite, contractAbiDef);
    }

    @Override
    public void handleEvent(TransactionReceipt.Logs log) {
        //1. 解析字段。indexed信息从topics获取，非indexed字段从data获取
        String topic = log.getTopics().get(0);
        ABIDefinition eventAbi = this.topicToEvents.get(topic);
        if(eventAbi == null) return;
        Map<String, Object> result = DecoderHelper.decodeEvent(log, eventAbi);
        //2. 字段信息入库，插入数据表，或者更新数据表等
        doLog("事件名："+eventAbi.getName());
        doLog("事件原文：");
        doLog(JacksonUtils.toJson(log));
        doLog("数据：");
        for(Map.Entry<String, Object> e: result.entrySet()){
            doLog("key:"+e.getKey());
            doLog("value:"+e.getValue().toString());
        }
    }

    private static void doLog(String o) {
        try(BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new BufferedOutputStream(
                new FileOutputStream("C:\\Users\\aaronchu\\Desktop\\logs.log")
        )))){
            bw.write(o.toString());
            bw.newLine();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
