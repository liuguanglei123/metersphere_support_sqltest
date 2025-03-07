package io.metersphere.sql.aspect;

import io.metersphere.sql.context.Chat2DBContext;
import io.metersphere.sql.context.Chat2dbConnectInfo;
import io.metersphere.sql.pojo.data.source.DataSourceBaseRequest;
import io.metersphere.sql.domain.UserConnectionInfo;
import io.metersphere.sql.mapper.UserConnectionInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.metersphere.sql.excption.ParamBusinessException;

/**
 * @author chucan
 * @version : ConnectionInfoHandler.java
 * 该切面用于在指定api接口前获取数据库连接，原版代码的设计思路应该是仅在需要的接口上获取数据库连接信息，不需要所有接口都获取一遍，避免性能浪费
 */
@Component
@Aspect
@Slf4j
public class ConnectionInfoHandler {

    @Autowired
    private UserConnectionInfoMapper userConnectionInfoMapper;

    @Around("within(@io.metersphere.sql.aspect.ConnectionInfoAspect *)")
    public Object connectionInfoHandler(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        try {
            Object[] params = proceedingJoinPoint.getArgs();
            if (params != null && params.length > 0) {
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    if (param instanceof DataSourceBaseRequest) {
                        Integer dataSourceId = ((DataSourceBaseRequest) param).getDataSourceId();
                        Chat2DBContext.putContext(toInfo(dataSourceId));
                    }
                }
            }

            return proceedingJoinPoint.proceed();
        } finally {
            Chat2DBContext.removeContext();
        }
    }

    public Chat2dbConnectInfo toInfo(Integer dataSourceId) {
        UserConnectionInfo userConnectionInfo = userConnectionInfoMapper.selectByPrimaryKey(dataSourceId);
        if (userConnectionInfo == null) {
            throw new ParamBusinessException("userConnectionInfo not exist!");
        }

        Chat2dbConnectInfo chat2dbConnectInfo = new Chat2dbConnectInfo();
        chat2dbConnectInfo.setPort(userConnectionInfo.getPort());
        chat2dbConnectInfo.setHost(userConnectionInfo.getHost());
        chat2dbConnectInfo.setUser(userConnectionInfo.getUsername());
        chat2dbConnectInfo.setPassword(userConnectionInfo.getPassword());
//        chat2dbConnectInfo.setDbType(userConnectionInfo.getProtocol());
        chat2dbConnectInfo.setDriverFileName(userConnectionInfo.getDriverFileName());
        chat2dbConnectInfo.setDriverClassName(userConnectionInfo.getDriverClassName());
        chat2dbConnectInfo.setDbProtocol(userConnectionInfo.getProtocol());

        return chat2dbConnectInfo;
    }

}