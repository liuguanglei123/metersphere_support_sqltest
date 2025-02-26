package io.metersphere.sql.domain;

import io.metersphere.validation.groups.*;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import lombok.Data;

@Data
public class UserConnectionInfo implements Serializable {
    @Schema(description = "主键id", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{user_connection_info.id.not_blank}", groups = {Updated.class})
    private Long id;

    @Schema(description = "")
    private String host;

    @Schema(description = "")
    private Integer port;

    @Schema(description = "数据库认证用户")
    private String username;

    @Schema(description = "数据库认证用密码")
    private String password;

    @Schema(description = "数据库协议，默认为mysql,目前支持dingo，未来将支持postgresql", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{user_connection_info.protocol.not_blank}", groups = {Created.class})
    @Size(min = 1, max = 20, message = "{user_connection_info.protocol.length_range}", groups = {Created.class, Updated.class})
    private String protocol;

    @Schema(description = "")
    private String driverFileName;

    @Schema(description = "项目id", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{user_connection_info.project_id.not_blank}", groups = {Created.class})
    @Size(min = 1, max = 50, message = "{user_connection_info.project_id.length_range}", groups = {Created.class, Updated.class})
    private String projectId;

    @Schema(description = "创建时间")
    private Long createTime;

    @Schema(description = "创建人")
    private String createUser;

    @Schema(description = "修改时间")
    private Long updateTime;

    @Schema(description = "归属人", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{user_connection_info.user_id.not_blank}", groups = {Created.class})
    @Size(min = 1, max = 50, message = "{user_connection_info.user_id.length_range}", groups = {Created.class, Updated.class})
    private String userId;

    @Schema(description = "数据库驱动类")
    private String driverClassName;

    private static final long serialVersionUID = 1L;

    public enum Column {
        id("id", "id", "INTEGER", false),
        host("host", "host", "VARCHAR", true),
        port("port", "port", "INTEGER", false),
        username("username", "username", "VARCHAR", false),
        password("password", "password", "VARCHAR", true),
        protocol("protocol", "protocol", "VARCHAR", false),
        driverFileName("driver_file_name", "driverFileName", "VARCHAR", false),
        projectId("project_id", "projectId", "VARCHAR", false),
        createTime("create_time", "createTime", "BIGINT", false),
        createUser("create_user", "createUser", "VARCHAR", false),
        updateTime("update_time", "updateTime", "BIGINT", false),
        userId("user_id", "userId", "VARCHAR", false),
        driverClassName("driver_class_name", "driverClassName", "VARCHAR", false);

        private static final String BEGINNING_DELIMITER = "`";

        private static final String ENDING_DELIMITER = "`";

        private final String column;

        private final boolean isColumnNameDelimited;

        private final String javaProperty;

        private final String jdbcType;

        public String value() {
            return this.column;
        }

        public String getValue() {
            return this.column;
        }

        public String getJavaProperty() {
            return this.javaProperty;
        }

        public String getJdbcType() {
            return this.jdbcType;
        }

        Column(String column, String javaProperty, String jdbcType, boolean isColumnNameDelimited) {
            this.column = column;
            this.javaProperty = javaProperty;
            this.jdbcType = jdbcType;
            this.isColumnNameDelimited = isColumnNameDelimited;
        }

        public String desc() {
            return this.getEscapedColumnName() + " DESC";
        }

        public String asc() {
            return this.getEscapedColumnName() + " ASC";
        }

        public static Column[] excludes(Column ... excludes) {
            ArrayList<Column> columns = new ArrayList<>(Arrays.asList(Column.values()));
            if (excludes != null && excludes.length > 0) {
                columns.removeAll(new ArrayList<>(Arrays.asList(excludes)));
            }
            return columns.toArray(new Column[]{});
        }

        public static Column[] all() {
            return Column.values();
        }

        public String getEscapedColumnName() {
            if (this.isColumnNameDelimited) {
                return new StringBuilder().append(BEGINNING_DELIMITER).append(this.column).append(ENDING_DELIMITER).toString();
            } else {
                return this.column;
            }
        }

        public String getAliasedEscapedColumnName() {
            return this.getEscapedColumnName();
        }
    }
}