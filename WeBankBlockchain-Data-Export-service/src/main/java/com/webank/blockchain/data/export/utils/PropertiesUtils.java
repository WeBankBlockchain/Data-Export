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
package com.webank.blockchain.data.export.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * PropertiesUtils
 *
 * @author maojiayu
 * @Description: PropertiesUtils
 * @data Dec 28, 2018 4:10:49 PM
 */
@Slf4j
@Component
public class PropertiesUtils {

    @Autowired
    private Environment environment;


    /**
     * return the first mapping result of args.
     *
     * @return property value
     */
    public  String getProperty(String... args) {
        final String delimiter = ".";
        final String key = String.join(delimiter, args);
        return environment.getProperty(key);
    }

}
