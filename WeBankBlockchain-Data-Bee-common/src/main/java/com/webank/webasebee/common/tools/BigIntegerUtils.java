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
package com.webank.webasebee.common.tools;

import java.math.BigInteger;

import cn.hutool.core.convert.Convert;

/**
 * BigIntegerUtils
 *
 * @Description: BigIntegerUtils
 * @author maojiayu
 * @data Dec 18, 2018 10:55:41 AM
 *
 */
public class BigIntegerUtils {

    public static long toLong(BigInteger bi) {
        return bi.longValue();
    }
    
    public static long toLong(Object obj) {
        return Convert.toLong(obj);
    }


    public static int toInteger(BigInteger bi) {
        return bi.intValue();
    }
    
    public static int toInteger(Object obj) {
        BigInteger bi = (BigInteger) obj;
        return bi.intValue();
    }


}
