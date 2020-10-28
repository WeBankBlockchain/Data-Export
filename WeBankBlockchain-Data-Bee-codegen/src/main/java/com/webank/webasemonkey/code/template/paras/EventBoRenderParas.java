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
import com.webank.webasemonkey.vo.EventMetaInfo;
import com.webank.webasemonkey.vo.FieldVO;

/**
 * EventBoRenderParas
 *
 * @Description: EventBoRenderParas
 * @author maojiayu
 * @data Jul 2, 2019 10:41:01 AM
 *
 */
@Component
public class EventBoRenderParas implements EventGenerateParas {

    @Autowired
    protected SystemEnvironmentConfig systemEnvironmentConfig;

    @Override
    public Map<String, Object> getMap(EventMetaInfo event) {
        List<FieldVO> list = event.getList();
        Map<String, Object> map = Maps.newLinkedHashMap();
        map.put("list", list);
        String className = getClassName(event);
        map.put("class_name", className);
        map.put("group", systemEnvironmentConfig.getGroup());
        map.put("projectName", PackageConstants.PROJECT_PKG_NAME + "." + PackageConstants.SUB_PROJECT_PKG_PARSER);
        return map;
    }

    @Override
    public String getTemplatePath() {
        return TemplateConstants.EVENT_BO_TEMPLATE_PATH;
    }

    @Override
    public String getGeneratedFilePath(EventMetaInfo event) {
        String packagePath = PackagePath.getPackagePath(PackageConstants.EVENT_BO_PACKAGE_POSTFIX,
                systemEnvironmentConfig.getGroup(), PackageConstants.SUB_PROJECT_PKG_PARSER);
        String className = getClassName(event);
        String javaFilePath = packagePath + "/" + className + ".java";
        return javaFilePath;
    }

    private String getClassName(EventMetaInfo event) {
        return event.getContractName() + event.getName() + "BO";
    }

}
