package io.metersphere.sql.aspect;

import java.lang.annotation.*;

/**
 * 存在此注解的方法，会自动根据参数获取数据库连接信息放在线程变量中，对于首次获取的连接池信息，则会先创建连接池
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface ConnectionInfoAspect {
}