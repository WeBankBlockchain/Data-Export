package com.webank.webasebee.db.dao;

import cn.hutool.core.bean.BeanUtil;
import com.webank.webasebee.common.bo.data.TxRawDataBO;
import com.webank.webasebee.db.entity.TxRawData;
import com.webank.webasebee.db.repository.TxRawDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author wesleywang
 * @Description:
 * @date 2020/10/26
 */
@Component
public class TxRawDataDAO implements SaveInterface<TxRawDataBO>{

    @Autowired
    private TxRawDataRepository txRawDataRepository;

    public void save(TxRawData txRawData) {
        BaseDAO.saveWithTimeLog(txRawDataRepository, txRawData);
    }

    public void save(List<TxRawDataBO> txRawDataList) {
        txRawDataList.forEach(this :: save);
    }

    @Override
    public void save(TxRawDataBO txRawDataBO) {
        TxRawData txRawData = new TxRawData();
        BeanUtil.copyProperties(txRawDataBO,txRawData,true);
        save(txRawData);
    }
}
