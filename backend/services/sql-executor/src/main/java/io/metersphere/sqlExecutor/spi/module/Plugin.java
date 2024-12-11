
package io.metersphere.sqlExecutor.spi.module;

import io.metersphere.sqlExecutor.config.DBConfig;

/**
 * @author jipengfei
 * @version : Plugin.java
 */
public interface Plugin {

    /**
     * Get DB configuration information.
     *
     * @return
     */
    DBConfig getDBConfig();

    /**
     * Query db metadata information.
     *
     * @return
     */
    MetaData getMetaData();


}