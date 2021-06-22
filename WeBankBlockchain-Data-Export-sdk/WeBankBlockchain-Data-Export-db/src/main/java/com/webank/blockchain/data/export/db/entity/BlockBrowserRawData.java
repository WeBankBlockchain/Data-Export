package com.webank.blockchain.data.export.db.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class BlockBrowserRawData extends IdEntity {
    private static final long serialVersionUID = -6636726869135248569L;

    /** @Fields blockHash : block hash */
    private String blockHash;

    /** @Fields blockHeight : block height */
    private long blockHeight;

    /** @Fields blockTimeStamp : block timestamp */
    private Date blockTimeStamp;

    private String sealer;

    private String sealerList;
}
