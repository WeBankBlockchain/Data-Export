package com.webank.blockchain.data.export.task;

import com.webank.blockchain.data.export.common.entity.DataExportContext;
import com.webank.blockchain.data.export.common.entity.ExportConfig;
import com.webank.blockchain.data.export.common.entity.ExportConstant;
import com.webank.blockchain.data.export.tools.ElasticJobUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.elasticjob.lite.api.bootstrap.impl.ScheduleJobBootstrap;
import org.apache.shardingsphere.elasticjob.reg.base.CoordinatorRegistryCenter;
import org.apache.shardingsphere.elasticjob.reg.zookeeper.ZookeeperRegistryCenter;

import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author wesleywang
 * @Description:
 * @date 2020/12/16
 */
@Data
@Slf4j
public class DataExportExecutor {

    private DataExportContext context;

    private ExportExecutor executor;

    private Future future;

    private CrawlRunner crawlRunner;

    public ThreadLocal<DataExportContext> threadLocal = ExportConstant.threadLocal;

    public DataExportExecutor(DataExportContext context) {
        this.context = context;
    }

    public static final ThreadLocal<CrawlRunner> crawler = new ThreadLocal<>();

    private static final ThreadPoolExecutor pool = new ThreadPoolExecutor(
            1, 50, 100, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>(2048));

    public void start() {
        log.info("DataExportExecutor is starting ！！！");
        if (context.getConfig().isMultiLiving()){
            ExportConfig config = context.getConfig();
            CoordinatorRegistryCenter registryCenter = ElasticJobUtil.createRegistryCenter(
                    config.getZookeeperServiceLists(), config.getZookeeperNamespace());
            new ScheduleJobBootstrap(registryCenter, new PrepareTaskJob(),
                    ElasticJobUtil.createJobConfiguration("PrepareTaskJob",config.getPrepareTaskJobCron(),
                            1, "0=A")).schedule();
            new ScheduleJobBootstrap(registryCenter, new DepotJob(),
                    ElasticJobUtil.createJobConfiguration("DataFlowJob",config.getDataFlowJobCron(),
                    config.getDataFlowJobShardingTotalCount(), config.getDataFlowJobItemParameters())).schedule();
            return;
        }
        executor = new ExportExecutor();
        crawlRunner = CrawlRunner.create(context);
        future = pool.submit(executor);
    }

    public void stop() {
        future.cancel(true);
        crawlRunner.getRunSwitch().getAndSet(false);
        log.info("DataExportExecutor stop success ！！！");
    }

    class ExportExecutor implements Runnable {

        @Override
        public void run() {
            threadLocal.set(context);
            crawler.set(crawlRunner);
            try {
                crawlRunner.run(context);
            } catch (Exception e) {
                log.error("DataExportExecutor boot failed ", e);
            }
        }
    }

}
