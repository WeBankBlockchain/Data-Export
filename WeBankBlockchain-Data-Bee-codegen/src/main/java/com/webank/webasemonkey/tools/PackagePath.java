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
package com.webank.webasemonkey.tools;

import com.webank.webasemonkey.constants.PackageConstants;
import com.webank.webasemonkey.enums.SubProjectEnum;

/**
 * PackageProcessor
 *
 * @Description: PackageProcessor
 * @author graysonzhang
 * @data 2018-11-10 1:54:19
 *
 */
public class PackagePath {

    /** @Fields ROOT_PATH : root path */
    public static final String ROOT_PATH = "src/main/java/";

    public static String getPackagePath(String postfix, String group, String subProjectPkg) {
        String packagePath = SubProjectEnum.valueOf(subProjectPkg.toUpperCase()).getPathName() + "/" + ROOT_PATH + group
                + "/" + PackageConstants.PROJECT_PKG_NAME + "/" + subProjectPkg + "/" + PackageConstants.GENERATED + "/"
                + postfix;
        packagePath = packagePath.replaceAll("\\.", "/");
        return packagePath;
    }

    public static String getPackagePath(String packageName) {
        String packagePath = ROOT_PATH + packageName;
        packagePath = packagePath.replaceAll("\\.", "/");
        return packagePath;
    }
}
