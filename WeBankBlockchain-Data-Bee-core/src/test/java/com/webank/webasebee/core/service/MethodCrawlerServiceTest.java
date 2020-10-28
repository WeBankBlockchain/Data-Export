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
package com.webank.webasebee.core.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.webank.webasebee.core.WebaseBeeApplicationTests;
import com.webank.webasebee.core.parser.ContractParser;
import com.webank.webasebee.parser.handler.MethodCrawlerHandler;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * MethodCrawlerServiceTest
 *
 * @Description: MethodCrawlerServiceTest
 * @author graysonzhang
 * @data 2018-12-10 15:15:59
 *
 */
@Slf4j
public class MethodCrawlerServiceTest extends WebaseBeeApplicationTests {
	
	@Autowired
	private ContractParser methodMapper;
	
	@Autowired
	private MethodCrawlerHandler methodCrawlerService;
	
	@Test
	public void testMethodService(){
		//methodCrawlerService.handle();
	}
	
	//@Test
	public void testMethodMapper(){
		//log.info("methodIdMap : {}", JacksonUtils.toJson(methodMapper.getMethodIdMap()));
		//log.info("methodFieldsMap : {}", JacksonUtils.toJson(methodMapper.getMethodFieldMap()));
	}
}
