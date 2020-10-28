/**
 * Copyright 2014-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.webank.webasemonkey.code.template.paras;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.google.common.collect.Maps;
import com.webank.webasemonkey.code.template.face.AtomicParas;
import com.webank.webasemonkey.constants.TemplateConstants;
import com.webank.webasemonkey.tools.SqlNameUtils;
import com.webank.webasemonkey.vo.ContractStructureMetaInfo;
import com.webank.webasemonkey.vo.FieldVO;

/**
 * GrafanaPanelParas
 *
 * @Description: GrafanaPanelParas
 * @author maojiayu
 * @data Mar 27, 2019 4:48:14 PM
 *
 */
@Component
public class GrafanaPanelTableParas implements AtomicParas<ContractStructureMetaInfo> {
    @Autowired
    private SqlNameUtils sqlNameUtils;

    @Override
    public Map<String, Object> getMap(ContractStructureMetaInfo info) {
        List<FieldVO> list = info.getList();
        Map<String, Object> map = Maps.newLinkedHashMap();
        map.put("list", list);
        String className = info.getContractName() + StringUtils.capitalize(info.getName());
        String tableName = sqlNameUtils.getSqlName(info.getContractName(), info.getName()+ "_" + info.getType());
        map.put("table_name", tableName);
        map.put("title", className);
        return map;
    }

    @Override
    public String getTemplatePath() {
        return TemplateConstants.GRAFANA_PANEL_TABLE_PATH;
    }

}
