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
import com.webank.blockchain.data.export.common.bo.data.TxBrowserRawDataBO;
import com.webank.blockchain.data.export.db.entity.TxBrowserRawData;
import com.webank.blockchain.data.export.db.repository.TxBrowserRawDataRepository;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * @author laifagen
 * @Description:
 * @date 2021/06/22
 */
@AllArgsConstructor
public class TxBrowserRawDataDAO implements SaveInterface<BlockInfoBO> {

    private TxBrowserRawDataRepository txBrowserRawDataRepository;

    public void save(TxBrowserRawData txBrowserRawData) {
        txBrowserRawDataRepository.save(txBrowserRawData);
    }

    public void save(List<TxBrowserRawDataBO> txBrowserDataBOList) {
        txBrowserDataBOList.forEach(this :: save);
    }

    public void save(TxBrowserRawDataBO txRawBrowserDataBO) {
        TxBrowserRawData txBrowserRawData = new TxBrowserRawData();
        BeanUtil.copyProperties(txRawBrowserDataBO,txBrowserRawData,true);
        save(txBrowserRawData);
    }

    @Override
    public void save(BlockInfoBO blockInfoBO) {
        save(blockInfoBO.getTxBrowserRawDataBOList());
    }
}
