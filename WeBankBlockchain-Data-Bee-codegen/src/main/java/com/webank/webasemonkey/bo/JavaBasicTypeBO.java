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
package com.webank.webasemonkey.bo;

import org.apache.commons.lang3.StringUtils;

import lombok.Data;

/**
 * JavaBasicType
 *
 * @Description: JavaBasicType
 * @author maojiayu
 * @data Apr 27, 2020 2:58:16 PM
 *
 */
@Data
public class JavaBasicTypeBO {
    private boolean indexed;
    private String classType;
    private boolean isArray;

    public boolean isStaticArray() {
        return StringUtils.contains(classType, "StaticArray");
    }

    public boolean isDyanmicArray() {
        return (StringUtils.contains(classType, "DynamicArray"));
    }

}
