CREATE TABLE IF NOT EXISTS connection_info(
    `id` int auto_increment NOT NULL COMMENT '主键id' ,
    `ip` VARCHAR(255) NOT NULL   COMMENT '数据库连接ip' ,
    `host` VARCHAR(20) NOT NULL   COMMENT '数据库端口' ,
    `username` VARCHAR(20)    COMMENT '数据库认证用户' ,
    `password` VARCHAR(500)    COMMENT '数据库认证用密码' ,
    `protocol` VARCHAR(20) NOT NULL  DEFAULT 'mysql' COMMENT '数据库协议，默认为mysql,目前支持dingo，未来将支持postgresql' ,
    `drive_file_id` BIGINT NOT NULL  DEFAULT 0 COMMENT '驱动文件id' ,
    `project_id` VARCHAR(50) NOT NULL   COMMENT '项目id' ,
    `create_time` BIGINT NOT NULL   COMMENT '创建时间' ,
    `create_user` VARCHAR(50) NOT NULL   COMMENT '创建人' ,
    `update_time` BIGINT NOT NULL   COMMENT '修改时间' ,
    `update_user` VARCHAR(50) NOT NULL   COMMENT '修改人' ,
    PRIMARY KEY (id)
) ENGINE = InnoDB
DEFAULT CHARSET = utf8mb4
COLLATE = utf8mb4_general_ci COMMENT = '数据库连接信息';

CREATE INDEX idx_project_id ON connection_info(project_id);