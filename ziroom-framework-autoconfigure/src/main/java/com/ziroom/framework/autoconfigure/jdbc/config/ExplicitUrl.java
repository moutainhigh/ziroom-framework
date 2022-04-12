package com.ziroom.framework.autoconfigure.jdbc.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ConditionalOnProperty(prefix = "spring.datasource", name = "url")
@Component
public class ExplicitUrl {
}
