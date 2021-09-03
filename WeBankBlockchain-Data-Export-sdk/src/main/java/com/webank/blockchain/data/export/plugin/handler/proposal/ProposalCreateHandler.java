package com.webank.blockchain.data.export.plugin.handler.proposal;

import com.webank.blockchain.data.export.plugin.handler.EventHandlerInterface;
import org.fisco.bcos.sdk.model.TransactionReceipt;

/**
 * @author aaronchu
 * @Description
 * @date 2021/09/03
 */
public class ProposalCreateHandler implements EventHandlerInterface<TransactionReceipt.Logs> {

    @Override
    public void handleEvent(TransactionReceipt.Logs o) {
        //1. 解析字段。indexed信息从topics获取，非indexed字段从data获取
        //2. 字段信息入库，插入数据表，或者更新数据表等
    }
}
