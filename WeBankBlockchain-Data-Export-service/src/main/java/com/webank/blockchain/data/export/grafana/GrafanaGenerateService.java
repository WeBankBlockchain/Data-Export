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

import cn.hutool.core.collection.CollectionUtil;
import com.google.common.collect.Lists;
import com.webank.blockchain.data.export.common.bo.contract.ContractDetail;
import com.webank.blockchain.data.export.common.bo.contract.ContractMapsInfo;
import com.webank.blockchain.data.export.common.bo.contract.EventMetaInfo;
import com.webank.blockchain.data.export.common.bo.contract.MethodMetaInfo;
import com.webank.blockchain.data.export.config.ServiceConfig;
import com.webank.blockchain.data.export.contract.ContractParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * GrafanaGenerateService
 *
 * @Description: GrafanaGenerateService
 * @author maojiayu
 * @data Mar 27, 2019 5:25:27 PM
 *
 */
@Service
@Slf4j
public class GrafanaGenerateService {
    /** @Fields gt : group template */
    @Autowired
    protected GroupTemplate gt;

    @Autowired
    private ServiceConfig config;
    @Autowired
    private GrafanaPanelTableParasMethod panelParaMethod;

    @Autowired
    private GrafanaPanelTableParasEvent panelParaEvent;

    @Autowired
    private GrafanaDashboardParas grafanaDashboardParas;

    @PostConstruct
    public void genereate() {
        if (!config.isGrafanaEnable()){
            return;
        }
        List<String> panels = Lists.newArrayList();
        if (CollectionUtil.isNotEmpty(this.config.getContractInfos())) {
            ContractMapsInfo mapsInfo = getContractInfo();
            Template template = gt.getTemplate(panelParaMethod.getTemplatePath());
            int index = 1;
            for (Map.Entry<String, ContractDetail> entry : mapsInfo.getContractBinaryMap().entrySet()) {
                ContractDetail contractDetail = entry.getValue();
                for (MethodMetaInfo methodMetaInfo : contractDetail.getMethodMetaInfos()) {
                    Map<String, Object> map = panelParaMethod.getMap(methodMetaInfo);
                    map.put("gridXPos", getX(index));
                    map.put("gridYPos", getY(index));
                    map.put("id", getId(index));
                    template.binding(map);
                    String panelStr = template.render();
                    panels.add(panelStr);
                    index++;
                }
                for (EventMetaInfo eventMetaInfo : contractDetail.getEventMetaInfos()) {
                    Map<String, Object> map = panelParaEvent.getMap(eventMetaInfo);
                    map.put("gridXPos", getX(index));
                    map.put("gridYPos", getY(index));
                    map.put("id", getId(index));
                    template.binding(map);
                    String panelStr = template.render();
                    panels.add(panelStr);
                    index++;
                }
            }
        }
        Map<String, Object> map = grafanaDashboardParas.getMap(panels);
        Template dashboardTemplate = gt.getTemplate(grafanaDashboardParas.getTemplatePath());
        dashboardTemplate.binding(map);
        String dashboardStr = dashboardTemplate.render();
        write(dashboardStr);

    }

    private ContractMapsInfo getContractInfo(){
        return ContractParser.initContractMaps(this.config.getContractInfos(),config);
    }

    public void write(String dashboardStr) {
        try {
            log.info("Begin to create grafana file: {}", grafanaDashboardParas.getGeneratedFilePath());
            FileUtils.write(new File( "./config" + grafanaDashboardParas.getGeneratedFilePath()), dashboardStr, "utf-8", false);
            log.info("Write succeed: {}", grafanaDashboardParas.getGeneratedFilePath());
        } catch (IOException e1) {
            log.error("Grafana Dashboard json file create. Error occourred: {}", e1.getMessage());
        }
    }

    public int getX(int index) {
        /*
         * if (index % 2 == 0) { return 12; } else { return 0; }
         */
        return 0;

    }

    public int getY(int index) {
        // index = (index + 1) / 2;
        // return 32 + index * 8;
        return 24 + index * 8;
    }

    public int getId(int index) {
        return index + 30;
    }

}
