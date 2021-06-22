package com.webank.blockchain.data.export.common.bo.data;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author laifagen
 * @Description:
 * @date 2021/06/22
 */
@Data
@Accessors(chain = true)
public class BlockBrowserRawDataBO {
    private String blockHash;
    private long blockHeight;
    private Date blockTimeStamp;
    private String sealer;
    private String sealerList;
}
