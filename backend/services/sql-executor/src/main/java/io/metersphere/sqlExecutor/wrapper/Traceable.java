package io.metersphere.sqlExecutor.wrapper;

/**
 * Is it possible to track
 *
 * @author Shi Yi
 */
public interface Traceable {
    /**
     * Get traceId
     *
     * @return traceId
     */
    String getTraceId();

    /**
     * Set traceId
     *
     * @param traceId
     */
    void setTraceId(String traceId);
}
