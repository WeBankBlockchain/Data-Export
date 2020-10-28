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

import java.io.File;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.webank.webasemonkey.code.template.face.ConfigGenerateParas;
import com.webank.webasemonkey.config.ButtonEnvironmentConfig;
import com.webank.webasemonkey.config.ServerConfig;
import com.webank.webasemonkey.config.SystemEnvironmentConfig;
import com.webank.webasemonkey.config.ZookeeperConfig;
import com.webank.webasemonkey.constants.ConfigConstants;
import com.webank.webasemonkey.constants.ConfigFileConstants;
import com.webank.webasemonkey.constants.PackageConstants;
import com.webank.webasemonkey.constants.TemplateConstants;
import com.webank.webasemonkey.enums.SubProjectEnum;
import com.webank.webasemonkey.vo.ContractInfo;
import com.webank.webasemonkey.vo.EventMetaInfo;
import com.webank.webasemonkey.vo.MethodMetaInfo;

/**
 * ApplicationPropParas to generate application.properties.
 *
 * @author maojiayu
 * @data Dec 28, 2018 2:47:01 PM
 *
 */
@Component
public class ApplicationPropParas implements ConfigGenerateParas {

    @Autowired
    private SystemEnvironmentConfig systemEnvironmentConfig;
    @Autowired
    private ButtonEnvironmentConfig buttonEnvironmentConfig;
    @Autowired
    private ServerConfig serverConfig;
    @Autowired
    private ZookeeperConfig zookeeperConfig;

    @Override
    public Map<String, Object> getMap(ContractInfo info) {
        List<EventMetaInfo> eventList = info.getEventList();
        List<MethodMetaInfo> methodList = info.getMethodList();
        Map<String, Object> map = Maps.newLinkedHashMap();
        map.put("eventList", eventList);
        map.put("methodList", methodList);
        map.put("port", serverConfig.getPort());
        map.put("nodeStr", systemEnvironmentConfig.getNodeStr());
        map.put("groupId", systemEnvironmentConfig.getGroupId());
        map.put("group", systemEnvironmentConfig.getGroup());
        map.put("projectName", PackageConstants.PROJECT_PKG_NAME + "." + PackageConstants.SUB_PROJECT_PKG_CORE);
        map.put("multiLiving", systemEnvironmentConfig.getMultiLiving());
        map.put("contractPackName", systemEnvironmentConfig.getContractPackName());
        map.put("contractPath", ConfigConstants.CONTRACT_PATH);
        map.put("crawlBatchUnit", systemEnvironmentConfig.getCrawlBatchUnit());
        map.put("frequency", systemEnvironmentConfig.getFrequency());
        map.put("serverListStr", zookeeperConfig.getServerList());
        map.put("nameSpace", zookeeperConfig.getNamespace());
        map.put("startBlockHeight", systemEnvironmentConfig.getStartBlockHeight());
        map.put("startDate", systemEnvironmentConfig.getStartDate());
        map.put("encryptType", systemEnvironmentConfig.getEncryptType());

        map.put("swagger", buttonEnvironmentConfig.getSwagger());

        return map;
    }

    @Override
    public String getTemplatePath() {
        return TemplateConstants.APPLICATION_PROPERTIES_PATH;
    }

    @Override
    public String getGeneratedFilePath(ContractInfo info) {
        return SubProjectEnum.CORE.getPathName() + File.separator
                + ConfigFileConstants.GENERATED_APPLICATION_PROPERTIES_FILE_PATH;
    }
}
