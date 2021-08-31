package com.webank.blockchain.data.export.service;

import com.webank.blockchain.data.export.common.bo.data.*;
import com.webank.blockchain.data.export.common.entity.ExportConstant;
import com.webank.blockchain.data.export.common.listener.BlockListeners;
import com.webank.blockchain.data.export.db.entity.TxRawData;

/**
 * @author aaronchu
 * @Description
 * @date 2021/08/31
 */
public class BlockListenerService {

    public static void onBlock(BlockInfoBO blockInfoBO){
        BlockListeners subscribers = ExportConstant.getCurrentContext().getBlockListeners();
        if(subscribers == null) return;
        if(subscribers.getBlockInfoListener() != null){
            subscribers.getBlockInfoListener().handleOnMatch(blockInfoBO);
        }

        if(subscribers.getTransactionListener() != null && blockInfoBO.getTxRawDataBOList() != null){
            for(TxRawDataBO rawData: blockInfoBO.getTxRawDataBOList()){
                subscribers.getTransactionListener().handleOnMatch(rawData);
            }
        }


        if(subscribers.getTransactionReceiptListener() != null && blockInfoBO.getTxReceiptRawDataBOList() != null){
            for(TxReceiptRawDataBO rawData: blockInfoBO.getTxReceiptRawDataBOList()){
                subscribers.getTransactionReceiptListener().handleOnMatch(rawData);
            }
        }

        if(subscribers.getMethodListener() != null && blockInfoBO.getMethodInfoList() != null){
            for(MethodBO methodBO:blockInfoBO.getMethodInfoList()){
                subscribers.getMethodListener().handleOnMatch(methodBO);
            }
        }

        if(subscribers.getEventListener() != null && blockInfoBO.getEventInfoList() != null){
            for(EventBO eventBO:blockInfoBO.getEventInfoList()){
                subscribers.getEventListener().handleOnMatch(eventBO);
            }
        }
    }

}
