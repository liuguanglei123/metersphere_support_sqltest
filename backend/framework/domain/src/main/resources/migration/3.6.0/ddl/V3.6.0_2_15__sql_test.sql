CREATE TABLE `sql_report_relate_task` (
    `task_resource_id` varchar(50) NOT NULL COMMENT '任务id/任务项id',
    `report_id` varchar(50) NOT NULL COMMENT '报告id',
    PRIMARY KEY (`task_resource_id`,`report_id`)
)  COMMENT='报告与任务关联表';

CREATE TABLE `sql_report_step` (
    `step_id` varchar(50) NOT NULL COMMENT '步骤id',
    `report_id` varchar(50) NOT NULL COMMENT '报告id',
    `name` varchar(255) DEFAULT NULL COMMENT '步骤名称',
    `sort` bigint NOT NULL COMMENT '序号',
    `step_type` varchar(50) NOT NULL COMMENT '步骤类型/SQL/CASE等',
    PRIMARY KEY (`step_id`,`report_id`),
    KEY `idx_report_id` (`report_id`),
    KEY `idx_sort` (`sort`),
    KEY `idx_step_type` (`step_type`)
)  COMMENT='SQL报告步骤'