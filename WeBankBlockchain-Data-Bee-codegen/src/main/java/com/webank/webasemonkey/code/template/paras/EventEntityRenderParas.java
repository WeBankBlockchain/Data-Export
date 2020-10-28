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

import com.google.common.collect.Maps;
import com.webank.webasemonkey.code.template.face.EventGenerateParas;
import com.webank.webasemonkey.config.SystemEnvironmentConfig;
import com.webank.webasemonkey.constants.PackageConstants;
import com.webank.webasemonkey.constants.TemplateConstants;
import com.webank.webasemonkey.tools.PackagePath;
import com.webank.webasemonkey.tools.SqlNameUtils;
import com.webank.webasemonkey.vo.EventMetaInfo;
import com.webank.webasemonkey.vo.FieldVO;

/**
 * EventEntityRenderParas to generate event entities.
 *
 * @author maojiayu
 * @data Dec 28, 2018 3:03:36 PM
 *
 */
@Component
public class EventEntityRenderParas implements EventGenerateParas {
    @Autowired
    protected SystemEnvironmentConfig systemEnvironmentConfig;
    @Autowired
    private SqlNameUtils sqlNameUtils;

    @Override
    public Map<String, Object> getMap(EventMetaInfo event) {
        List<FieldVO> list = event.getList();
        Map<String, Object> map = Maps.newLinkedHashMap();
        map.put("list", list);
        String className = event.getContractName() + event.getName();
        String tableName = sqlNameUtils.getSqlName(event.getContractName(), event.getName()) + "_event";
        map.put("table_name", systemEnvironmentConfig.getTablePrefix() + tableName + systemEnvironmentConfig.getTablePostfix());
        map.put("class_name", className);
        map.put("group", systemEnvironmentConfig.getGroup());
        map.put("projectName", PackageConstants.PROJECT_PKG_NAME + "." + PackageConstants.SUB_PROJECT_PKG_DB);
        return map;
    }

    @Override
    public String getTemplatePath() {
        return TemplateConstants.DB_EVENT_ENTITY_TEMPLATE_PATH;
    }

    @Override
    public String getGeneratedFilePath(EventMetaInfo event) {
        String packagePath = PackagePath.getPackagePath(PackageConstants.DB_EVENT_ENTRY_PACKAGE_POSTFIX,
                systemEnvironmentConfig.getGroup(), PackageConstants.SUB_PROJECT_PKG_DB);
        String className = event.getContractName() + event.getName();
        String javaFilePath = packagePath + "/" + className + ".java";
        return javaFilePath;
    }

}
