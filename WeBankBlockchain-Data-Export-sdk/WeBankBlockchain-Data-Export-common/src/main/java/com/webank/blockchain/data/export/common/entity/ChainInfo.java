package com.webank.blockchain.data.export.common.entity;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author wesleywang
 * @Description:
 * @date 2020/12/16
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class ChainInfo extends BlockDataSource {

    private String nodeStr;
    private String groupId;
    private String certPath;
    //0-ECDSA,1-SM
    private int cryptoTypeConfig;
    private String rpcUrl;
}
