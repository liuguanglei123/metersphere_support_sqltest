CREATE TABLE `sql_scenario_report_step` (
    `step_id` varchar(50) NOT NULL COMMENT '步骤id',
    `report_id` varchar(50) NOT NULL COMMENT '请求资源 id',
    `name` varchar(255) DEFAULT NULL COMMENT '步骤名称',
    `sort` bigint NOT NULL COMMENT '序号',
    `step_type` varchar(50) NOT NULL COMMENT '步骤类型/API/CASE等',
    `parent_id` varchar(50) NOT NULL DEFAULT 'NONE' COMMENT '父级fk',
    PRIMARY KEY (`step_id`,`report_id`),
    KEY `idx_sort` (`sort`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_report_id` (`report_id`)
) COMMENT='场景报告步骤';