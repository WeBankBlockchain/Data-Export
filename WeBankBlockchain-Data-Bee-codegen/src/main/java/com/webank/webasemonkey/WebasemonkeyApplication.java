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
package com.webank.webasemonkey;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * webasemonkeyApplication
 *
 * @Description: webase-monkey is a powerful code generation project serving for fisco-bcos. In this version, it can
 *               generate webase-bee, which could crawl data from fisco-bcos network, including block data，event data,
 *               transaction data and account data.
 * @author maojiayu
 * @data Dec 28, 2018 10:58:11 AM
 *
 */
@SpringBootApplication
public class WebasemonkeyApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebasemonkeyApplication.class, args);
    }
}
