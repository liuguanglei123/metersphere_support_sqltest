CREATE TABLE IF NOT EXISTS sql_debug(
                                        `id` VARCHAR(50) NOT NULL   COMMENT '接口pk' ,
    `name` VARCHAR(255) NOT NULL   COMMENT '接口名称' ,
    `protocol` VARCHAR(20) NOT NULL   COMMENT '接口协议' ,
    `method` VARCHAR(20)    COMMENT 'http协议类型post/get/其它协议则是协议名(mqtt)' ,
    `path` VARCHAR(500)    COMMENT 'http协议路径/其它协议则为空' ,
    `pos` BIGINT NOT NULL  DEFAULT 0 COMMENT '自定义排序' ,
    `project_id` VARCHAR(50) NOT NULL   COMMENT '项目fk' ,
    `module_id` VARCHAR(50) NOT NULL  DEFAULT 'root' COMMENT '模块fk' ,
    `create_time` BIGINT NOT NULL   COMMENT '创建时间' ,
    `create_user` VARCHAR(50) NOT NULL   COMMENT '创建人' ,
    `update_time` BIGINT NOT NULL   COMMENT '修改时间' ,
    `update_user` VARCHAR(50) NOT NULL   COMMENT '修改人' ,
    PRIMARY KEY (id)
    ) ENGINE = InnoDB
    DEFAULT CHARSET = utf8mb4
    COLLATE = utf8mb4_general_ci COMMENT = '接口调试';

CREATE INDEX idx_project_id ON sql_debug(project_id);
CREATE INDEX idx_module_id ON sql_debug(module_id);
CREATE INDEX idx_protocol ON sql_debug(protocol);
CREATE INDEX idx_create_time ON sql_debug(create_time desc);
CREATE INDEX idx_create_user ON sql_debug(create_user);
CREATE INDEX idx_name ON sql_debug(name);