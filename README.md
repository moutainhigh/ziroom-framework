# 基础组件 （ ziroom-framework ）

## 项目介绍
自如基础组件，提供了常用工具类，常用框架，并且打通的自如内部系统。

## quick start
两种集成方式 
### 1. 继承ziroom-framework-staters-parent
该方式可获取对dependencies和plugin版本的管理
```xml
    <parent>
        <artifactId>ziroom-framework-starter-parent</artifactId>
        <groupId>com.ziroom.framework</groupId>
        <version>1.0.0-SNAPSHOT</version>
        <relativePath>../pom.xml</relativePath>
    </parent>
```
引入依赖与build插件
```xml
<project>
    <dependencies>
      <dependency>
        <groupId>com.ziroom.framework</groupId>
        <artifactId>ziroom-framework-starter-web</artifactId>
      </dependency>
    </dependencies>
    <build>
      <plugins>
        <plugin>
          <groupId>org.apache.maven.plugins</groupId>
          <artifactId>maven-deploy-plugin</artifactId>
          <configuration>
            <skip>true</skip>
          </configuration>
        </plugin>
      </plugins>
    </build>
</project>
```
通过脚手架可以直接构建。

### 2. 依赖ziroom-framework-dependencies 
引入依赖与build插件  
```xml
<project>
    <dependencies>
      <dependency>
        <groupId>com.ziroom.framework</groupId>
        <artifactId>ziroom-framework-starter-web</artifactId>
      </dependency>
      <dependency>
        <groupId>com.ziroom.framework</groupId>
        <artifactId>ziroom-framework-dependencies</artifactId>
        <version>1.0.0-SNAPSHOT</version>
      </dependency>
    </dependencies>
    <build>
      <plugins>
        <plugin>
          <groupId>org.apache.maven.plugins</groupId>
          <artifactId>maven-deploy-plugin</artifactId>
          <configuration>
            <skip>true</skip>
          </configuration>
        </plugin>
      </plugins>
    </build>
</project>
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
3. develop 和 master为保护分支，push代码需要request merge，审批人赵耀、张宗启、梁仁凯
4. 验证通过后，推release包到maven仓库。

develop 和 master均为保护分支， push代码，在gitlab上提交request merge。
审批人： 赵耀，张宗启，梁仁凯

