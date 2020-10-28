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
package com.webank.webasebee.db.specification;

import java.util.List;

import javax.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;

import com.google.common.collect.Lists;
import com.webank.webasebee.db.sys.condition.CommonTimeCondition;
import com.webank.webasebee.db.tools.JpaUtils;

/**
 * TimeSpecification
 *
 * @Description: TimeSpecification
 * @author maojiayu
 * @data Dec 21, 2018 10:48:59 AM
 *
 */
public class TimeSpecification<T> {
    /**
     * compare blockTimeStamp in the db to conditon.
     * 
     * @param condition
     * @return
     */
    public static Specification queryByCriteria(CommonTimeCondition condition) {
        return (root, query, cb) -> {
            List<Predicate> predicates = Lists.newArrayList();
            if (condition.getBeginTime() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("blockTimeStamp"), condition.getBeginTime()));
            }
            if (condition.getEndTime() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("blockTimeStamp"), condition.getEndTime()));
            }

            return JpaUtils.andTogether(predicates, cb);
        };
    }

}
