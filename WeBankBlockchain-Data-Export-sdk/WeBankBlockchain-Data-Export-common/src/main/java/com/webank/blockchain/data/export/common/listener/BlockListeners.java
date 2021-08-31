package com.webank.blockchain.data.export.common.listener;

import lombok.Data;

/**
 * @author aaronchu
 * @Description
 * @date 2021/08/31
 */
@Data
public class BlockListeners {

    private BlockInfoListener blockInfoListener = new BlockInfoListener();
    private TransactionListener transactionListener = new TransactionListener();
    private TransactionReceiptListener transactionReceiptListener = new TransactionReceiptListener();
    private MethodListener methodListener = new MethodListener();
    private EventListener eventListener = new EventListener();

}
