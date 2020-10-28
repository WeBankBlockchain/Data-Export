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
package com.webank.webasebee.parser.service;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.webank.webasebee.parser.crawler.face.BcosMethodCrawlerInterface;

/**
 * CrawlService
 *
 * @Description: CrawlService
 * @author maojiayu
 * @data Jul 12, 2019 5:50:57 PM
 *
 */
@Service
public class MethodCrawlService {
    @Autowired
    private Map<String, BcosMethodCrawlerInterface> bcosMethodCrawlerMap;

    public Optional<BcosMethodCrawlerInterface> getMethodCrawler(String name) {
        for (String k : bcosMethodCrawlerMap.keySet()) {
            if (k.equalsIgnoreCase(name)) {
                return Optional.of(bcosMethodCrawlerMap.get(k));
            }
        }
        return Optional.empty();
    }

}
