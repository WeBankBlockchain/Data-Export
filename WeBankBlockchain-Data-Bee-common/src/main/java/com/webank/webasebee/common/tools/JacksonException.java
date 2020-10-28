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

/**
 * JacksonException
 *
 * @Description: JacksonException
 * @author maojiayu
 * @data Dec 28, 2018 3:45:52 PM
 *
 */
public class JacksonException extends RuntimeException {

    private static final long serialVersionUID = -3313868940376241665L;

    public JacksonException() {
        super();
    }

    public JacksonException(String message, Throwable cause) {
        super(message, cause);
    }

    public JacksonException(String message) {
        super(message);
    }

    public JacksonException(Throwable cause) {
        super(cause);
    }

}
