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
import com.webank.webasemonkey.code.template.face.MethodGenerateParas;
import com.webank.webasemonkey.config.SystemEnvironmentConfig;
import com.webank.webasemonkey.constants.TemplateConstants;
import com.webank.webasemonkey.enums.SubProjectEnum;
import com.webank.webasemonkey.tools.SqlNameUtils;
import com.webank.webasemonkey.tools.StringStyleUtils;
import com.webank.webasemonkey.vo.FieldVO;
import com.webank.webasemonkey.vo.MethodMetaInfo;

/**
 * MethodSqlRenderParas uses to installing params for generating sql scripts of method entities.
 *
 * @Description: MethodSqlRenderParas
 * @author graysonzhang
 * @data 2018-12-4 21:11:08
 *
 */
@Component
public class MethodSqlRenderParas implements MethodGenerateParas {
    @Autowired
    protected SystemEnvironmentConfig systemEnvironmentConfig;
    @Autowired
    private SqlNameUtils sqlNameUtils;

    @Override
    public Map<String, Object> getMap(MethodMetaInfo method) {
        List<FieldVO> list = method.getList();
        List<FieldVO> outputList = method.getOutputList();
        Map<String, Object> map = Maps.newLinkedHashMap();
        map.put("list", list);
        map.put("outputList", outputList);
        String tableName = sqlNameUtils.getSqlName(method.getContractName(), method.getName()) + "_method";
        map.put("table_name", tableName);
        return map;
    }

    @Override
    public String getTemplatePath() {
        return TemplateConstants.SQL_METHOD_TEMPLATE_PATH;
    }

    @Override
    public String getGeneratedFilePath(MethodMetaInfo method) {
        String javaFilePath = SubProjectEnum.CORE.getPathName() + File.separator + "src/main/scripts/method/"
                + StringStyleUtils.upper2underline(method.getContractName()) + "_"
                + StringStyleUtils.upper2underline(method.getName()) + ".sql";
        return javaFilePath;
    }
}
