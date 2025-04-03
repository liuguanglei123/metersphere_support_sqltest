CREATE TABLE `sql_scenario_record` (
    `sql_scenario_report_id` varchar(50) NOT NULL COMMENT '报告id',
    `sql_scenario_id` varchar(50) NOT NULL COMMENT '场景id',
    PRIMARY KEY (`sql_scenario_report_id`,`sql_scenario_id`)
)  COMMENT='场景执行记录';

CREATE TABLE `sql_scenario_report_detail` (
    `id` varchar(50) NOT NULL COMMENT 'ID',
    `report_id` varchar(50) NOT NULL COMMENT '报告fk',
    `step_id` varchar(50) NOT NULL COMMENT '场景中各个步骤请求唯一标识',
    `status` varchar(20) DEFAULT NULL COMMENT '结果状态',
    `fake_code` varchar(200) DEFAULT NULL COMMENT '误报编号/误报状态独有',
    `request_name` varchar(500) DEFAULT NULL COMMENT '请求名称',
    `request_time` bigint NOT NULL COMMENT '请求耗时',
    `code` varchar(500) DEFAULT NULL COMMENT '请求响应码',
    `response_size` bigint NOT NULL DEFAULT '0' COMMENT '响应内容大小',
    `script_identifier` varchar(255) DEFAULT NULL COMMENT '脚本标识',
    `sort` bigint NOT NULL COMMENT '用于循环请求排序',
    PRIMARY KEY (`id`),
    KEY `idx_report_id` (`report_id`),
    KEY `idx_resource_id` (`step_id`)
)  COMMENT='场景报告步骤结果';


CREATE TABLE `sql_scenario_report_detail_blob` (
    `id` varchar(50) NOT NULL COMMENT 'ID',
    `report_id` varchar(50) NOT NULL COMMENT '报告fk',
    `content` longblob COMMENT '执行结果',
     PRIMARY KEY (`id`),
    KEY `idx_report_id` (`report_id`) USING BTREE
)  COMMENT='场景报告步骤结果内容';