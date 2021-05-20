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
package com.webank.blockchain.data.export.grafana;

import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Maps;
import com.webank.blockchain.data.export.common.bo.contract.EventMetaInfo;
import com.webank.blockchain.data.export.common.bo.contract.FieldVO;
import com.webank.blockchain.data.export.config.ServiceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

import static com.webank.blockchain.data.export.grafana.GrafanaConstant.GRAFANA_PANEL_TABLE_PATH;

/**
 * GrafanaPanelParas
 *
 * @Description: GrafanaPanelParas
 * @author maojiayu
 * @data Mar 27, 2019 4:48:14 PM
 *
 */
@Component
public class GrafanaPanelTableParasEvent implements AtomicParas<EventMetaInfo> {

    @Autowired
    private ServiceConfig config;

    @Override
    public Map<String, Object> getMap(EventMetaInfo info) {
        List<FieldVO> list = info.getList();
        Map<String, Object> map = Maps.newLinkedHashMap();
        map.put("list", list);

        String className = info.getContractName() + StringUtils.capitalize(info.getEventName());
        String tableName = getTableName(info.getContractName(),info.getEventName() + "_event");
        map.put("table_name", tableName);
        map.put("title", className);
        return map;
    }

    public String getTableName(String contractName,String name){
        String tablePrefix = config.getTablePrefix();
        String tablePostfix = config.getTablePostfix();
        return tablePrefix + contractName + "_" + StrUtil.toUnderlineCase(name) + tablePostfix;
    }

    @Override
    public String getTemplatePath() {
        return GRAFANA_PANEL_TABLE_PATH;
    }

}
