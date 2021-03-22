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
package com.webank.blockchain.data.export.common.stash.rlp;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.codec.binary.Hex;
import org.fisco.bcos.sdk.utils.Numeric;

import java.util.ArrayList;
import java.util.List;

/**
 * RLPList
 *
 * @Description: RLPList
 * @author maojiayu
 * @data Sep 4, 2019 4:21:50 PM
 *
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class RLPList extends ArrayList<RLPElement> implements RLPElement{

    private static final long serialVersionUID = -2763960600461380063L;

    private byte[] RLPData;

    public List<String> getList() {
        List<String> result = new ArrayList<>();
        for (RLPElement e : this) {
            result.add(Numeric.toHexString(e.getRLPData()));
        }
        return result;
    }
}