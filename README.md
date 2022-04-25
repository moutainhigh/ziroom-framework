# 基础组件 （ ziroom-framework ）

## 项目介绍
自如基础组件，提供了常用工具类，常用框架，并且打通的自如内部系统。

## quick start

两种集成方式 

### 方式1
该方式可对常见的***dependencies***和***plugin***进行版本的管理


步骤 1) 继承ziroom-framework-staters-parent
```xml
    <parent>
        <artifactId>ziroom-framework-starter-parent</artifactId>
        <groupId>com.ziroom.framework</groupId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>
```
步骤2）引入ziroom-framework-starter-web依赖
```xml
    <dependencies>
      <dependency>
        <groupId>com.ziroom.framework</groupId>
        <artifactId>ziroom-framework-starter-web</artifactId>
      </dependency>
    </dependencies>
```
步骤3）引入build插件
```xml
  <build>
   <plugins>
      <plugin>
         <groupId>org.apache.maven.plugins</groupId>
         <artifactId>maven-deploy-plugin</artifactId>
      </plugin>
   </plugins>
</build>
```
后续可通过脚手架可以直接构建。
http://start.kt.ziroom.com/

### 方式2 
该方式可对常见的***dependencies***进行版本的管理。与方式1的区别是，未对plugin的版本进行管理

步骤 1） 依赖ziroom-framework-dependencies
```xml
    <dependencyManagement>
      <dependency>
        <groupId>com.ziroom.framework</groupId>
        <artifactId>ziroom-framework-dependencies</artifactId>
        <version>1.0.0-SNAPSHOT</version>
      </dependency>
    </dependencyManagement>
```
步骤2）引入ziroom-framework-starter-web依赖
```xml
    <dependencies>
      <dependency>
        <groupId>com.ziroom.framework</groupId>
        <artifactId>ziroom-framework-starter-web</artifactId>
      </dependency>
    </dependencies>
```

步骤3）引入build插件（包含版本号）
```xml
  <build>
   <plugins>
      <plugin>
         <groupId>org.apache.maven.plugins</groupId>
         <artifactId>maven-deploy-plugin</artifactId>
         <version>2.3.2.RELEASE</version>
      </plugin>
   </plugins>
</build>
```



## 项目结构
1. ziroom-framework-autoconfigure: 自动配置包，如jdbc，redis，mq等自动配置的逻辑
2. ziroom-framework-common: 一些通用的类， 如utils，接口vo，dto等等
3. ziroom-ziroom-framework-dependencies: 全局依赖版本管理，包含了本项目的版本管理。以及依赖第三方的版本管理。
   1. 通过springboot-dependencies管理常用包版本， 
   2. springboot-dependencies未管理到的，单独添加。
4. ziroom-framework-examples：starter的示例代码
5. ziroom-framework-staters：用户依赖的starter
6. ziroom-framework-modules: 需要独立开发， 且相对复杂的模块

## 提交代码规范
1. 该项目需要编写单元测试，通过关联模块的单元测试，才可提交代码。
2. 功能复杂的模块，请添加readme.md文件
3. 为保证质量，push代码需要至少2名成员code review。
4. 验证通过后，推release包到maven仓库。

