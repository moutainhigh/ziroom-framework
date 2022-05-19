package com.ziroom.framework.module.web.swagger;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ziroom.web.swagger")
@Data
public class WebSwaggerProperties {


    private boolean enabled = true;

    private String title;

    private String description;

    private String version = "1.0.0";

    private String groupName = "默认分组";

    private ApiSelector select = new ApiSelector(PathSelector.ANY, "com.ziroom");

    private String host;

    @Data
    public static class ApiSelector {
        private PathSelector path;
        private String basePackage;

        public ApiSelector(PathSelector path, String basePackage) {
            this.path = path;
            this.basePackage = basePackage;
        }

        public ApiSelector() {
        }
    }

    @Data
    public static class PathSelector {
        public static final PathSelector ANY = new PathSelector();
        private String regex;
        private String ant;
    }
}
