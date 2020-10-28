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
import com.webank.webasemonkey.code.template.face.EventGenerateParas;
import com.webank.webasemonkey.config.SystemEnvironmentConfig;
import com.webank.webasemonkey.constants.TemplateConstants;
import com.webank.webasemonkey.enums.SubProjectEnum;
import com.webank.webasemonkey.tools.SqlNameUtils;
import com.webank.webasemonkey.tools.StringStyleUtils;
import com.webank.webasemonkey.vo.EventMetaInfo;
import com.webank.webasemonkey.vo.FieldVO;

/**
 * EventSqlFileRenderParas to generate event sql file.
 *
 * @Description: the generated sql file isn't used directly, Actually we use hibernate to operate db instead. This
 *               record of sql files are still important.
 * @author maojiayu
 * @data Dec 28, 2018 3:06:14 PM
 *
 */
@Component
public class EventSqlFileRenderParas implements EventGenerateParas {
    @Autowired
    protected SystemEnvironmentConfig systemEnvironmentConfig;
    @Autowired
    private SqlNameUtils sqlNameUtils;

    /*
     * input: 0-List<FieldVO> 字段属性; 1-String SQL表名;
     */
    @Override
    public Map<String, Object> getMap(EventMetaInfo event) {
        List<FieldVO> list = event.getList();
        Map<String, Object> map = Maps.newLinkedHashMap();
        map.put("list", list);
        String tableName = sqlNameUtils.getSqlName(event.getContractName(), event.getName()) + "_event";
        map.put("table_name", tableName);
        return map;
    }

    @Override
    public String getTemplatePath() {
        return TemplateConstants.SQL_EVENT_TEMPLATE_PATH;
    }

    @Override
    public String getGeneratedFilePath(EventMetaInfo event) {
        String javaFilePath = SubProjectEnum.CORE.getPathName() + File.separator + "src/main/scripts/event/"
                + StringStyleUtils.upper2underline(event.getName()) + ".sql";
        return javaFilePath;
    }

}
