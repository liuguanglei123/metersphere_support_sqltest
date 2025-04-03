CREATE TABLE `sql_scenario_step_blob` (
    `id` varchar(50) NOT NULL COMMENT '场景步骤id',
    `scenario_id` varchar(50) NOT NULL COMMENT '场景id',
    `content` longblob COMMENT '场景步骤内容',
    PRIMARY KEY (`id`),
    KEY `idx_scenario_id` (`scenario_id`)
) COMMENT='场景步骤内容'