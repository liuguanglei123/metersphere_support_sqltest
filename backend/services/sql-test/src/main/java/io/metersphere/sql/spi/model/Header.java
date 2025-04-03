package io.metersphere.sql.spi.model;

import io.metersphere.sql.enums.DataTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * cell header
 *
 * @author Jiaju Zhuang
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class Header implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * cell type
     *
     * @see DataTypeEnum
     */
    private String dataType;

    /**
     * display name
     */
    private String name;


    private Boolean primaryKey;


    private String comment;

    private String defaultValue;

    private Integer autoIncrement;

    private Integer nullable;

    private Integer columnSize;

    private Integer decimalDigits;

    public boolean equals(Header header){
        log.info("execute equals");
        return true;
    }

}
