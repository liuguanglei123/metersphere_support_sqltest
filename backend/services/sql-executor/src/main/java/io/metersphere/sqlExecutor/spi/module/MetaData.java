package io.metersphere.sqlExecutor.spi.module;



/**
 * Get database metadata information.
 *
 * @author jipengfei
 * @version : MetaData.java
 */
public interface MetaData {
    /**
     * Get command executor.
     */
    CommandExecutor getCommandExecutor();

    /**
     * Get column builder.
     *
     */
    ValueProcessor getValueProcessor();

}