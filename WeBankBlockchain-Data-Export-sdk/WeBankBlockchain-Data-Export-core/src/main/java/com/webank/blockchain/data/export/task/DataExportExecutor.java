package com.webank.blockchain.data.export.task;

import com.webank.blockchain.data.export.common.entity.DataExportContext;
import com.webank.blockchain.data.export.common.entity.ExportConfig;
import com.webank.blockchain.data.export.common.entity.ExportConstant;
import com.webank.blockchain.data.export.tools.ElasticJobUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.elasticjob.api.JobConfiguration;
import org.apache.shardingsphere.elasticjob.lite.api.bootstrap.impl.ScheduleJobBootstrap;
import org.apache.shardingsphere.elasticjob.reg.base.CoordinatorRegistryCenter;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author wesleywang
 * @Description:
 * @date 2020/12/16
 */
@Data
@Slf4j
public class DataExportExecutor {

    private DataExportContext context;

    @SuppressWarnings("rawtypes")
    private Future future;

    private CrawlRunner crawlRunner;

    public DataExportExecutor(DataExportContext context) {
        this.context = context;
    }

    public void start() {
        log.info("DataExportExecutor is starting ！！！");
        if (context.getConfig().isMultiLiving()){
            ExportConfig config = context.getConfig();
            CoordinatorRegistryCenter registryCenter = ElasticJobUtil.createRegistryCenter(
                    config.getZookeeperServiceLists(), config.getZookeeperNamespace());
            PrepareTaskJob prepareTaskJob = new PrepareTaskJob(context);
            new ScheduleJobBootstrap(registryCenter, prepareTaskJob,
                    JobConfiguration.newBuilder("PrepareTaskJob", 1)
                            .cron(config.getPrepareTaskJobCron()).shardingItemParameters("0=A").overwrite(true).build()
                   ).schedule();
            new ScheduleJobBootstrap(registryCenter, new DepotJob(context,
                    prepareTaskJob.getMapsInfo(),prepareTaskJob.getDataPersistenceManager()),
                    JobConfiguration.newBuilder("DataFlowJob", config.getDataFlowJobShardingTotalCount())
                            .cron(config.getDataFlowJobCron())
                            .shardingItemParameters(config.getDataFlowJobItemParameters()).overwrite(true).build()
                    ).schedule();
            return;
        }
        crawlRunner = CrawlRunner.create(context);
        future = Executors.newSingleThreadExecutor().submit(this::crawlRunnerExport);
    }

    public void stop() {
        future.cancel(true);
        crawlRunner.getRunSwitch().compareAndSet(true,false);
        log.info("DataExportExecutor stop success ！！！");
    }

    public void crawlRunnerExport() {
        ExportConstant.setCurrentContext(context);
        DataPersistenceManager.setCurrentManager(DataPersistenceManager.create(context));
        try {
            crawlRunner.export();
        } catch (Exception e) {
            log.error("DataExportExecutor boot failed ", e);
        }
    }
}
