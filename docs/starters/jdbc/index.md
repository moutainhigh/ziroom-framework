# 基础组件 （ ziroom-framework ）
自如框架项目jdbc-starter组件使用指南

版本

> Test环境 -> test-1.0.0-SNAPSHOT

> QA环境 -> prev-1.0.0-SNAPSHOT

> 线上环境 -> prod-1.0.0


```
    <dependency---
title: 数据源
---

# {% $markdoc.frontmatter.title %}

## 快速上手
### 1. 引入依赖

```xml
<dependency>
    <groupId>com.ziroom.framework</groupId>
    <artifactId>ziroom-framework-starter-jdbc</artifactId>
    <version>{% $version.release %}</version>
</dependency>
```

### 2. 添加数据库配置
在 src/main/resources 中添加名为 `datasource-demo.yaml` 配置文件:

```yaml
ziroom:
  datasource:
    name: merak
    password: ziroomdb
    url: jdbc:mysql://10.30.7.107:3306/merak?useUnicode=true&serverTimezone=GMT&useSSL=false&characterEncoding=utf8
    username: dev_jcpt
    type: com.zaxxer.hikari.HikariDataSource
    hikari:
      connection-init-sql: select 1
      maximum-pool-size: 100
      minimum-idle: 10
```

### 3. 开始使用
以 MyBatis 为例

```java
@Configuration
public class DataSourceConfig  {

    @Autowired
    private DataSource dataSource;

    @Bean
    public SqlSessionFactory sqlSessionFactory(@Autowired  DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setMapperLocations(new ClassPathResource("mapper/RoleMapper.xml"));
        return bean.getObject();
    }

    @Bean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) throws Exception{
        return new SqlSessionTemplate(sqlSessionFactory);
    }

}
```

ziroom-framework-starter-jdbc 会扫描 classpath 中所有符合 `datasource-*.yaml` 或者 `datasource-*.yml` 的配置文件,
并根据他们的配置生成 `DataSource` bean 并注册进 spring ApplicationContext 中.

## 配置说明

```yaml
ziroom:
  datasource:
    # 数据库名称，会根据该字段生成 bean 名称 "${ziroom.database.name}DataSource"
    name: merak

    # 链接信息
    url: jdbc:mysql://10.30.7.107:3306/ump?useUnicode=true&serverTimezone=GMT&useSSL=false&characterEncoding=utf8
    password: ziroomdb
    username: dev_jcpt

    # DataSource 类型，默认 com.zaxxer.hikari.HikariDataSource
    # 可选值: com.zaxxer.hikari.HikariDataSource, org.apache.tomcat.jdbc.pool.DataSource, org.apache.commons.dbcp2.BasicDataSource
    type: com.zaxxer.hikari.HikariDataSource

    # hikari 数据源参数
    # 完整配置参数参考 com.zaxxer.hikari.HikariConfig 字段
    hikari:
        # ...

    dbcp2:
        # ...

    tomcat:
        # ...
    
    druid:
        # ...

    # 
```

### 单数据源
当 ClassPath 中仅存在一个 `datasource-*.yaml` 文件时，`ziroom-framework-jdbc` 会将相关配置参数以 `spring.datasource` 为前缀注册到 spring environment 中。

即会覆盖应用在 `application.properties` 中 `spring.datasource` 下的配置参数。

假如当前同时存在以下配置:


**application.properties**
```properties

spring.datasource.url=jdbc:mysql://10.30.7.101:3306/hire_sign?useSSL=false&useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&zeroDateTimeBehavior=convertToNull
spring.datasource.username=dev_crm
spring.datasource.password=ziroomdb
```

**datasource-hire_sign.yaml**
```yaml
ziroom:
  datasource:
    # 数据库名称，会根据该字段生成 bean 名称 "${ziroom.database.name}DataSource"
    # 链接信息
    url: jdbc:mysql://10.30.7.107:3306/hire_sign?useUnicode=true&serverTimezone=GMT&useSSL=false&characterEncoding=utf8
    password: ziroomdb
    username: dev_jcpt
```

则最终应用使用的数据库为 **datasource-hire_sign.yaml** 中声明的 `10.30.7.107:3306/hire_sign`，而不是 `application.properties` 中声明的 `10.30.7.101:3306/hire_sign`

### 多数据源
当 classpath 中同时存在多个 `datasource-*.yaml` 文件时，`ziroom-framework-jdbc` 不会对已有配置做任何调整，应用可通过 `ziroom.datasource.name`DataSource 引用对应的数据源。

**配置文件结构**

```
[classpath]/
    └── datasource-db1.yaml
    └── datasource-db2.yaml
```

**配置文件内容**
```yaml

# datasource-db1.yaml
ziroom:
  datasource:
    name: db1
    url: jdbc:mysql://10.30.7.107:3306/merak?useUnicode=true&serverTimezone=GMT&useSSL=false&characterEncoding=utf8
    password: ziroomdb
    username: dev_jcpt

---

# datasource-db2.yaml
ziroom:
  datasource:
    name: db2
    url: jdbc:mysql://10.30.7.101:3306/hire_sign?useUnicode=true&serverTimezone=GMT&useSSL=false&characterEncoding=utf8
    password: ziroomdb
    username: dev_jcpt

```

**引用 DataSource bean**

```java
@Configuration
public class DataSourceConfig  {

    @Autowired
    @Qualifier("db1DataSource")
    private DataSource dataSource;


    @Autowired
    @Qualifier("db2DataSource")
    private DataSource dataSource;

    @Bean
    public SqlSessionFactory sqlSessionFactory1(@Autowired @@Qualifier("db1DataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        // ...
        return bean.getObject();
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory2(@Autowired @@Qualifier("db2DataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        // ...
        return bean.getObject();
    }

}


```

## 雅典娜平台集成
上文中的 `datasource-*.yaml` 配置文件可由雅典娜平台进行管理。

当用户在[雅典娜平台](http://db.ziroom.com)申请完数据库，并完成应用绑定后，雅典娜会将数据库链接信息推送至 Omega 平台。可在 `应用 > 环境 > 配置文件 - 数据库配置` 中找到。

>
        <groupId>com.ziroom.framework</groupId>
        <artifactId>ziroom-framework-starters</artifactId>
    </dependency>
    
```

## ziroom-framework-jdbc-starter说明
雅典娜（db平台）推送数据库配置信息至omega系统，ziroom-framework-jdbc-starter读取omega系统数据库配置，自动注入datasource spring applicationContext中，
单数据源和多数据源情况说明：
```
1、单数据源时，omega数据源自动覆盖系统application.yaml中配置的数据源，omega数据源时，系统配置生效，系统主动注入spring applicationContext
,数据源名称为默认值datasource
2、多数据源时，必须在omega配置主数据源,系统配置数据源不生效
```

## ziroom-framework-jdbc-starter使用
各组件使用样例

* pom继承parent
```
  <parent>
        <groupId>com.ziroom.framework</groupId>
        <artifactId>ziroom-framework-dependencies</artifactId>
        <version>test-1.0.0-SNAPSHOT</version>
        <relativePath />
    </parent>
```
* pom添加ziroom-framework-jdbc-starter依赖
```
    <dependency>
        <groupId>com.ziroom.framework</groupId>
        <artifactId>ziroom-framework-starters</artifactId>
    </dependency>
```
* 单数据源
  无需特殊配置，直接使用mybatis等
* 多数据源 需要手动配置数据链接
  *beanFactory.getBean("omega") omega为数据源名称，自动设置成数据库名称 
```
@Configuration
//@MapperScan(basePackages = {"com.ziroom.tech.omega.example.dao"}, sqlSessionFactoryRef = "omegaSqlSessionFactory")
public class MerakDataSourceConfig {

    @Autowired
    private BeanFactory beanFactory;

    @Primary
    @Bean(name = "omegaSqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource((DataSource)beanFactory.getBean("omega"));
//        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/*.xml"));
        return bean.getObject();
    }

    @Primary
    @Bean(name = "merakTransactionManager")
    public DataSourceTransactionManager transactionManager() {
        return new DataSourceTransactionManager((DataSource)beanFactory.getBean("omega"));
    }

    @Primary
    @Bean(name = "merakSqlSessionTemplate")
    public SqlSessionTemplate sqlSessionTemplate() throws Exception{
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource((DataSource)beanFactory.getBean("omega"));
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/*.xml"));
        SqlSessionFactory sqlSessionFactory = (SqlSessionFactory) bean.getObject();
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
```
* omega配置示例（测试，可直接放在项目classpath下面）
  *文件名称omega-datasource.yaml 
 ```
 appId: merak
config:
  classify: master
  primary: true
prefix: jdbc
properties:
  name: merak
  password: 
  url: jdbc:mysql://127.0.0.1:3306/merak?useUnicode=true&serverTimezone=GMT&useSSL=false&characterEncoding=utf8
  username: dev_
  type: com.zaxxer.hikari.HikariDataSource
  driver-class-name: com.mysql.cj.jdbc.Driver
hikari:
  connection-init-sql: select 1
  maximum-pool-size: 100
  minimum-idle: 10
 ```