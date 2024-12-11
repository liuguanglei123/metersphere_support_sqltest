package io.metersphere.sqlExecutor.dataSourceHelp;

import com.alibaba.druid.pool.DruidDataSource;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DruidHelp {

    private static Map<String,DruidDataSource> dataSourceMap = new ConcurrentHashMap<>();

    public static DruidDataSource getConnetction(String connectUrl){
        //TODO:设置退出机制，不然datasource对象会一直在，内存会一直增长
        if(dataSourceMap.get(connectUrl) != null){
            return dataSourceMap.get(connectUrl);
        }else{
            DruidDataSource druidConnectionPool = createDruidConnectionPool(connectUrl);
            dataSourceMap.put(connectUrl,druidConnectionPool);
            return druidConnectionPool;
        }
    }

    public static DruidDataSource createDruidConnectionPool(String connectUrl) {
        // create druid source
        // TODO:这里先固定写好各种配置，后期再做参数化改造，简单来说，通过这里的connecturl或者某个标识id，可以在ms中拿到各种配置信息
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setUrl(connectUrl);
        druidDataSource.setUsername(System.getenv("root"));
        druidDataSource.setPassword(System.getenv("123123"));
        druidDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");

        druidDataSource.setInitialSize(1);
        druidDataSource.setMaxActive(5);
        druidDataSource.setMaxWait(60000);
        druidDataSource.setTimeBetweenEvictionRunsMillis(60000);
        druidDataSource.setMinEvictableIdleTimeMillis(300000);

        druidDataSource.setTestWhileIdle(true);

        return druidDataSource;
    }
}
