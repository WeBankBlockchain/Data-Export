package com.webank.blockchain.data.export.service;

import com.webank.blockchain.data.export.common.bo.data.*;
import com.webank.blockchain.data.export.common.entity.ExportConstant;
import com.webank.blockchain.data.export.common.subscribe.TopicRegistry;
import com.webank.blockchain.data.export.db.entity.TxRawData;

/**
 * @author aaronchu
 * @Description
 * @date 2021/08/31
 */
public class BlockListenerService {

    public static void onBlock(BlockInfoBO blockInfoBO){
        TopicRegistry subscribers = ExportConstant.getCurrentContext().getTopicRegistry();
        if(subscribers == null) return;
        if(subscribers.getBlockTopic() != null){
            subscribers.getBlockTopic().publish(blockInfoBO, null);
        }

        if(subscribers.getTxTopic() != null && blockInfoBO.getTxRawDataBOList() != null){
            for(TxRawDataBO rawData: blockInfoBO.getTxRawDataBOList()){
                subscribers.getTxTopic().publish(rawData, blockInfoBO);
            }
        }


        if(subscribers.getTxReceiptTopic() != null && blockInfoBO.getTxReceiptRawDataBOList() != null){
            for(TxReceiptRawDataBO rawData: blockInfoBO.getTxReceiptRawDataBOList()){
                subscribers.getTxReceiptTopic().publish(rawData, blockInfoBO);
            }
        }

        if(subscribers.getMethodTopic() != null && blockInfoBO.getMethodInfoList() != null){
            for(MethodBO methodBO:blockInfoBO.getMethodInfoList()){
                subscribers.getMethodTopic().publish(methodBO, blockInfoBO);
            }
        }

        if(subscribers.getEventTopic() != null && blockInfoBO.getEventInfoList() != null){
            for(EventBO eventBO:blockInfoBO.getEventInfoList()){
                subscribers.getEventTopic().publish(eventBO, blockInfoBO);
            }
        }
    }

}
