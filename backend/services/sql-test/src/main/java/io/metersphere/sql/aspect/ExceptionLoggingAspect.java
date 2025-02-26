package io.metersphere.sql.aspect;

import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ExceptionLoggingAspect {

    // 捕获 service 层抛出的所有异常
    @AfterThrowing(pointcut = "execution(* io.metersphere.sql.service.*.*.*(..)) ||  execution(* io.metersphere.sql.spi.*.*(..))", throwing = "ex")
    public void logException(Exception ex) throws Throwable {
        // 打印堆栈日志
        ex.printStackTrace();
        // 重新抛出异常
        throw ex;
    }
}