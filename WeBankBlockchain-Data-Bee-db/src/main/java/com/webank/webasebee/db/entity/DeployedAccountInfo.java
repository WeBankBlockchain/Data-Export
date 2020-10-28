package com.webank.webasebee.db.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 * @author wesleywang
 * @Description:
 * @date 2020/10/26
 */
@Data
@Accessors(chain = true)
@Entity(name = "deployed_account_info")
@Table(name = "deployed_account_info", indexes = { @Index(name = "block_height", columnList = "block_height"),
        @Index(name = "contract_address", columnList = "contract_address"),
        @Index(name = "block_timestamp", columnList = "block_timestamp") })
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class DeployedAccountInfo extends IdEntity {

    /** @Fields contractName : contract name */
    @Column(name = "contract_name")
    private String contractName;

    /** @Fields contractAddress : contract address */
    @Column(name = "contract_address")
    private String contractAddress;

    /** @Fields blockHeight : block height */
    @Column(name = "block_height")
    private long blockHeight;

    /** @Fields blockTimeStamp : block timestamp */
    @Column(name = "block_timestamp")
    private Date blockTimeStamp;

    @Column(name = "abi_hash")
    private String abiHash;

    /** @Fields updatetime : depot update time */
    @UpdateTimestamp
    @Column(name = "depot_updatetime")
    @Temporal(TemporalType.TIMESTAMP)
    protected Date depotUpdatetime;
}

