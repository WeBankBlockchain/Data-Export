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
package com.webank.webasebee.core.api.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.webank.webasebee.common.vo.CommonResponse;
import com.webank.webasebee.db.service.UnitBasicQueryService;
import com.webank.webasebee.db.vo.UnitBiParaQueryPageReq;
import com.webank.webasebee.db.vo.UnitParaQueryPageReq;
import com.webank.webasebee.db.vo.UnitQueryPageReq;
import com.webank.webasebee.db.vo.UnitSpecificationQueryPageReq;
import com.webank.webasebee.db.vo.UnitTimeRangeQueryPageReq;

/**
 * EventManager
 *
 * @Description: EventManager
 * @author maojiayu
 * @data Dec 24, 2018 11:44:43 AM
 *
 */
@Service
public class EventManager {
    @Autowired
    private UnitBasicQueryService unitBasicQueryService;
    private final String type = "EventRepository";

    public CommonResponse getPageListByReq(UnitParaQueryPageReq<String> req) {
        return unitBasicQueryService.getPageListByReq(req, type);
    }

    public CommonResponse getPageListByReq(UnitBiParaQueryPageReq<String> req) {
        return unitBasicQueryService.getPageListByReq(req, type);
    }

    public CommonResponse getPageListByReq(UnitSpecificationQueryPageReq req) {
        return unitBasicQueryService.getPageListByReq(req, type);
    }

    public CommonResponse getPageListByReq(UnitTimeRangeQueryPageReq req) {
        return unitBasicQueryService.getPageListByReq(req, type);
    }

    public CommonResponse find(UnitQueryPageReq<String> req) {
        return unitBasicQueryService.find(req, type);
    }

}
