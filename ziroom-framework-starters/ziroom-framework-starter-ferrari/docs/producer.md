# 1. QuickStart

## 使用示例

    @Resource
    private FerrariRabbitTemplate ferrariRabbitTemplate;
    
    ferrariRabbitTemplate.convertAndSend("exchangeName", "routingKey", message);

## 1.1 引入jia包

          <dependency>
            <groupId>com.ziroom.ferrari</groupId>
            <artifactId>ferrari-rabbit-producer</artifactId>
            <version>${version}</version>
          </dependency>

|Spring版本|ferrari版本|Ferrari依赖Spring版本|
|----|----|----|
|Spring4.x|V4-1.2.RELEASE|4.2.6.RELEASE|
|Spring5.x|V5-1.2.RELEASE|5.2.15.RELEASE|

## 1.2 新建表
    
    CREATE TABLE `ferrari_lock` (
        `id` bigint(20) NOT NULL AUTO_INCREMENT,
        `unique_key` varchar(32) NOT NULL COMMENT '唯一键',
        `locked` tinyint(4) NOT NULL DEFAULT '0' COMMENT ' 0 :未加锁 1:锁定',
        `expire_date` datetime NOT NULL COMMENT '锁超时时间',
        `version` int(11) DEFAULT NULL COMMENT '版本',
        PRIMARY KEY (`id`),
        UNIQUE KEY `idx_unique_key` (`unique_key`)
    ) ENGINE=InnoDB  COMMENT='ferrari分布式锁';    

    CREATE TABLE `ferrari_message` (
        `id` bigint(20) NOT NULL AUTO_INCREMENT,
        `bean_id` varchar(64)  NOT NULL,
        `exchange_key` varchar(64)  NOT NULL,
        `routing_key` varchar(64) DEFAULT NULL,
        `msg_status` int(11) NOT NULL DEFAULT '1' COMMENT '1 待发送 2发送成功 3发送失败,4失败不重试',
        `msg_body` varchar(4096),
        `msg_body_type` int(11),
        `msg_body_class` varchar(255),
        `msg_attribute` varchar(512) ,
        `msg_type` varchar(16)  DEFAULT NULL,
        `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
        `send_time` datetime DEFAULT NULL,
        PRIMARY KEY (`id`),
        KEY `idx_msg_status` (`msg_status`)
    ) ENGINE=InnoDB;
    
    CREATE TABLE `ferrari_message_archive` (
        `id` bigint(20) NOT NULL,
        `bean_id` varchar(64) NOT NULL,
        `exchange_key` varchar(64)NOT NULL,
        `routing_key` varchar(64) DEFAULT NULL,
        `msg_status` int(11) NOT NULL DEFAULT '1' COMMENT '1 待发送 2发送成功 3发送失败,4失败不重试',
        `msg_body` varchar(4096),
        `msg_body_type` int(11),
        `msg_body_class` varchar(255),
        `msg_attribute` varchar(512) ,
        `msg_type` varchar(16),
        `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
        `send_time` datetime DEFAULT NULL,
        PRIMARY KEY (`id`)
    ) ENGINE=MyISAM  COMMENT='ferrari_message归档';

## 1.3 配置

### 1.3.1 SpringBoot

#### a. 引入Ferrari配置

    @Import(FerrariConf.class)
[引入Ferrari配置示例](https://gitlab.ziroom.com/rent-back/Ferrari/blob/master-4.0/ferrari-test/src/main/java/com/ziroom/ferrari/test/FerrariTestServer.java)

#### b. 配置数据源

[配置示例](https://gitlab.ziroom.com/rent-back/Ferrari/blob/master-4.0/ferrari-test/src/main/java/com/ziroom/ferrari/test/conf/TestConfig.java)

        @Bean(name = "masterOnlyJdbcSettings")
        public JdbcSettings masterOnlyJdbcSettings() {
            JdbcSettings jdbcSettings = new JdbcSettings();
            List<DataSource> writeDatasource = Collections.singletonList(dataSource);
            List<DataSource> readDatasource = Collections.singletonList(dataSource);;
            jdbcSettings.setWriteDataSource(writeDatasource);
            jdbcSettings.setReadDataSource(readDatasource);
            jdbcSettings.setDialectEnum(DialectEnum.MYSQL);
            return jdbcSettings;
        }

#### c. 配置FerrariRabbitTemplate

[配置示例](https://gitlab.ziroom.com/rent-back/Ferrari/blob/master-4.0/ferrari-test/src/main/java/com/ziroom/ferrari/test/conf/TestConfig.java)

        @Bean
        public FerrariRabbitTemplate ferrariRabbitTemplate(@Qualifier("connectionFactory") ConnectionFactory connectionFactory, RetryTemplate retryTemplate) {
            FerrariRabbitTemplate template = new FerrariRabbitTemplate();
            template.setConnectionFactory(connectionFactory);
            template.setRetryTemplate(retryTemplate);
            template.setMessageConverter(new Jackson2JsonMessageConverter());
            return template;
        }

### 1.3.2 SpringMvc
## 1.4 其他
    注意事项:
    使用MessagePostProcessor发送消息只会持久化,发送失败不会重试发送！不会重试发送！不会重试发送！
    目前Ferrari 支持发送消息最大长度为4096个字符
# 2. 设计文档:

    http://wiki.ziroom.com/pages/viewpage.action?pageId=661762955