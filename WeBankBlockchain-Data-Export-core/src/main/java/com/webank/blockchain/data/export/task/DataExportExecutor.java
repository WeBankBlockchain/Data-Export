package com.webank.blockchain.data.export.task;

import cn.hutool.db.DaoTemplate;
import cn.hutool.db.Db;
import cn.hutool.db.DbUtil;
import cn.hutool.db.meta.MetaUtil;
import com.webank.blockchain.data.export.common.entity.DataExportContext;
import com.webank.blockchain.data.export.common.entity.ExportConstant;
import com.webank.blockchain.data.export.common.entity.TableSQL;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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
            } catch (InterruptedException e) {
                log.error("DataExportExecutor boot failed ", e);
            }
        }
    }

}
