
package io.metersphere.sql.context;

import com.alibaba.druid.pool.DruidDataSource;
import io.metersphere.sql.config.DriverConfig;
import io.metersphere.sql.domain.UserConnectionInfo;
import io.metersphere.sql.spi.model.KeyValue;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author jipengfei
 * @version : ConnectInfo.java
 */
@Slf4j
@Data
public class Chat2dbConnectInfo {

    private String loginUser;

    private String alias;

    private Long dataSourceId;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    private String databaseName;

    private String schemaName;

    private Long consoleId;

    private String url;

    private String user;

    private String password;

    private Boolean consoleOwn = Boolean.FALSE;

    private String dbType;

    private String dbProtocol;

    private Integer port;

    private String urlWithOutDatabase;

    private String host;

    private String sid;

    private String driver;

    private String jdbc;

    private List<KeyValue> extendInfo;

    public Connection connection;

    private String dbVersion;

    private DriverConfig driverConfig;

    private Date lastAccessTime;

    private String driverFileName;

    private String driverClassName;

    private DruidDataSource druidDataSource;

    public LinkedHashMap<String, Object> getExtendMap() {

        if (ObjectUtils.isEmpty(extendInfo)) {
            if (driverConfig != null) {
                extendInfo = driverConfig.getExtendInfo();
            } else {
                return new LinkedHashMap<>();
            }
        }
        if (ObjectUtils.isEmpty(extendInfo)) {
            return new LinkedHashMap<>();
        }
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        for (KeyValue keyValue : extendInfo) {
            map.put(keyValue.getKey(), keyValue.getValue());
        }
        return map;
    }


    public void setDatabase(String database) {
        this.databaseName = database;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Chat2dbConnectInfo)) {
            return false;
        }
        Chat2dbConnectInfo that = (Chat2dbConnectInfo) o;
        return Objects.equals(dataSourceId, that.dataSourceId)
                && Objects.equals(gmtModified, that.gmtModified)
                ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(dataSourceId);
    }

    public Chat2dbConnectInfo copy() {
        Chat2dbConnectInfo copy = new Chat2dbConnectInfo();
        copy.setDbVersion(this.getDbVersion());
        copy.setDbType(this.getDbType());
        copy.setHost(this.getHost());
        copy.setPort(this.getPort());
        copy.setDatabaseName(this.getDatabaseName());
        copy.setSchemaName(this.getSchemaName());
        copy.setUser(this.getUser());
        copy.setPassword(this.getPassword());
        copy.setUrl(this.getUrl());
        copy.setAlias(this.getAlias());
        copy.setDataSourceId(this.getDataSourceId());
        copy.setConsoleId(this.getConsoleId());
        copy.setConsoleOwn(this.getConsoleOwn());
        copy.setDriver(this.getDriver());
        copy.setJdbc(this.getJdbc());
        copy.setSid(this.getSid());
        copy.setUrlWithOutDatabase(this.getUrlWithOutDatabase());
        copy.setLastAccessTime(new Date());
        copy.setDriverFileName(this.getDriverFileName());
        copy.setDriverClassName(this.getDriverClassName());
        copy.setDbProtocol(this.getDbProtocol());
        return copy;
    }

    public void close() {
        if (this != null) {
            Connection connection = this.getConnection();
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                    log.info("connection close success");
                }
            } catch (SQLException e) {
                log.error("connection close error",e);
            }
        }
    }


    public String getKey() {
        return "dbHost:"+host+"_dbPort:"+port+"_dbUser:"+user+"_dbPassword:"+password;
    }

    private final AtomicInteger refCount = new AtomicInteger(0);

    public int incrementRefCount() {
       return refCount.incrementAndGet();
    }

    public int decrementRefCount() {
       return refCount.decrementAndGet();
    }

    public int getRefCount() {
        return refCount.get();
    }
}