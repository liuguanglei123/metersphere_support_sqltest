
package io.metersphere.sql.context;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import io.metersphere.sql.config.DriverConfig;
import io.metersphere.sql.domain.UserConnectionInfo;
import io.metersphere.sql.spi.service.DBManage;
import io.metersphere.sql.spi.service.MetaData;
import io.metersphere.sql.spi.service.Plugin;
import io.metersphere.sql.spi.service.SqlBuilder;
import io.metersphere.sql.utils.ConnectionPool;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.ap.internal.util.Strings;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

// TODO: 需要注意close方法或者remove方法，防止内存泄漏和连接池一直在
@Slf4j
public class Chat2DBContext {

    // CONNECT_INFO_THREAD_LOCAL这个线程变量中保存的是数据库连接相关的基本信息，包括数据库协议类型，host，port，驱动文件jar，驱动类等等
    // 部分请求会通过请求参数中的datasourceid等值来获取连接信息，然后将连接信息存储在该线程变量中供后续使用
    private static final ThreadLocal<Chat2dbConnectInfo> CONNECT_INFO_THREAD_LOCAL = new ThreadLocal<>();

    // PLUGIN_MAP来源于chat2db的原版设计，其源目的是存储数据库管理相关的各种实现类，Plugin为一个接口，其实现类包括mysql orcle pg等不同数据库的实现
    // 在dingodb的测试中，这里的实现类就是基于不同的协议来实现不同的实现类，比如mysql协议，dingo协议，以及未来支持的pg协议等等
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
        return PLUGIN_MAP.get(getConnectInfo().getDbProtocol().toUpperCase()).getMetaData();
    }

    /**
     * Get the ContentContext of the current thread
     *
     * @return
     */
    public static Chat2dbConnectInfo getConnectInfo() {
        return CONNECT_INFO_THREAD_LOCAL.get();
    }

    /**
     * Set context
     *
     * @param info
     */
    public static void putContext(Chat2dbConnectInfo info) {
        // chat2db这里的代码逻辑，对于config为空的情况进行了特殊处理，目的应该是对于没有指定连接类型的数据库，当做mysql并使用默认驱动类来获取driver相关信息
        // 但是对于dingodb的测试来说，通过前端流程进行限制，不允许driverFileName和driverClassName为空的情况
        // DriverConfig config = info.getDriverFileName();
        // if (config == null) {
        //     config = getDriverConfig(info.getDriverFileName(),info.getDriverClassName());
        //     info.setDriverConfig(config);
        // }

        if(info == null || Strings.isEmpty(info.getDriverFileName()) || Strings.isEmpty(info.getDriverClassName())){
            throw new RuntimeException("DriverFileName and DriverClassName can not be null");
        }
        CONNECT_INFO_THREAD_LOCAL.set(info);
    }

    /**
     * remove context
     */
    public static void removeContext() {
        Chat2dbConnectInfo connectInfo = CONNECT_INFO_THREAD_LOCAL.get();
        if (connectInfo != null) {
            CONNECT_INFO_THREAD_LOCAL.remove();
            ConnectionPool.close(connectInfo);
        }
    }

    public static void close() {
        removeContext();
    }

    public static Connection getConnection() throws SQLException {
        Chat2dbConnectInfo connectInfo = CONNECT_INFO_THREAD_LOCAL.get();
        DruidPooledConnection connection = ConnectionPool.getDruidDataSource(getConnectInfo()).getConnection();
        connectInfo.setConnection(connection);

        return connection;
    }

    public static DBManage getDBManage() {
        return PLUGIN_MAP.get(getConnectInfo().getDbProtocol().toUpperCase()).getDBManage();
    }

    public static SqlBuilder getSqlBuilder() {
        return PLUGIN_MAP.get(getConnectInfo().getDbProtocol().toUpperCase()).getMetaData().getSqlBuilder();
    }

}