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
package com.webank.blockchain.data.export.config;

import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.lite.api.JobScheduler;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;

/**
 * SimpleJobConfig
 *
 * @Description: PrepareTaskJobConfig
 * @author maojiayu
 * @data Jan 11, 2019 9:59:06 AM
 *
 */
public class PrepareTaskJobConfig {

    private ZookeeperRegistryCenter regCenter;

    public JobScheduler simpleJobScheduler(final SimpleJob simpleJob,
            final String cron,
           final int shardingTotalCount,
            final String shardingItemParameters) {
        return new SpringJobScheduler(simpleJob, regCenter,
                getLiteJobConfiguration(simpleJob.getClass(), cron, shardingTotalCount, shardingItemParameters));
    }

    private LiteJobConfiguration getLiteJobConfiguration(final Class<? extends SimpleJob> jobClass, final String cron,
            final int shardingTotalCount, final String shardingItemParameters) {
        return LiteJobConfiguration
                .newBuilder(
                        new SimpleJobConfiguration(
                                JobCoreConfiguration.newBuilder(jobClass.getName(), cron, shardingTotalCount)
                                        .shardingItemParameters(shardingItemParameters).build(),
                                jobClass.getCanonicalName()))
                .overwrite(true).build();
    }
}
