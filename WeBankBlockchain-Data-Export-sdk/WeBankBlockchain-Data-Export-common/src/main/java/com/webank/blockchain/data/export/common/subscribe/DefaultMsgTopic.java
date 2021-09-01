package com.webank.blockchain.data.export.common.subscribe;

import com.webank.blockchain.data.export.common.subscribe.face.MsgTopicInterface;
import com.webank.blockchain.data.export.common.subscribe.face.SubscriberInterface;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author aaronchu
 * @Description
 * @date 2021/09/01
 */
public class DefaultMsgTopic<TMsg> implements MsgTopicInterface<TMsg> {

    private ConcurrentHashMap<SubscriberInterface<TMsg>, Boolean> subscribers = new ConcurrentHashMap<>();

    public void publish(TMsg msg, Object context){
        this.subscribers.forEachKey(1, subscriber -> {
            if(subscriber.shouldProcess(msg, context)){
                subscriber.process(msg);
            }
        });
    }

    public void addSubscriber(SubscriberInterface<TMsg> subscriber){
        this.subscribers.putIfAbsent(subscriber, true);
    }

    public void removeSubscriber(SubscriberInterface<TMsg> subscriber){
        this.subscribers.remove(subscriber);
    }
}
