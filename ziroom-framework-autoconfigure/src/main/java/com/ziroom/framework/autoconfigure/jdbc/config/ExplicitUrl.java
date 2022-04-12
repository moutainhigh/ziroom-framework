package com.ziroom.framework.autoconfigure.jdbc.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ConditionalOnProperty(prefix = "spring.datasource", name = "url")
@Configuration
public class ExplicitUrl {
}
