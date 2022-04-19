# 基础组件 （ ziroom-framework ）
自如框架项目jdbc-starter组件使用指南

版本

> Test环境 -> test-1.0.0-SNAPSHOT

> QA环境 -> prev-1.0.0-SNAPSHOT

> 线上环境 -> prod-1.0.0


```
    <dependency>
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