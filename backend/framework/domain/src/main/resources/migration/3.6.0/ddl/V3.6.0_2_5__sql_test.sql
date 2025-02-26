CREATE TABLE IF NOT EXISTS sql_debug_blob(
    `id` VARCHAR(50) NOT NULL   COMMENT 'SQL fk/ 一对一关系' ,
    `request` LONGBLOB    COMMENT '请求内容' ,
    `response` LONGBLOB    COMMENT '响应内容' ,
    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = 'SQL调试详情内容';