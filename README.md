# 基础组件 （ ziroom-framework ）
自如框架项目starter组件集合

## ziroom-exception-starter
统一异常定义

## ziroom-http-starter
统一HTTP接口定义和filter

版本

> Test环境 -> test-1.0.0-SNAPSHOT

> QA环境 -> prev-1.0.0-SNAPSHOT

> 线上环境 -> prod-1.0.0


```
    <dependency>
        <groupId>com.ziroom.framework</groupId>
        <artifactId>ziroom-http-starter</artifactId>
        <version>${ziroom-http-starter.version}</version>
    </dependency>
    
```

## ziroom-swagger-starter
引用swagger的V2.9.2，通过自定义的swagger.properties，来简化swagger的接入

## ziroom-swagger-dubbo
扩展swagger采集dubbo接口

## ziroom-springcloud-dependencies
> packaging parent pom.xml

* 集成spring-cloud-dependencies (Hoxton.SR12)
* 集成spring-cloud-alibaba-dependencies (2.2.6.RELEASE)
* 继承spring-boot-dependencies (2.1.18.RELEASE)

版本

> Test环境 -> test-1.0.0-SNAPSHOT

> QA环境 -> prev-1.0.0-SNAPSHOT

> 线上环境 -> prod-1.0.0

 
```
    <parent>
        <groupId>com.ziroom.framework</groupId>
        <artifactId>ziroom-springcloud-dependencies</artifactId>
        <version>test-1.0.0-SNAPSHOT</version>
        <relativePath />
    </parent>
    
```


## ziroom-framework-examples
各组件使用样例

* ziroom-framework-dubbo-example
* ziroom-framework-spring-boot-example
* ziroom-framework-spring-cloud-example
  * ziroom-framework-spring-cloud-consumer-example
  * ziroom-framework-spring-cloud-provider-example
  * ziroom-framework-spring-cloud-sentinel-example
* ziroom-springcloud-dependencies-example
