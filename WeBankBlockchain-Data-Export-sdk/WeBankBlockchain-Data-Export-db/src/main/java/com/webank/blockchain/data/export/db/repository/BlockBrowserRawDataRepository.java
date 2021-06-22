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
package com.webank.blockchain.data.export.db.repository;

import cn.hutool.db.DaoTemplate;
import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import com.webank.blockchain.data.export.common.entity.ExportConstant;
import com.webank.blockchain.data.export.db.entity.BlockBrowserRawData;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;

/**
 * @author laifagen
 * @Description:
 * @date 2021/06/22
 */
@Slf4j
@AllArgsConstructor
public class BlockBrowserRawDataRepository implements RollbackInterface {

    private DaoTemplate blockBrowserRawDataDao;

    private final String tableName;


    public void rollback(long blockHeight){
        try {
            blockBrowserRawDataDao.del(Entity.create(tableName).set("block_height",">= " + blockHeight));
        } catch (SQLException e) {
            log.error(" BlockBrowserRawDataRepository rollback failed ", e);
        }
    }

    public void rollback(long startBlockHeight, long endBlockHeight){
        try {
            Db.use(ExportConstant.getCurrentContext().getDataSource()).execute(
                    "delete from " + tableName +" where block_height >= ? and block_height< ?",startBlockHeight,endBlockHeight);
        } catch (SQLException e) {
            log.error(" BlockBrowserRawDataRepository rollback failed ", e);
        }
    }


    public void save(BlockBrowserRawData blockBrowserRawData) {
        try {
            Entity entity = Entity.parse(blockBrowserRawData,true,true);
            entity.setTableName(tableName);
            blockBrowserRawDataDao.addForGeneratedKey(entity);
        } catch (SQLException e) {
            log.error(" BlockBrowserRawDataRepository save failed ", e);
        }
    }
}
