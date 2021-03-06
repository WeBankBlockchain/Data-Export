/**
 * Copyright 2020 Webank.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.webank.blockchain.data.export.db.dao;

import cn.hutool.core.bean.BeanUtil;
import com.webank.blockchain.data.export.common.bo.data.BlockInfoBO;
import com.webank.blockchain.data.export.common.bo.data.TxReceiptRawDataBO;
import com.webank.blockchain.data.export.db.entity.TxReceiptRawData;
import com.webank.blockchain.data.export.db.repository.TxReceiptRawDataRepository;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * @author wesleywang
 * @Description:
 * @date 2020/10/26
 */
@AllArgsConstructor
public class TxReceiptRawDataDAO implements SaveInterface<BlockInfoBO> {

    private TxReceiptRawDataRepository txReceiptRawDataRepository;

    public void save(TxReceiptRawData txReceiptRawData) {
        txReceiptRawDataRepository.save(txReceiptRawData);
    }

    public void save(List<TxReceiptRawDataBO> txReceiptRawDataBOList) {
        txReceiptRawDataBOList.forEach(this :: save);
    }

    public void save(TxReceiptRawDataBO txReceiptRawDataBO) {
        TxReceiptRawData txReceiptRawData = new TxReceiptRawData();
        BeanUtil.copyProperties(txReceiptRawDataBO,txReceiptRawData,true);
        save(txReceiptRawData);
    }

    @Override
    public void save(BlockInfoBO blockInfoBO) {
        save(blockInfoBO.getTxReceiptRawDataBOList());
    }
}
