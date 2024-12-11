
package io.metersphere.sqlExecutor.context;

import io.metersphere.sqlExecutor.spi.module.MetaData;
import io.metersphere.sqlExecutor.spi.module.Plugin;
import io.metersphere.sqlExecutor.utils.ConnectionPool;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

// TODO:记得修改类名，可以全局搜索chat2db然后修改一下，不然抄袭的痕迹太明显了
// TODO: 需要增加close方法或者remove方法，防止内存泄漏
@Slf4j
public class Chat2DBContext {
    private static final ThreadLocal<ConnectInfo> CONNECT_INFO_THREAD_LOCAL = new ThreadLocal<>();

    public static Map<String, Plugin> PLUGIN_MAP = new ConcurrentHashMap<>();

    static {
        ServiceLoader<Plugin> s = ServiceLoader.load(Plugin.class);
        Iterator<Plugin> iterator = s.iterator();
        while (iterator.hasNext()) {
            Plugin plugin = iterator.next();
            PLUGIN_MAP.put(plugin.getDBConfig().getDbType(), plugin);
        }
    }

    public static MetaData getMetaData(String dbType) {
        if (StringUtils.isBlank(dbType)) {
            return getMetaData();
        }
        return PLUGIN_MAP.get(dbType).getMetaData();
    }

    public static MetaData getMetaData() {
        return PLUGIN_MAP.get(getConnectInfo().getDbType()).getMetaData();
    }

    /**
     * Get the ContentContext of the current thread
     *
     * @return
     */
    public static ConnectInfo getConnectInfo() {
        return CONNECT_INFO_THREAD_LOCAL.get();
    }

    /**
     * Set context
     *
     * @param info
     */
    //TODO: 需要支持这里的线程数据set，否则后面都是不通的
    public static void putContext(ConnectInfo info) {
//        DriverConfig config = info.getDriverConfig();
//        if (config == null) {
//            config = getDefaultDriverConfig(info.getDbType());
//            info.setDriverConfig(config);
//        }
//        CONNECT_INFO_THREAD_LOCAL.set(info);
    }

    /**
     * Set context
     */
    public static void removeContext() {
//        ConnectInfo connectInfo = CONNECT_INFO_THREAD_LOCAL.get();
//        if (connectInfo != null) {
////            connectInfo.close();
//            CONNECT_INFO_THREAD_LOCAL.remove();
//            ConnectionPool.close(connectInfo);
//        }
    }

    public static void close() {
        removeContext();
    }

    public static Connection getConnection() {
//        ConnectInfo connectInfo = getConnectInfo();
//        Connection connection = connectInfo.getConnection();
//        try {
//            if (connection == null || connection.isClosed()) {
//                synchronized (connectInfo) {
//                    connection = connectInfo.getConnection();
//                    try {
//                        if (connection != null && !connection.isClosed()) {
//                            log.info("get connection from cache");
//                            return connection;
//                        } else {
//                            log.info("get connection from db begin");
//                            connection = getDBManage().getConnection(connectInfo);
//                            log.info("get connection from db end");
//                        }
//                    } catch (SQLException e) {
//                        log.error("get connection error", e);
//                        log.info("get connection from db begin2");
//                        connection = getDBManage().getConnection(connectInfo);
//                        log.info("get connection from db end2");
//                    }
//                    connectInfo.setConnection(connection);
//                }
//            }
//        } catch (SQLException e) {
//            log.error("get connection error", e);
//        }
        return ConnectionPool.getConnection(getConnectInfo());
    }

}
