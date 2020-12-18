package com.webank.blockchain.data.export.task;

import cn.hutool.db.DaoTemplate;
import cn.hutool.db.Db;
import cn.hutool.db.DbUtil;
import com.webank.blockchain.data.export.common.entity.DataExportContext;
import com.webank.blockchain.data.export.common.entity.ExportConstant;
import com.webank.blockchain.data.export.common.entity.TableSQL;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;
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

    public ThreadLocal<Map<String, DaoTemplate>> daoThreadLocal = ExportConstant.daoThreadLocal;

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
            createTable();
            threadLocal.set(context);
            crawler.set(crawlRunner);
            daoThreadLocal.set(buildDaoMap());
            try {
                crawlRunner.run(context);
            } catch (InterruptedException e) {
                log.error("DataExportExecutor boot failed ", e);
            }
        }

        public Map<String,DaoTemplate> buildDaoMap(){
            Db db = Db.use(context.getDataSource());
            Map<String,DaoTemplate> daoTemplateMap = new ConcurrentHashMap<>();
            DaoTemplate blockDetailInfoDao = new DaoTemplate(ExportConstant.BLOCK_DETAIL_INFO_TABLE,"pk_id", db);
            DaoTemplate blockTaskPoolDao = new DaoTemplate(ExportConstant.BLOCK_TASK_POOL_TABLE,"pk_id", db);
            DaoTemplate blockRawDataDao = new DaoTemplate(ExportConstant.BLOCK_RAW_DATA_TABLE,"pk_id", db);
            DaoTemplate blockTxDetailInfoDao = new DaoTemplate(ExportConstant.BLOCK_TX_DETAIL_INFO_TABLE,"pk_id", db);
            DaoTemplate txRawDataDao = new DaoTemplate(ExportConstant.TX_RAW_DATA_TABLE,"pk_id", db);
            DaoTemplate txReceiptRawDataDao = new DaoTemplate(ExportConstant.TX_RECEIPT_RAW_DATA_TABLE,"pk_id", db);
            DaoTemplate deployedAccountInfoDao = new DaoTemplate(ExportConstant.DEPLOYED_ACCOUNT_INFO_TABLE,"pk_id", db);

            daoTemplateMap.put(ExportConstant.BLOCK_DETAIL_DAO, blockDetailInfoDao);
            daoTemplateMap.put(ExportConstant.BLOCK_TASK_POOL_DAO, blockTaskPoolDao);
            daoTemplateMap.put(ExportConstant.BLOCK_RAW_DAO, blockRawDataDao);
            daoTemplateMap.put(ExportConstant.BLOCK_TX_DETAIL_DAO, blockTxDetailInfoDao);
            daoTemplateMap.put(ExportConstant.TX_RAW_DAO, txRawDataDao);
            daoTemplateMap.put(ExportConstant.TX_RECEIPT_RAW_DAO, txReceiptRawDataDao);
            daoTemplateMap.put(ExportConstant.DEPLOYED_ACCOUNT_DAO, deployedAccountInfoDao);

            return daoTemplateMap;
        }


        private void createTable() {
            if (context.isAutoCreateTable()){
                log.info("export data auto create table begin....");
                try {
                    Db db = Db.use(context.getDataSource());
                    db.execute(TableSQL.BLOCK_DETAIL_INFO);
                    db.execute(TableSQL.BLOCK_RAW_DATA);
                    db.execute(TableSQL.BLOCK_TASK_POOL);
                    db.execute(TableSQL.BLOCK_TX_DETAIL_INFO);
                    db.execute(TableSQL.DEPLOYED_ACCOUNT_INFO);
                    db.execute(TableSQL.TX_RAW_DATA);
                    db.execute(TableSQL.TX_RECEIPT_RAW_DATA);
                } catch (SQLException e) {
                    log.error("export data table create failed, reason is : ", e);
                    Thread.currentThread().interrupt();
                }
                log.info("export data auto create table success !");
            }
        }


    }

}
