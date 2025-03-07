CREATE TABLE `sql_definition_module` (
    `id` varchar(50) NOT NULL COMMENT 'SQL用例模块pk',
    `name` varchar(255) NOT NULL COMMENT '模块名称',
    `parent_id` varchar(50) NOT NULL DEFAULT 'NONE' COMMENT '父级fk',
    `project_id` varchar(50) NOT NULL COMMENT '项目fk',
    `pos` int DEFAULT NULL COMMENT '排序',
    `create_time` bigint NOT NULL COMMENT '创建时间',
    `update_time` bigint NOT NULL COMMENT '修改时间',
    `update_user` varchar(50) NOT NULL COMMENT '修改人',
    `create_user` varchar(50) NOT NULL COMMENT '创建人',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uq_name_project_parent_type` (`project_id`,`name`,`parent_id`),
    KEY `idx_project_id` (`project_id`),
    KEY `idx_pos` (`pos`)
) COMMENT='SQL用例定义模块';