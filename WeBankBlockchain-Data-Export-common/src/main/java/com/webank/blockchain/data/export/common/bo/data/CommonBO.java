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
package com.webank.blockchain.data.export.common.bo.data;

import lombok.Data;

import java.util.Map;

/**
 * CommonBO
 *
 * @Description: CommonBO
 * @author maojiayu
 * @data Jul 7, 2019 3:05:17 PM
 *
 */
@Data
public class CommonBO {
    private String table;
    private Map<String, Object> entity;
}
