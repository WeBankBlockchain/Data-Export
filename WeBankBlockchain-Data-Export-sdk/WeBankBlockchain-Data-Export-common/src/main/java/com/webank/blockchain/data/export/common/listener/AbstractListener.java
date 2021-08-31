package com.webank.blockchain.data.export.common.listener;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author aaronchu
 * @Description
 * @date 2021/08/31
 */
public abstract class AbstractListener<TData> implements ListenerInterface<TData> {
    private ConcurrentHashMap<Predicate<TData>, Consumer<TData>> subscriptions = new ConcurrentHashMap<>();

    @Override
    public synchronized void handleOnMatch(TData data) {
        this.subscriptions.forEach((predicate, consumer) -> {
            if (predicate.test(data)) {
                consumer.accept(data);
            }
        });
    }

    @Override
    public synchronized void subscribe(Predicate<TData> filter, Consumer<TData> handler) {
        this.subscriptions.putIfAbsent(filter, handler);
    }
}
