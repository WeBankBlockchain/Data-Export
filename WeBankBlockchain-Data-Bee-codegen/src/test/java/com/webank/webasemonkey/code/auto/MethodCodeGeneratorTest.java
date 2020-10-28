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
package com.webank.webasemonkey.code.auto;

import java.io.IOException;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.webank.webasemonkey.WebasemonkeyApplicationTests;
import com.webank.webasemonkey.code.service.CodeGenerateService;
import com.webank.webasemonkey.code.template.face.GenerateParas;

/**
 * MethodCodeGeneratorTest
 *
 * @Description: MethodCodeGeneratorTest
 * @author graysonzhang
 * @data 2018年12月4日 下午4:57:44
 *
 */
public class MethodCodeGeneratorTest extends WebasemonkeyApplicationTests {
	
	@Autowired
    private CodeGenerateService codeGenerator;
    @SuppressWarnings("rawtypes")
    @Autowired
    private Map<String, GenerateParas> parasMap;

    @Test
    public void go() throws ClassNotFoundException, IOException {
        parasMap.forEach((k, v) -> System.out.println(k + " " + v));
        codeGenerator.generateBee();
        Assertions.assertNotNull(codeGenerator);
    }
}
