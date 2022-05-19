package com.ziroom.framework.module.web.response;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashSet;
import java.util.Set;

@Data
@ConfigurationProperties(prefix = "ziroom.web.response")
public class WebResponseProperties {

    private Enforce enforce = new Enforce();

    @Data
    public static class Enforce {
        /**
         * 是否强制约束所有 Web 返回值都必须使用 {@link com.ziroom.framework.common.api.pojo.ResponseData}  <br/>
         * 未返回 ResponseData 的接口，会通过 {@link EnforceResponseDataAdvice} 将原始返回值包含在 ResponseData.data 内
         */
        private boolean enabled = false;

        /**
         * 当 enforce = true 时，满足 excludePaths 的路径不会被 ResponseDataEnvelopeAdvice 处理
         */
        private Set<String> excludePaths = new HashSet<>();

        private Set<Class<?>> excludeTypes = new HashSet<>();
    }

}
