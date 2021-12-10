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

import com.google.common.collect.Maps;
import com.webank.blockchain.data.export.config.ServiceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.Map;

import static com.webank.blockchain.data.export.grafana.GrafanaConstant.GENERATED_GRAFANA_DEFAULT_DASHBOARD_PATH;
import static com.webank.blockchain.data.export.grafana.GrafanaConstant.GRAFANA_DASHBOARD_PATH;

/**
 * GrafanaDashboardParas
 *
 * @Description: GrafanaDashboardParas
 * @author maojiayu
 * @data Mar 28, 2019 11:12:50 AM
 *
 */
@Component
public class GrafanaDashboardParas implements GrafanaParas<List<String>> {

    @Autowired
    private ServiceConfig config;

    @Override
    public Map<String, Object> getMap(List<String> infoList) {
        final String prefix = config.getTablePrefix();
        final String suffix = config.getTablePostfix();
        final Map<String, Object> map = Maps.newConcurrentMap();
        map.put("panels", infoList);
        map.put("block_task_pool",  prefix + "block_task_pool" + suffix);
        map.put("block_detail_info", prefix + "block_detail_info" + suffix);
        map.put("block_tx_detail_info", prefix + "block_tx_detail_info" + suffix);
        map.put("deployed_account_info", prefix + "deployed_account_info" + suffix);
        return map;
    }

    @Override
    public String getTemplatePath() {
        return GRAFANA_DASHBOARD_PATH;
    }

    @Override
    public String getGeneratedFilePath() {
        return File.separator
                + GENERATED_GRAFANA_DEFAULT_DASHBOARD_PATH;
    }
}
