package io.metersphere.sqlExecutor.aspect;

import java.lang.annotation.*;

/**
 * @author jipengfei
 * @version : ConnectionInfoAspect.java
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface ConnectionInfoAspect {
}