package com.ziroom.framework.autoconfigure.web;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import com.ziroom.framework.module.web.swagger.WebSwaggerProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.ApiSelectorBuilder;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

@Configuration
@ConditionalOnClass(name = {"springfox.documentation.spring.web.plugins.Docket",
    "com.ziroom.framework.module.web.swagger.WebSwaggerProperties"})
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@EnableKnife4j
@EnableSwagger2WebMvc
@EnableConfigurationProperties(WebSwaggerProperties.class)
public class WebSwaggerAutoConfiguration {

    @Autowired
    private WebSwaggerProperties swaggerProperties;

    @Bean
    @ConditionalOnMissingBean
    public Docket defaultDocket() {
        Docket defaultDocket = new Docket(DocumentationType.SWAGGER_2)
            .enable(swaggerProperties.isEnabled())
            .groupName(swaggerProperties.getGroupName())
            .apiInfo(apiInfo())
            .host(swaggerProperties.getHost());

        WebSwaggerProperties.ApiSelector selectConfig = swaggerProperties.getSelect();
        ApiSelectorBuilder select = defaultDocket
            .select()
            .apis(RequestHandlerSelectors.basePackage(selectConfig.getBasePackage()));

        if (selectConfig.getPath().getRegex() != null) {
            select.paths(PathSelectors.regex(selectConfig.getPath().getRegex()));
        } else if (selectConfig.getPath().getAnt() != null) {
            select.paths(PathSelectors.ant(selectConfig.getPath().getAnt()));
        } else {
            select.paths(PathSelectors.any());
        }

        return select.build();
    }


    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
            .title(swaggerProperties.getTitle())
            .description(swaggerProperties.getDescription())
            .version(swaggerProperties.getVersion())
            .build();
    }
}
