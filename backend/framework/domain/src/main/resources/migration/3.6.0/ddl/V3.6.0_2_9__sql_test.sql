CREATE TABLE `sql_scenario_csv`
(
    `id`                 varchar(50)  NOT NULL COMMENT 'id',
    `file_id`            varchar(50)  NOT NULL COMMENT '文件id/引用文件id',
    `scenario_id`        varchar(50)  NOT NULL COMMENT '场景id',
    `name`               varchar(255) NOT NULL COMMENT 'csv变量名称',
    `file_name`          varchar(255)          DEFAULT NULL COMMENT '文件名称',
    `scope`              varchar(50)  NOT NULL COMMENT '作用域 SCENARIO/STEP',
    `enable`             bit(1)       NOT NULL DEFAULT b'1' COMMENT '启用/禁用',
    `association`        bit(1)       NOT NULL DEFAULT b'0' COMMENT '是否引用',
    `encoding`           varchar(50)  NOT NULL DEFAULT 'UTF-8' COMMENT '文件编码',
    `random`             bit(1)       NOT NULL DEFAULT b'0' COMMENT '是否随机',
    `variable_names`     varchar(255)          DEFAULT NULL COMMENT '变量名称(西文逗号间隔)',
    `ignore_first_line`  bit(1)       NOT NULL DEFAULT b'0' COMMENT '忽略首行(只有在设置了变量名称后才生效)',
    `delimiter`          varchar(50)           DEFAULT NULL COMMENT '分隔符',
    `allow_quoted_data`  bit(1)       NOT NULL DEFAULT b'0' COMMENT '是否允许带引号',
    `recycle_on_eof`     bit(1)       NOT NULL DEFAULT b'1' COMMENT '遇到文件结束符再次循环',
    `stop_thread_on_eof` bit(1)       NOT NULL DEFAULT b'0' COMMENT '遇到文件结束符停止线程',
    `project_id`         varchar(50)  NOT NULL COMMENT '项目id',
    PRIMARY KEY (`id`),
    KEY `idx_scenario_id` (`scenario_id`),
    KEY `idx_name` (`name`),
    KEY `idx_file_name` (`file_name`),
    KEY `idx_project_id` (`project_id`)
) COMMENT ='场景csv';


CREATE TABLE `sql_scenario_csv_step`
(
    `id`          varchar(50) NOT NULL COMMENT 'id',
    `file_id`     varchar(50) NOT NULL COMMENT '文件id',
    `step_id`     varchar(50) NOT NULL COMMENT '步骤id',
    `scenario_id` varchar(50) NOT NULL COMMENT '场景ID',
    PRIMARY KEY (`id`),
    KEY `idx_file_id` (`file_id`),
    KEY `idx_step_id` (`step_id`),
    KEY `idx_scenario_id` (`scenario_id`) USING BTREE
) COMMENT ='场景csv引用关系';