---
title: RocketMQ
---

`ziroom-framework-starter-rocketmq` 提供了与 spring-boot-starter-rocketmq 完全相同的功能。

使用方式也极为类似，首先引入依赖:

```xml
<dependency>
    <groupId>com.ziroom.framework</groupId>
    <artifactId>ziroom-framework-starter-rocketmq</artifactId>
</dependency>
```

添加 rocketmq 相关配置参数，在 src/main/resources 中添加名为 `rocketmq-demo.yaml` 配置文件:

```yaml
ziroom:
  rocketmq:
    name-server: 10.216.9.189:9876

    producer:
      # producer group
      group: demo-group
```

> ziroom.rocketmq 内的所有配置项与 rocketmq-spring-boot-starter 完全一致
> 可查看 org.apache.rocketmq.spring.autoconfigure.RocketMQProperties 获取所有可配置项

> 实际上 `ziroom-framework-starter-rocketmq` 会从 ClassPath 中扫描所有符合 `rocketmq-*.yaml` 模式的配置文件，所以正式部署时，保证该配置文件存在于应用的 ClassPath 中即可，例如可托管于 Omega 的环境 > 配置文件中。   

## 版本
`ziroom-framework-starter-rocketmq` 基于以下开源组件进行扩展:

- rocketmq-spring-boot-starter: 2.0.2
- rocketmq-client: 4.4.0

## 消息发送与消费
这部分功能与开源版本完全一致，可直接参考[相关文档](https://github.com/apache/rocketmq-spring/wiki/%E5%8F%91%E9%80%81%E6%B6%88%E6%81%AF)


## 同时连接多套集群
如果需要同时链接多套 RocketMQ 集群，只需参考上文配置部分，将相关集群配置文件全部放入 ClassPath 中，每个集群需要放入一个独立配置文件中。例如

```
ClassPath
   └── rocketmq-cluster1.yaml
   └── rocketmq-cluster2.yaml
```

```yaml
# rocketmq-cluster1.yaml
ziroom:
  rocketmq:
    name-server: {cluster1.nameserverAddr}
    
    templateBeanName: rocketmqTemplate1

    producer:
      # producer group
      group: demo-group
      
---

# rocketmq-cluster2.yaml
ziroom:
  rocketmq:
    name-server: {cluster1.nameserverAddr}

    templateBeanName: rocketmqTemplate2

    producer:
      # producer group
      group: demo-group

```

并通过 `ziroom.rocketmq.templateBeanName` 声明的 bean 名称即可获得对应集群的 RocketMQTemplate 引用:

```java

    @Autowaired
    @Qualifier("rocketmqTemplate1")
    private RocketMQTemplate rocketmqTemplate1;


    @Autowaired
    @Qualifier("rocketmqTemplate1")
    private RocketMQTemplate rocketmqTemplate2;

```

## 扩展能力

### 事务消息与 at-least-once 消息可靠性保证
`ziroom-framework-starter-rocketmq` 提供了基于本地消息表的 rocketmq 消息事务，并提供了后台任务在 RocketMQ 集群处理消息失败时进行重试。

> 该功能与 RocketMQ 事务消息并不冲突，原生事务消息仍可继续使用

#### 初始化数据库
请参考 ziroom-framework-examples/ziroom-framework-rocketmq-example/src/main/resources/ferrari.sql 初始化必须的 3 张数据库表.

#### 发送消息
首先通过 `@EnableFerrariRocketProducer` 开启消息生产者事务消息能力，并在需要开启事务消息的集群配置文件中添加配置项:

```yaml
ziroom:
  rocketmq:
    name-server: 10.216.9.189:9876
    
+    ferrari:
+      enabled: true

    producer:
      # producer group
      group: demo-group
```

这样由 `ZiroomRocketMQAutoConfiguration` 提供的 `RocketMQTemplate` 即可获得事务消息的能力。

##### 自定义 RocketMQTemplate
如果希望自行创建 RocketMQTemplate, 需要简单修改下代码，在创建 `RocketMQTemplate` 时，将实现替换为: `com.ziroom.ferrari.rocketmq.producer.FerrariRocketmqTemplate`。例如:

```java
@Bean
public RocketMQTemplate rocketmqTemplate() {
+    RocketMQTemplate template = new FerrariRocketmqTemplate();
-    RocketMQTemplate template = new RocketMQTemplate();
    
    // ...省略其他设置项
    
    return template;
}
```

#### 消费消息


#### 消息状态查询


### 天枢动态路由

### 配置管理
> TODO: 暂未实现
>
上文中的 `rocketmq-demo.yaml`可人工添加，但更推荐的方式是在 [ZMS 消息队列平台](http://superzms.kp.ziroom.com) 完成消息队列申请之后，由 ZMS 平台生成。

在申请 Topic 后，ZMS 平台会将对应的 `rocketm-{cluster-name}.yaml` 配置文件推送至 Omega 发布平台进行托管，可在相关应用的 `应用 > 环境 > 配置文件`页面找到。

TODO: 添加系统截图

应用在部署至 Omega 平台后，可在运行时直接读取到该文件，无需人工管理。