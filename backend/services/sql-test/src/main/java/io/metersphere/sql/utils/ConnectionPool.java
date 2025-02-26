package io.metersphere.sql.utils;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import io.metersphere.sdk.util.LogUtils;
import io.metersphere.sql.context.Chat2dbConnectInfo;
import io.metersphere.sql.context.Chat2DBContext;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ConnectionPool {
    // TODO: 1.增加remove方法等，防止内存泄漏，其他带有ThreadLocal的地方也要检查一下
    // TODO: 2.slf4j的日志实现类不确定有没有问题，后面都要统一改成LogUtils日志输出
    // TODO：3.DRUID_DATASOURCE_MAP的remove方法和断连方法需要补充
    private static ConcurrentHashMap<String, Chat2dbConnectInfo> CONNECTION_MAP = new ConcurrentHashMap<>();

    private static ConcurrentHashMap<String, Chat2dbConnectInfo> DRUID_DATASOURCE_MAP = new ConcurrentHashMap<>();

    static {
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000 * 60);
                    LogUtils.info("CONNECTION_MAP size:{}",CONNECTION_MAP.size());
                    CONNECTION_MAP.forEach((k, v) -> {
                        log.info("CONNECTION_key:{},value:{}",k,v.getRefCount());
                        if (v.getLastAccessTime().getTime() + 1000 * 60 * 20 < System.currentTimeMillis() && v.getRefCount() == 0) {
                            try {
                                Connection connection = v.getConnection();
                                if (connection != null) {
                                    connection.close();
                                    CONNECTION_MAP.remove(k);
                                }
                            } catch (SQLException e) {
                                log.error("close connection error", e);
                            }
                        }
                    });
                } catch (InterruptedException e) {
                    log.error("close connection error", e);
                }
            }
        }).start();

    }

    public static Connection getConnection(Chat2dbConnectInfo chat2dbConnectInfo) throws SQLException {
        DruidPooledConnection connection = getDruidDataSource(chat2dbConnectInfo).getConnection();
        return connection;

//        Connection connection = chat2dbConnectInfo.getConnection();
//        try {
//            if (connection != null && !connection.isClosed()) {
//                log.info("get connection from local");
//                return connection;
//            }
//            String key = chat2dbConnectInfo.getKey();
//            Chat2dbConnectInfo lock = CONNECTION_MAP.computeIfAbsent(key, k -> chat2dbConnectInfo.copy());
//            try {
//                synchronized (lock) {
//                    connection = chat2dbConnectInfo.getConnection();
//                    if (connection != null && !connection.isClosed()) {
//                        log.info("get connection from local");
//                        return connection;
//                    }
//
//                    int n = lock.incrementRefCount();
//                    if (n == 1) {
//                        connection = lock.getConnection();
//                        if (connection != null && !connection.isClosed()) {
//                            log.info("get connection from cache");
//                            chat2dbConnectInfo.setConnection(connection);
//                            lock.setLastAccessTime(new Date());
//                            return connection;
//                        } else {
//                            log.info("get connection from db begin");
//                            // TODO:这里需要重新设计一下，如何获取和保持数据库连接
//                            connection = Chat2DBContext.getDBManage().getConnection(chat2dbConnectInfo);
//                            lock.setConnection(connection);
//                            lock.setLastAccessTime(new Date());
//                            log.info("get connection from db end");
//                        }
//                        chat2dbConnectInfo.setConnection(connection);
//                        return connection;
//                    } else {
//                        connection = Chat2DBContext.getDBManage().getConnection(chat2dbConnectInfo);
//                        chat2dbConnectInfo.setConnection(connection);
//                        return connection;
//                    }
//                }
//            } catch (SQLException e) {
//                log.error("get connection error", e);
//                if (connection != null && !connection.isClosed()) {
//                    connection.close();
//                }
//            }
//        } catch (SQLException e) {
//            log.error("get connection error", e);
//            try {
//                if (connection != null && !connection.isClosed()) {
//                    connection.close();
//                }
//            } catch (Exception e1) {
//                log.error("", e1);
//            }
//        }
//        return null;
    }

    public static DruidDataSource getDruidDataSource(Chat2dbConnectInfo chat2dbConnectInfo) {
        DruidDataSource druidDataSource = chat2dbConnectInfo.getDruidDataSource();
        if (druidDataSource != null && !druidDataSource.isClosed()) {
            log.info("get dataSource from local");
            return druidDataSource;
        }
        String key = chat2dbConnectInfo.getKey();
        Chat2dbConnectInfo lock = DRUID_DATASOURCE_MAP.computeIfAbsent(key, k -> chat2dbConnectInfo.copy());
        synchronized (lock) {
            druidDataSource = chat2dbConnectInfo.getDruidDataSource();
            if (druidDataSource != null && !druidDataSource.isClosed()) {
                log.info("get connection from local");
                return druidDataSource;
            }

            int n = lock.incrementRefCount();
            if (n == 1) {
                druidDataSource = lock.getDruidDataSource();
                if (druidDataSource != null && !druidDataSource.isClosed()) {
                    log.info("get connection from cache");
                    chat2dbConnectInfo.setDruidDataSource(druidDataSource);
                    lock.setLastAccessTime(new Date());
                    return druidDataSource;
                } else {
                    log.info("get connection from db begin");
                    druidDataSource = Chat2DBContext.getDBManage().getDruidDataSource(chat2dbConnectInfo);
                    lock.setDruidDataSource(druidDataSource);
                    lock.setLastAccessTime(new Date());
                    log.info("get connection from db end");
                }
                chat2dbConnectInfo.setDruidDataSource(druidDataSource);
                return druidDataSource;
            } else {
                druidDataSource = Chat2DBContext.getDBManage().getDruidDataSource(chat2dbConnectInfo);
                chat2dbConnectInfo.setDruidDataSource(druidDataSource);
                return druidDataSource;
            }
        }
    }


    public static void close(Chat2dbConnectInfo connectInfo) {
        String key = connectInfo.getKey();
        try {
            Connection currentConnection = connectInfo.getConnection();
            // 如果当前连接已经关闭，则不需要重复关闭
            if (currentConnection == null || currentConnection.isClosed()) {
                log.info("connection is already closed, key:{}, n:{}", connectInfo.getKey(), connectInfo.getRefCount());
                return;
            }
        } catch (SQLException e) {
            log.error("connection close error",e);
        }
        Chat2dbConnectInfo lock = CONNECTION_MAP.get(key);
        if (lock != null) {
            synchronized (lock) {
                int n = lock.decrementRefCount();
                if (n == 0) {
                    lock.setLastAccessTime(new Date());
                    lock.setConnection(connectInfo.getConnection());
                } else {
                    connectInfo.close();
                }
            }
        } else {
            connectInfo.close();
        }


    }
}
