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
package com.webank.webasebee.core.api.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.webank.webasebee.common.tools.ResponseUtils;
import com.webank.webasebee.common.vo.CommonResponse;
import com.webank.webasebee.core.api.manager.MethodManager;
import com.webank.webasebee.db.vo.UnitParaQueryPageReq;
import com.webank.webasebee.db.vo.UnitQueryPageReq;
import com.webank.webasebee.db.vo.UnitTimeRangeQueryPageReq;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * MethodController
 *
 * @Description: MethodController
 * @author maojiayu
 * @data Dec 24, 2018 11:06:25 AM
 *
 */
@RestController
@RequestMapping("/api/method")
@Api(value = "MethodController", tags = "Method Infomation Query")
public class MethodController {
    @Autowired
    private MethodManager methodManager;

    @PostMapping("paras/get")
    @ApiOperation(value = "get by method and paras", httpMethod = "POST")
    public CommonResponse getByMethodParas(@RequestBody @Valid UnitParaQueryPageReq<String> req, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseUtils.validateError(result);
        }
        return methodManager.getPageListByReq(req);
    }

    @PostMapping("name/get")
    @ApiOperation(value = "get by method name", httpMethod = "POST")
    public CommonResponse getByMethodName(@RequestBody @Valid UnitQueryPageReq<String> req, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseUtils.validateError(result);
        }
        return methodManager.find(req);
    }

    @ResponseBody
    @RequestMapping("/time/get")
    @ApiOperation(value = "Base on time range", httpMethod = "POST")
    public CommonResponse getAccountInfoListByTimeRange(@RequestBody @Valid UnitTimeRangeQueryPageReq req,
            BindingResult result) {
        if (result.hasErrors()) {
            return ResponseUtils.validateError(result);
        }
        return methodManager.getPageListByReq(req);
    }

}
