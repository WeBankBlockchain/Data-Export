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

import java.io.File;
import java.io.FileFilter;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * ClazzScanUtils
 *
 * @Description: ClazzScanUtils
 * @author maojiayu
 * @data Dec 28, 2018 6:14:38 PM
 *
 */
@Slf4j
public class ClazzScanUtils {
    public static Set<Class<?>> scan(String contractPath, String packageName) {
        Set<Class<?>> classes = new HashSet<>();
        log.info("Scan package path is {}", contractPath);
        File dir = new File(contractPath);
        if (!dir.exists() || !dir.isDirectory()) {
            log.error("{} can't be read.", contractPath);
            return classes;
        }
        File[] dirfiles = dir.listFiles(new FileFilter() {
            public boolean accept(File file) {
                return (file.isDirectory()) || (file.getName().endsWith(".java"));
            }
        });
        log.info("The package dir length is {}", dirfiles.length);
        for (File file : dirfiles) {
            log.info("begin to scan file: {}", file.getName());
            String className = StringUtils.substringBefore(file.getName(), ".");
            try {
                classes.add(Thread.currentThread().getContextClassLoader().loadClass(packageName + '.' + className));
            } catch (ClassNotFoundException e) {
                log.error("can't find .class files.");
            }

        }
        return classes;
    }
}
