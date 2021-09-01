package com.webank.blockchain.data.export.common.subscribe;

import com.webank.blockchain.data.export.common.bo.data.*;
import com.webank.blockchain.data.export.common.subscribe.face.MsgTopicInterface;
import lombok.Data;

/**
 * @author aaronchu
 * @Description
 * @date 2021/09/01
 */
@Data
public class TopicRegistry {

    private MsgTopicInterface<BlockInfoBO> blockTopic = new DefaultMsgTopic<>();

    private MsgTopicInterface<TxRawDataBO> txTopic = new DefaultMsgTopic<>();

    private MsgTopicInterface<TxReceiptRawDataBO> txReceiptTopic = new DefaultMsgTopic<>();

    private MsgTopicInterface<MethodBO> methodTopic = new DefaultMsgTopic<>();

    private MsgTopicInterface<EventBO> eventTopic = new DefaultMsgTopic<>();
}
