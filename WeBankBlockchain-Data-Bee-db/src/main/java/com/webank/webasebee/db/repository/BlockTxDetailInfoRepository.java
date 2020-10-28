/**
 * Copyright 2014-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.webank.webasebee.db.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.webank.webasebee.db.entity.BlockTxDetailInfo;

/**
 * BlockTxDetailInfoRepository
 *
 * @Description: BlockTxDetailInfoRepository
 * @author graysonzhang
 * @data 2018-12-20 14:52:22
 *
 */
@Repository
public interface BlockTxDetailInfoRepository
        extends JpaRepository<BlockTxDetailInfo, Long>, JpaSpecificationExecutor<BlockTxDetailInfo>, RollbackInterface {

    /**
     * Get block transaction info according to block height, return BlockTxDetailInfo object list.
     * 
     * @param blockHeight: block height
     * @return List<BlockTxDetailInfo>
     */
    public List<BlockTxDetailInfo> findByBlockHeight(long blockHeight);

    /**
     * Get block transaction info according to transaction sender, return BlockTxDetailInfo object list.
     * 
     * @param txFrom: transaction sender
     * @return List<BlockTxDetailInfo>
     */
    public List<BlockTxDetailInfo> findByTxFrom(String txFrom);

    /*
     * @see com.webank.webasebee.sys.db.repository.RollbackInterface#rollback(long)
     */
    @Transactional
    @Modifying
    @Query(value = "delete from  #{#entityName} where block_height >= ?1", nativeQuery = true)
    public void rollback(long blockHeight);

    /*
     * @see com.webank.webasebee.sys.db.repository.RollbackInterface#rollback(long)
     */
    @Transactional
    @Modifying
    @Query(value = "delete from  #{#entityName} where block_height >= ?1 and block_height< ?2", nativeQuery = true)
    public void rollback(long startBlockHeight, long endBlockHeight);

}
