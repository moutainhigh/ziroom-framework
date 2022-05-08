package com.ziroom.framework.autoconfigure.web;

import java.util.LinkedHashSet;
import java.util.Set;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by liangrk on 2022/5/5.
 */
@ConfigurationProperties(prefix = "ziroom.framework.weblog")
@Data
public class WebLogProperties {

    private Set<String> basePackages = new LinkedHashSet<>();

}
