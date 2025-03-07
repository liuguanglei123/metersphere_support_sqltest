package io.metersphere.sql.constant;

import lombok.Getter;

/**
 * @author: LAN
 * @date: 2023/11/16 10:42
 * @version: 1.0
 */
@Getter
public enum SqlDefinitionStatus {
    PROCESSING,
    DEPRECATED,
    DEBUGGING,
    DONE
}
