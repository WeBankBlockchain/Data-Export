package com.webank.blockchain.data.export.common.listener;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author aaronchu
 * @Description
 * @date 2021/08/31
 */
public interface ListenerInterface<TData> {

    void handleOnMatch(TData data);

    void subscribe(Predicate<TData> filter, Consumer<TData> handler);
}
