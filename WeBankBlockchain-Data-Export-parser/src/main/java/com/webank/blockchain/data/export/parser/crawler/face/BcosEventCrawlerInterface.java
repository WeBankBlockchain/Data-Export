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
package com.webank.blockchain.data.export.parser.crawler.face;

import com.webank.blockchain.data.export.common.bo.data.EventBO;
import org.fisco.bcos.sdk.model.TransactionReceipt;

import java.util.Date;
import java.util.List;

/**
 * BcosEventCrawlerInterface
 *
 * @Description: BcosEventCrawlerInterface
 * @author graysonzhang
 * @data 2018-11-07 18:27:58
 *
 */
public interface BcosEventCrawlerInterface {

    /**    
     * Get event data by parsing transaction receipt object,and storage event data into db. 
     * if occurs error, return false, else return true.              
     * 
     * @param receipt
     * @param blockTimeStamp    
     * @return boolean   
     * @throws   
     */
    List<EventBO> handleReceipt(TransactionReceipt receipt, Date blockTimeStamp);
}
