package com.webank.blockchain.data.export.plugin.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.fisco.bcos.sdk.abi.wrapper.ABIDefinition;

import java.util.List;

/**
 * @author aaronchu
 * @Description
 * @date 2021/09/06
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class DecodedEvent {

    private List<ABIDefinition.NamedType> indexedFields;

    private List<Object> indexedFieldValues;

    private List<ABIDefinition.NamedType> nonIndexedFields;

    private List<Object> nonIndexedFieldValues;
}
