package com.ziroom.framework.module.web.log;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

@Data
@ConfigurationProperties(prefix = "ziroom.web.accesslog")
public class WebAccessLogProperties {

    public static final String HEADER_UID = "uid";
    public static final String HEADER_ZIROOM_GATEWAY_EMPLOYEEID = "ziroom-gateway-employeeid";

    private boolean enabled = true;

    private Duration tp95Threshold = Duration.ofSeconds(1);

    private Set<String> includeHeaders = new HashSet<>();


    private boolean includeRequestBody = true;
    private int maxRequestBodyLength = 1024 * 2;

    public WebAccessLogProperties() {
        this.includeHeaders.add(HEADER_UID);
        this.includeHeaders.add(HEADER_ZIROOM_GATEWAY_EMPLOYEEID);
    }

}
