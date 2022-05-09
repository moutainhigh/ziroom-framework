# 1. QuickStart

    只支持使用 @RabbitListener 注解方式的consumer

## 1.1 引入jia包

          <dependency>
            <groupId>com.ziroom.framework</groupId>
            <artifactId>ferrari-rocketmq-consumer</artifactId>
            <version>${version}</version>
          </dependency>

|Spring版本|ferrari版本|Ferrari依赖Spring版本|
|----|----|----|
|Spring4.x|V4-1.2.RELEASE|4.2.6.RELEASE|
|Spring5.x|V5-1.2.RELEASE|5.2.15.RELEASE|

## 1.2 新建表

    CREATE TABLE `ferrari_message_consumer` (
        `id` int(11) NOT NULL AUTO_INCREMENT,
        `msg_id` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '消息ID',
        `msg_type` varchar(16) COLLATE utf8mb4_unicode_ci DEFAULT '',
        `exchange_key` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT '',
        `routing_key` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT '',
        `queue` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT '',
        `msg_body` varchar(4096) COLLATE utf8mb4_unicode_ci DEFAULT '',
        `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
        PRIMARY KEY (`id`),
        KEY `IDX_QUEUE` (`queue`),
        KEY `IDX_MESSAGE_ID` (`msg_id`)
    ) ENGINE=InnoDB ;

## 1.3 配置

### 1.3.1 SpringBoot

#### a. 引入Ferrari配置

    @Import(FerrariConsumerConf.class)

    ps: 如果同时接入了producer，FerrariConsumerConf 可以不引入

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

#### b. 配置RocketMQMessageListener

    ```
    @Component
    @RocketMQMessageListener(
            topic = TestRocketmqService.NOTICE_TOPIC,
            consumerGroup = TestRocketmqService.CONSUMER_GROUP_LISTENTER
    )
    public class RocketMqExampleAnnotionListner<T> implements RocketMQListener<NoticeReq> {
    
        @Override
        public void onMessage(NoticeReq noticeReq) {
            try {
                handelMessage(noticeReq);
            } catch (Exception e) {
                log.error("消息变更Mq处理系统异常,e:{}", e);
            }
        }
    
        private void handelMessage(NoticeReq noticeReq) {
            log.info("消息变更Mq通知: [{}]", noticeReq);
        }
    }
    ```
> 只支持@RocketMQMessageListener方式注入消费者
> 暂不支持DefaultMQPushConsumer方式注入消费者
    

### 1.3.2 SpringMvc


## 1.4 其他
