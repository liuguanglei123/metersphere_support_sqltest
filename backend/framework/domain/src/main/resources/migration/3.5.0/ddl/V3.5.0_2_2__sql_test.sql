CREATE TABLE IF NOT EXISTS sql_debug_module(
    `id` VARCHAR(50) NOT NULL   COMMENT '接口模块pk' ,
    `name` VARCHAR(255) NOT NULL   COMMENT '模块名称' ,
    `parent_id` VARCHAR(50) NOT NULL  DEFAULT 'NONE' COMMENT '父级fk' ,
    `project_id` VARCHAR(50) NOT NULL   COMMENT '项目fk' ,
    `pos` BIGINT NOT NULL   COMMENT '排序' ,
    `create_time` BIGINT NOT NULL   COMMENT '创建时间' ,
    `update_time` BIGINT NOT NULL   COMMENT '修改时间' ,
    `update_user` VARCHAR(50) NOT NULL   COMMENT '修改人' ,
    `create_user` VARCHAR(50) NOT NULL   COMMENT '创建人' ,
    PRIMARY KEY (id)
    ) ENGINE = InnoDB
    DEFAULT CHARSET = utf8mb4
    COLLATE = utf8mb4_general_ci COMMENT = '接口调试模块';

CREATE INDEX idx_project_id ON sql_debug_module(project_id);
CREATE INDEX idx_pos ON sql_debug_module(pos);
CREATE INDEX idx_create_user ON sql_debug_module(create_user);
CREATE UNIQUE INDEX uq_name_project_parent_type ON sql_debug_module (project_id, name, parent_id);