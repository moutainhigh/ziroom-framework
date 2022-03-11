package com.ziroom.framework.swagger.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.*;

@Configuration
@EnableSwagger2
@PropertySource("classpath:swagger.properties")
public class SwaggerConfig {
    @Value("${swagger.enable}")
    private boolean swagger_enable;

    @Value("${swagger.title}")
    private String title;

    @Value("${swagger.description}")
    private String description;

    @Value("${swagger.version}")
    private String version;

    @Value("${swagger.globalResponseMessageJson}")
    private String globalResponseMessageJson;

    @Bean
    public Docket createRestApi() {
        Docket defaultDocket = new Docket(DocumentationType.SWAGGER_2)
                .enable(swagger_enable)
                .apiInfo(apiInfo());

        Optional<String> jsonOptional = Optional.of(globalResponseMessageJson);
        if (jsonOptional.isPresent()) {
            Optional<Map<RequestMethod, List<ResponseMessageEntity>>> globalResponseMessageOptional = Optional.of(JSON.parseObject(globalResponseMessageJson,
                    new TypeReference<HashMap<RequestMethod, List<ResponseMessageEntity>>>() {
                    }));
            if (globalResponseMessageOptional.isPresent()) {
                Map<RequestMethod, List<ResponseMessageEntity>> globalResponseMessages = globalResponseMessageOptional.get();

                for (Map.Entry<RequestMethod, List<ResponseMessageEntity>> entry : globalResponseMessages.entrySet()) {
                    RequestMethod requestMethod = entry.getKey();
                    List<ResponseMessageEntity> responseMessageEntitys = entry.getValue();
                    List<ResponseMessage> responseMessages = new ArrayList<>();
                    for (ResponseMessageEntity responseMessageEntity : responseMessageEntitys) {
                        responseMessages.add(new ResponseMessage(
                                responseMessageEntity.getCode(),
                                responseMessageEntity.getMessage(),
                                null,
                                new HashMap<>(),
                                new ArrayList<>()
                        ));
                    }
                    defaultDocket.globalResponseMessage(requestMethod, responseMessages);
                }
            }
        }

        return defaultDocket
                .select()
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(title)
                .description(description)
                .version(version)
                .contact(new Contact("admin",
                        "https://admin.ziroom.com",
                        "admin@ziroom.com"))
                .build();
    }
}
