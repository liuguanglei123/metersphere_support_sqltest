package io.metersphere.sqlExecutor.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * @author jipengfei
 * @version : ConnectionInfoHandler.java
 */
@Component
@Aspect
@Slf4j
public class ConnectionInfoHandler {

    @Around("within(@io.metersphere.sqlExecutor.aspect.ConnectionInfoAspect *)")
    public Object connectionInfoHandler(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        //TODO:获取数据库的链接
        return proceedingJoinPoint.proceed();
    }
}