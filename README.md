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

## 开发指南
### 项目结构
```
.
├── ziroom-framework-autoconfigure: 自动配置包，如jdbc，redis，mq等自动配置的逻辑
├── ziroom-framework-common: 一些通用的类， 如utils，接口vo，dto等等
├── ziroom-framework-dependencies: 全局依赖版本管理，包含了本项目的版本管理。以及依赖第三方的版本
├── ziroom-framework-examples：starter的示例代码
├── ziroom-framework-staters：用户依赖的starter
└── ziroom-framework-modules: 需要独立开发， 且相对复杂的模块
```

### 如何贡献代码

#### 分支模型
采用[主干开发模型](https://trunkbaseddevelopment.com/)，可发布的代码应直接提交进入主干。

#### 如何保证主干代码质量
项目维护者通常不应当直接向[主仓库](https://gitlab.ziroom.com/ziroom/framework/ziroom-framework) 提交代码，而应当通过主项目派生出个人项目（例如 [zhaoy13/ziroom-framework](https://gitlab.ziroom.com/zhaoy13/ziroom-framework)），并在个人项目中进行开发。代码开发完成后，通过 MergeRequest 申请合并到主仓库的 master 分支。

#### Review 流程
1. CI: 发起 MR 后会触发自动化 CI，等待 CI 运行完成，如有严重问题，请提交人先进行修改，直至满足代码检查要求。
2. Review：在发起 MR 后，需通过 1-2 位 Reviewer 审核，完成所有修改建议。
3. Merge：审核通过后，由 Repo Master（当前轮值人：@zhaoy13 @zhangzq8 @liangrk）负责合并到主干。

#### 版本发布
TODO

#### 代码规范
代码提交前应满足:
- [] 通过所有 checkstyle 风格检查
- [] 接入 SonarQube 扫描，没有 Blocker 级别的 issue
- [] TODO 单元测试规范