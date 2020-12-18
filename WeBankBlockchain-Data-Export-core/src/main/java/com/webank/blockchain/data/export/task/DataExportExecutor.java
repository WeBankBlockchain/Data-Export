package com.webank.blockchain.data.export.task;

import cn.hutool.db.DaoTemplate;
import cn.hutool.db.Db;
import com.webank.blockchain.data.export.common.entity.DataExportContext;
import com.webank.blockchain.data.export.common.entity.ExportThreadLocal;
import lombok.Data;

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
public class DataExportExecutor {

    private DataExportContext context;

    private ExportExecutor executor;

    private Future future;

    private CrawlRunner crawlRunner;

    public ThreadLocal<DataExportContext> threadLocal = ExportThreadLocal.threadLocal;

    public ThreadLocal<Map<String, DaoTemplate>> daoThreadLocal = ExportThreadLocal.daoThreadLocal;

    public DataExportExecutor(DataExportContext context) {
        this.context = context;
    }


    private static final ThreadPoolExecutor pool = new ThreadPoolExecutor(
            1, 50, 100, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>(2048));

    public void start() {
        executor = new ExportExecutor();
        crawlRunner = new CrawlRunner(context);
        future = pool.submit(executor);
    }

    public void stop() {
        future.cancel(true);
    }

    class ExportExecutor implements Runnable {

        @Override
        public void run() {
            threadLocal.set(context);
            daoThreadLocal.set(buildDaoMap());
            try {
                crawlRunner.run(context);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public Map<String,DaoTemplate> buildDaoMap(){
            Db db = Db.use(context.getDataSource());
            Map<String,DaoTemplate> daoTemplateMap = new ConcurrentHashMap<>();
            DaoTemplate blockDetailInfoDao = new DaoTemplate(ExportThreadLocal.BLOCK_DETAIL_INFO_TABLE,"pk_id", db);
            DaoTemplate blockTaskPoolDao = new DaoTemplate(ExportThreadLocal.BLOCK_TASK_POOL_TABLE,"pk_id", db);
            DaoTemplate blockRawDataDao = new DaoTemplate(ExportThreadLocal.BLOCK_RAW_DATA_TABLE,"pk_id", db);
            DaoTemplate blockTxDetailInfoDao = new DaoTemplate(ExportThreadLocal.BLOCK_TX_DETAIL_INFO_TABLE,"pk_id", db);
            DaoTemplate txRawDataDao = new DaoTemplate(ExportThreadLocal.TX_RAW_DATA_TABLE,"pk_id", db);
            DaoTemplate txReceiptRawDataDao = new DaoTemplate(ExportThreadLocal.TX_RECEIPT_RAW_DATA_TABLE,"pk_id", db);
            DaoTemplate deployedAccountInfoDao = new DaoTemplate(ExportThreadLocal.DEPLOYED_ACCOUNT_INFO_TABLE,"pk_id", db);

            daoTemplateMap.put(ExportThreadLocal.BLOCK_DETAIL_DAO, blockDetailInfoDao);
            daoTemplateMap.put(ExportThreadLocal.BLOCK_TASK_POOL_DAO, blockTaskPoolDao);
            daoTemplateMap.put(ExportThreadLocal.BLOCK_RAW_DAO, blockRawDataDao);
            daoTemplateMap.put(ExportThreadLocal.BLOCK_TX_DETAIL_DAO, blockTxDetailInfoDao);
            daoTemplateMap.put(ExportThreadLocal.TX_RAW_DAO, txRawDataDao);
            daoTemplateMap.put(ExportThreadLocal.TX_RECEIPT_RAW_DAO, txReceiptRawDataDao);
            daoTemplateMap.put(ExportThreadLocal.DEPLOYED_ACCOUNT_DAO, deployedAccountInfoDao);
            return daoTemplateMap;
        }

    }

}
