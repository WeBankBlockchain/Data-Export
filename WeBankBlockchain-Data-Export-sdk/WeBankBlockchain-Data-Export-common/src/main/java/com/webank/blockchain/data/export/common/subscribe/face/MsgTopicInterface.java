package com.webank.blockchain.data.export.common.subscribe.face;

/**
 * @author aaronchu
 * @Description
 * @date 2021/09/01
 */
public interface MsgTopicInterface<TMsg> {

    void publish(TMsg msg, Object context);

    void addSubscriber(SubscriberInterface<TMsg> subscriber);

    void removeSubscriber(SubscriberInterface<TMsg> subscriber);

}
