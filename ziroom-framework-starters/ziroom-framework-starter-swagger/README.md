# 基础组件
## rent-swagger-starter
引用swagger的V2.9.2，通过自定义的swagger.properties，来简化swagger的接入。
目的
* 统一swagger版本;
* 统一swagger的JSON访问路径和swagger-ui;
* 统一扩展swagger对dubbo接口的采集

### 接入方法
1. 引入POM
     ```
    <dependency>
        <groupId>com.ziroom.framework</groupId>
        <artifactId>rent-swagger-starter</artifactId>
        <version>${rent-swagger-starter.version}</version>
    </dependency>
     ```

    > <rent-swagger-starter.version>prod-1.0.0</rent-swagger-starter.version>
2. 自定义swagger.properties
    ```
    swagger.enable = true
    swagger.title = "service-name"
    swagger.description = "service-desc"
    swagger.version = "1.0"
    swagger.globalResponseMessageJson = {"POST":[{"code":20000,"message":"\u8bf7\u6c42\u6210\u529f","headers":{},"vendorExtensions":[]},{"code":50000,"message":"\u670d\u52a1\u5668\u5f02\u5e38","headers":{},"vendorExtensions":[]},{"code":200,"message":"\u65e0","headers":{},"vendorExtensions":[]}],"GET":[{"code":20000,"message":"\u8bf7\u6c42\u6210\u529f","headers":{},"vendorExtensions":[]},{"code":50000,"message":"\u670d\u52a1\u5668\u5f02\u5e38","headers":{},"vendorExtensions":[]},{"code":200,"message":"\u65e0","headers":{},"vendorExtensions":[]}]}
    ```
    
    说明：
    
    * 全居响应码swagger.globalResponseMessageJson
        > 如果成功返回码不是200,请加globalResponseMessageJson
        ```
        {
            "POST": [
                {
                "code": 20000,
                "message": "请求成功",
                "headers": {},
                "vendorExtensions": []
                },
                {
                "code": 50000,
                "message": "服务器异常",
                "headers": {},
                "vendorExtensions": []
                }
            ],
            "GET": [
                {
                "code": 20000,
                "message": "请求成功",
                "headers": {},
                "vendorExtensions": []
                },
                {
                "code": 50000,
                "message": "服务器异常",
                "headers": {},
                "vendorExtensions": []
                }
            ]
        }
    
        ```
   * 如果200为成功，则 {}
        > swagger.globalResponseMessageJson = {}
3. swagger访问路径
   * swagger-ui
    > http://domain/doc.html
   * swagger-http-json
    > http://domain/v2/doc
   * swagger-dubbo-json
    > http://domain/swagger-dubbo/api-docs