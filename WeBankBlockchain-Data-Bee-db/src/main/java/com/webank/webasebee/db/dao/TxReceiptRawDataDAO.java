package com.webank.webasebee.db.dao;

import cn.hutool.core.bean.BeanUtil;
import com.webank.webasebee.common.bo.data.TxReceiptRawDataBO;
import com.webank.webasebee.db.entity.TxReceiptRawData;
import com.webank.webasebee.db.repository.TxReceiptRawDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author wesleywang
 * @Description:
 * @date 2020/10/26
 */
@Component
public class TxReceiptRawDataDAO implements SaveInterface<TxReceiptRawDataBO>{

    @Autowired
    private TxReceiptRawDataRepository txRawDataRepository;

    public void save(TxReceiptRawData txReceiptRawData) {
        BaseDAO.saveWithTimeLog(txRawDataRepository, txReceiptRawData);
    }

    public void save(List<TxReceiptRawDataBO> txReceiptRawDataBOList) {
        txReceiptRawDataBOList.forEach(this :: save);
    }

    @Override
    public void save(TxReceiptRawDataBO txReceiptRawDataBO) {
        TxReceiptRawData txReceiptRawData = new TxReceiptRawData();
        BeanUtil.copyProperties(txReceiptRawDataBO,txReceiptRawData,true);
        save(txReceiptRawData);
    }
}
