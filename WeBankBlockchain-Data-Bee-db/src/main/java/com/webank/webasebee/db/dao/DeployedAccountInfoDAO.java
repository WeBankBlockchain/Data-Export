package com.webank.webasebee.db.dao;

import cn.hutool.core.bean.BeanUtil;
import com.webank.webasebee.common.bo.contract.ContractMapsInfo;
import com.webank.webasebee.common.bo.contract.ContractDetail;
import com.webank.webasebee.common.bo.data.DeployedAccountInfoBO;
import com.webank.webasebee.db.entity.DeployedAccountInfo;
import com.webank.webasebee.db.repository.DeployedAccountInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author wesleywang
 * @Description:
 * @date 2020/10/26
 */
@Component
public class DeployedAccountInfoDAO implements SaveInterface<DeployedAccountInfoBO>{

    @Autowired
    private DeployedAccountInfoRepository deployedAccountInfoRepository;
    @Autowired
    private ContractMapsInfo contractMapsInfo;

    public void save(DeployedAccountInfo deployedAccountInfo) {
        BaseDAO.saveWithTimeLog(deployedAccountInfoRepository, deployedAccountInfo);
    }

    public void save(List<DeployedAccountInfoBO> deployedAccountInfoBOS) {
        deployedAccountInfoBOS.forEach(this::save);
    }

    @Override
    public void save(DeployedAccountInfoBO deployedAccountInfoBO) {
        DeployedAccountInfo deployedAccountInfo = new DeployedAccountInfo();
        BeanUtil.copyProperties(deployedAccountInfoBO, deployedAccountInfo, true);
        ContractDetail contractMethodInfo = contractMapsInfo.getContractBinaryMap().get(deployedAccountInfoBO.getBinary());
        deployedAccountInfo.setAbiHash(contractMethodInfo.getContractInfoBO().getAbiHash());
        save(deployedAccountInfo);
    }
}
