
package io.metersphere.sql.spi.service;

import io.metersphere.sql.config.DBConfig;

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

    /**
     *
     * @return
     */
    DBManage getDBManage();

}