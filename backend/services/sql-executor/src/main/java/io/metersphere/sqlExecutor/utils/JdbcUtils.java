package io.metersphere.sqlExecutor.utils;

import com.alibaba.druid.DbType;
import lombok.extern.slf4j.Slf4j;
import io.metersphere.sqlExecutor.enums.DataTypeEnum;
import org.springframework.lang.Nullable;


import java.sql.*;
import java.text.Collator;
import java.util.*;

/**
 * jdbc tool class
 *
 * @author Jiaju Zhuang
 */
@Slf4j
public class JdbcUtils {

    private static final long MAX_RESULT_SIZE = 256 * 1024;

    /**
     * Get the database type of Druid
     *
     * @param dbType
     * @return
     */
    public static DbType parse2DruidDbType(String dbType) {
        if (dbType == null) {
            return null;
        }
        if("DINGO".equalsIgnoreCase(dbType)){
            return DbType.other;
        }
        try {
            return DbType.valueOf(dbType.toLowerCase());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * TODO：整理一下字段，估计要加一下特殊类型的支持
     * Parse field type
     *
     * @param typeName
     * @param type
     * @return
     */
    public static DataTypeEnum resolveDataType(String typeName, int type) {
        switch (getTypeByTypeName(typeName, type)) {
            case Types.BOOLEAN:
                return DataTypeEnum.BOOLEAN;
            case Types.CHAR:
            case Types.VARCHAR:
            case Types.NVARCHAR:
            case Types.LONGVARCHAR:
            case Types.LONGNVARCHAR:
                return DataTypeEnum.STRING;
            case Types.BIGINT:
            case Types.DECIMAL:
            case Types.DOUBLE:
            case Types.FLOAT:
            case Types.INTEGER:
            case Types.NUMERIC:
            case Types.REAL:
            case Types.SMALLINT:
                return DataTypeEnum.NUMERIC;
            case Types.BIT:
            case Types.TINYINT:
                if (typeName.toLowerCase().contains("bool")) {
                    // Declared as numeric but actually it's a boolean
                    return DataTypeEnum.BOOLEAN;
                }
                return DataTypeEnum.NUMERIC;
            case Types.DATE:
            case Types.TIME:
            case Types.TIME_WITH_TIMEZONE:
            case Types.TIMESTAMP:
            case Types.TIMESTAMP_WITH_TIMEZONE:
                return DataTypeEnum.DATETIME;
            case Types.BINARY:
            case Types.VARBINARY:
            case Types.LONGVARBINARY:
                return DataTypeEnum.BINARY;
            case Types.BLOB:
            case Types.CLOB:
            case Types.NCLOB:
            case Types.SQLXML:
                return DataTypeEnum.CONTENT;
            case Types.STRUCT:
                return DataTypeEnum.STRUCT;
            case Types.ARRAY:
                return DataTypeEnum.ARRAY;
            case Types.ROWID:
                return DataTypeEnum.ROWID;
            case Types.REF:
                return DataTypeEnum.REFERENCE;
            case Types.OTHER:
                return DataTypeEnum.OBJECT;
            default:
                return DataTypeEnum.UNKNOWN;
        }
    }

//    TODO：整理一下blob字段，下面的几种类型不是都支持的吧
    private static int getTypeByTypeName(String typeName, int type) {
        // [JDBC: SQLite driver uses VARCHAR value type for all LOBs]
        if (type == Types.OTHER || type == Types.VARCHAR) {
            if ("BLOB".equalsIgnoreCase(typeName)) {
                return Types.BLOB;
            } else if ("CLOB".equalsIgnoreCase(typeName)) {
                return Types.CLOB;
            } else if ("NCLOB".equalsIgnoreCase(typeName)) {
                return Types.NCLOB;
            }
        } else if (type == Types.BIT) {
            // Workaround for MySQL (and maybe others) when TINYINT(1) == BOOLEAN
            if ("TINYINT".equalsIgnoreCase(typeName)) {
                return Types.TINYINT;
            }
        }
        return type;
    }

    public static void closeResultSet(@Nullable ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException var2) {
                log.trace("Could not close JDBC ResultSet", var2);
            } catch (Throwable var3) {
                log.trace("Unexpected exception on closing JDBC ResultSet", var3);
            }
        }

    }

}
