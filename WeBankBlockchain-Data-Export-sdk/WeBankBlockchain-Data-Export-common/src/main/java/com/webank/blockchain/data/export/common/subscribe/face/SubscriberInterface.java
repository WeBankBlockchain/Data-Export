package com.webank.blockchain.data.export.common.subscribe.face;

import com.webank.blockchain.data.export.common.subscribe.DefaultMsgTopic;

/**
 * @author aaronchu
 * @Description
 * @date 2021/09/01
 */
public interface SubscriberInterface<TMsg> {

    boolean shouldProcess(TMsg msg, Object context);

    void process(TMsg msg);

    default void subscribe(DefaultMsgTopic<TMsg> msg) {
        msg.addSubscriber(this);
    }

    default void unsubscribe(DefaultMsgTopic<TMsg> msg){
        msg.removeSubscriber(this);
    }
}
