CREATE TABLE `ferrari_lock`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT,
    `unique_key`  varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '唯一键',
    `locked`      tinyint(4) NOT NULL DEFAULT '0' COMMENT ' 0 :未加锁 1:锁定',
    `expire_date` datetime                               NOT NULL COMMENT '锁超时时间',
    `version`     int(11) DEFAULT NULL COMMENT '版本',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_unique_key` (`unique_key`) USING BTREE
) COMMENT='ferrari分布式锁';


CREATE TABLE `ferrari_message`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT,
    `bean_id`        varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL,
    `exchange_key`   varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL,
    `routing_key`    varchar(64) COLLATE utf8mb4_unicode_ci  DEFAULT NULL,
    `msg_status`     int(11) NOT NULL DEFAULT '1' COMMENT '1 待发送 2发送成功 3发送失败',
    `msg_body`       text COLLATE utf8mb4_unicode_ci,
    `msg_body_type`  int(11) DEFAULT NULL,
    `msg_body_class` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `msg_attribute`  text COLLATE utf8mb4_unicode_ci,
    `msg_type`       varchar(16) COLLATE utf8mb4_unicode_ci  DEFAULT NULL,
    `create_time`    datetime                                DEFAULT CURRENT_TIMESTAMP,
    `send_time`      datetime                                DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY              `idx_msg_status` (`msg_status`)
) COMMENT='ferrari 消息主表';

-- Create syntax for TABLE 'ferrari_message_archive'
CREATE TABLE `ferrari_message_archive`
(
    `id`            bigint(20) NOT NULL,
    `bean_id`       varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL,
    `exchange_key`  varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL,
    `routing_key`   varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `msg_status`    int(11) NOT NULL DEFAULT '1' COMMENT '1 待发送 2发送成功 3发送失败',
    `msg_body`      text COLLATE utf8mb4_unicode_ci,
    `msg_body_type` int(11) DEFAULT NULL,
    `msg_attribute` text COLLATE utf8mb4_unicode_ci,
    `msg_type`      varchar(16) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `create_time`   datetime                               DEFAULT CURRENT_TIMESTAMP,
    `send_time`     datetime                               DEFAULT NULL,
    PRIMARY KEY (`id`)
) COMMENT='ferrari_message 归档';

-- Create syntax for TABLE 'ferrari_message_consumer'
CREATE TABLE `ferrari_message_consumer`
(
    `id`           int(11) NOT NULL AUTO_INCREMENT,
    `msg_id`       varchar(32) COLLATE utf8mb4_unicode_ci   DEFAULT '' COMMENT '消息ID',
    `msg_type`     varchar(16) COLLATE utf8mb4_unicode_ci   DEFAULT NULL,
    `exchange_key` varchar(32) COLLATE utf8mb4_unicode_ci   DEFAULT NULL,
    `routing_key`  varchar(32) COLLATE utf8mb4_unicode_ci   DEFAULT NULL,
    `queue`        varchar(32) COLLATE utf8mb4_unicode_ci   DEFAULT NULL,
    `msg_body`     varchar(4096) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `create_time`  datetime                                 DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY            `IDX_QUEUE` (`queue`),
    KEY            `IDX_MESSAGE_ID` (`msg_id`)
) COMMENT='ferrari 消费日志表';