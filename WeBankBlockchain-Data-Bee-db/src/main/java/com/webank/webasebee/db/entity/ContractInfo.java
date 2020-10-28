package com.webank.webasebee.db.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.EqualsAndHashCode;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author wesleywang
 * @Description:
 * @date 2020/10/26
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@Entity(name = "contract_info")
@Table(name = "contract_info")
@ToString(callSuper = true)
public class ContractInfo extends IdEntity{

    @Column(name = "abi_hash", unique = true)
    protected String abiHash;

    @Lob
    @Column(name = "contractABI")
    private String contractABI;

    @Lob
    @Column(name = "contractBinary")
    private String contractBinary;

    @Column(name = "version")
    private short version = 1;

    @Column(name = "contractName")
    private String contractName;

    /** @Fields updatetime : depot update time */
    @UpdateTimestamp
    @Column(name = "depot_updatetime")
    @Temporal(TemporalType.TIMESTAMP)
    protected Date depotUpdatetime;
}
