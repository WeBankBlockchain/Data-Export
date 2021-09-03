package com.webank.blockchain.data.export.plugin.handler;

import org.fisco.bcos.sdk.abi.wrapper.ABIDefinition;

/**
 * @author aaronchu
 * @Description
 * @date 2021/09/03
 */
public interface EventHandlerInterface<TEvent> {

    void handleEvent(TEvent event);

}
