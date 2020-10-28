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
package com.webank.webasebee.core.parser;

import java.io.IOException;
import java.math.BigInteger;

import org.fisco.bcos.sdk.client.protocol.response.BcosBlock.Block;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.webank.webasebee.common.bo.data.BlockInfoBO;
import com.webank.webasebee.common.tools.JacksonUtils;
import com.webank.webasebee.core.WebaseBeeApplicationTests;
import com.webank.webasebee.extractor.ods.EthClient;
import com.webank.webasebee.parser.facade.ParseFacade;

/**
 * ParserTest
 *
 * @Description: ParserTest
 * @author maojiayu
 * @data Jul 3, 2019 11:22:41 AM
 *
 */
public class ParserTest extends WebaseBeeApplicationTests {

    @Autowired
    private ParseFacade facade;
    @Autowired
    private EthClient ethClient;

    @Test
    public void testParseBlock() throws IOException {
        for (int i = 0; i < 6; i++) {
            Block block = ethClient.getBlock(BigInteger.valueOf(i));
            BlockInfoBO b = facade.parse(block);
            System.out.println(JacksonUtils.toJson(b));
        }

    }

}
