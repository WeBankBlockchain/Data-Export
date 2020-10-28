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
package com.webank.webasebee.core.utils;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.webank.webasebee.common.tools.JacksonUtils;
import com.webank.webasebee.core.WebaseBeeApplicationTests;
import com.webank.webasebee.db.entity.BlockTaskPool;
import com.webank.webasebee.db.repository.BlockTaskPoolRepository;
import com.webank.webasebee.db.tools.QueryUtils;

/**
 * QueryHelpTest
 *
 * @Description: QueryHelpTest
 * @author maojiayu
 * @data Jun 24, 2020 11:18:05 PM
 *
 */
public class QueryHelpTest extends WebaseBeeApplicationTests {
    @Autowired
    private BlockTaskPoolRepository blockTaskPoolRepository;

    @Test
    public void testQuery() {
        BlockTaskPoolQueryCriteria criteria = new BlockTaskPoolQueryCriteria();
        criteria.setSyncStatus(2);
        List<Long> l = Arrays.asList(5L, 10L);
        criteria.setBlockHeight(l);
        List<BlockTaskPool> list = blockTaskPoolRepository.findAll(
                (root, criteriaQuery, criteriaBuilder) -> QueryUtils.getPredicate(root, criteria, criteriaBuilder));

        System.out.println(JacksonUtils.toJson(list));
        System.out.println(list.size());
    }

}
