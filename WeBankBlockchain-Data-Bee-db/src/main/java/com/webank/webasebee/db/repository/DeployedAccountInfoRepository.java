package com.webank.webasebee.db.repository;

import com.webank.webasebee.db.entity.DeployedAccountInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * @author wesleywang
 * @Description:
 * @date 2020/10/26
 */
@Repository
public interface DeployedAccountInfoRepository extends JpaRepository<DeployedAccountInfo, Long>, JpaSpecificationExecutor<DeployedAccountInfo> {

}