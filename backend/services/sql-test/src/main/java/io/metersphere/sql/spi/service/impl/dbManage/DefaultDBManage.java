package io.metersphere.sql.spi.service.impl.dbManage;

import com.alibaba.druid.pool.DruidDataSource;
import io.metersphere.sql.config.AppConfig;
import io.metersphere.sql.context.Chat2dbConnectInfo;
import io.metersphere.sql.service.common.JarPackagesManageService;
import io.metersphere.sql.spi.service.DBManage;
import io.metersphere.sql.spi.service.IDriverManager;
import org.apache.commons.lang3.StringUtils;

import io.metersphere.sql.excption.BusinessException;
import org.mapstruct.ap.internal.util.Strings;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;

public class DefaultDBManage implements DBManage {

    private JarPackagesManageService jarPackagesManageService;

    protected static final String DIVIDING_LINE = "-- ----------------------------";

    protected static final String NEW_LINE = "\n";

    protected static final String EXPORT_TITLE = DIVIDING_LINE + NEW_LINE + "-- Chat2DB export data , export time: %s" + NEW_LINE + DIVIDING_LINE;

    protected static final String TABLE_TITLE = DIVIDING_LINE + NEW_LINE + "-- Table structure for table %s" + NEW_LINE + DIVIDING_LINE;

    protected static final String VIEW_TITLE = DIVIDING_LINE + NEW_LINE + "-- View structure for view %s" + NEW_LINE + DIVIDING_LINE;

    protected static final String FUNCTION_TITLE = DIVIDING_LINE + NEW_LINE + "-- Function structure for function %s" + NEW_LINE + DIVIDING_LINE;

    protected static final String TRIGGER_TITLE = DIVIDING_LINE + NEW_LINE + "-- Trigger structure for trigger %s" + NEW_LINE + DIVIDING_LINE;

    protected static final String PROCEDURE_TITLE = DIVIDING_LINE + NEW_LINE + "-- Procedure structure for procedure %s" + NEW_LINE + DIVIDING_LINE;

    private static final String RECORD_TITLE = DIVIDING_LINE + NEW_LINE + "-- Records of %s" + NEW_LINE + DIVIDING_LINE;

    public DefaultDBManage(){
        jarPackagesManageService = (JarPackagesManageService)AppConfig.getApplicationContext().getBean(JarPackagesManageService.class);
    }

    @Override
    public Connection getConnection(Chat2dbConnectInfo connectInfo) {
        Connection connection = connectInfo.getConnection();
        String url = connectInfo.getUrl();

        try {
            connection = IDriverManager.getConnection(url, connectInfo.getUser(), connectInfo.getPassword(),
                    connectInfo.getDriverConfig(), connectInfo.getExtendMap());

        } catch (Exception e1) {
            close(connection);
            throw new BusinessException("connection.error", null, e1);
        }
        connectInfo.setConnection(connection);
        if (StringUtils.isNotBlank(connectInfo.getDatabaseName()) || StringUtils.isNotBlank(connectInfo.getSchemaName())) {
            connectDatabase(connection, connectInfo.getDatabaseName());
        }
        return connection;
    }

    @Override
    public DruidDataSource getDruidDataSource(Chat2dbConnectInfo connectInfo) {
        DruidDataSource dataSource = new DruidDataSource();

        try {
            Class<Driver> driverClass = (Class<Driver>)loadDriverClass(connectInfo.getDriverFileName(), connectInfo.getDriverClassName());
            dataSource.setDriver(driverClass.newInstance());
            dataSource.setDriverClassName(driverClass.getName());
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("getDruidDataSource.error", null, e);
        }

        String schemaName = Strings.isEmpty(connectInfo.getSchemaName())?"dingo":connectInfo.getSchemaName();
        dataSource.setUrl("jdbc:mysql://" + connectInfo.getHost() + ":" + connectInfo.getPort() + "/" + schemaName +
                "?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=UTC");
        dataSource.setUsername(connectInfo.getUser());
        dataSource.setPassword(connectInfo.getPassword());

        // Druid连接池配置
        dataSource.setInitialSize(5);
        dataSource.setMinIdle(5);
        dataSource.setMaxActive(5);
        dataSource.setMaxWait(60000);
        dataSource.setValidationQuery("SELECT 1");
        dataSource.setTestWhileIdle(true);
        dataSource.setTimeBetweenEvictionRunsMillis(60000);
        dataSource.setMinEvictableIdleTimeMillis(300000);
        dataSource.setMaxEvictableIdleTimeMillis(600000);

        return dataSource;
    }

    private Class<?> loadDriverClass(String driverFileName, String driverClassName) throws Exception {
        byte[] jarBytes = jarPackagesManageService.getFile(driverFileName);
        if (jarBytes == null || jarBytes.length == 0) {
            throw new IOException("在minio中找不到如下文件: " + driverFileName);
        }

        // 将字节数组转换为 ByteArrayInputStream
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(jarBytes);

        // 将 ByteArrayInputStream 封装为 URLClassLoader
        URL jarUrl = new URL("jar:file:/tmp/temporary.jar!/");
        URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{jarUrl});

        // 通过 URLClassLoader 加载类
        return classLoader.loadClass(driverClassName);
    }

    private void close(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (Exception e) {

            }
        }
    }

    @Override
    public void connectDatabase(Connection connection, String database) {

    }

}
