/*
 * Copyright 2012-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ziroom.framework.autoconfigure.jdbc;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.bind.BindHandler;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.ResolvableType;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Objects;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for {@link DataSource}.
 *
 * @author Dave Syer
 * @author Phillip Webb
 * @author Stephane Nicoll
 * @author Kazuki Shimizu
 * @since 1.0.0
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({ DataSource.class, EmbeddedDatabaseType.class })
@AutoConfigureBefore(DataSourceAutoConfiguration.class)
@Import({ OmegaConfigRegistrar.class})
@ConditionalOnMissingBean(type = "io.r2dbc.spi.ConnectionFactory")
//@EnableConfigurationProperties({DataSourceProperties.class})
public class ZiRoomDataSourceAutoConfiguration implements InitializingBean {
    //application.yaml不存在datasource配置时，不加载

//    @Bean
//    @ConfigurationProperties(prefix = "spring.datasource.hikari")

    @Autowired
    ConfigurableApplicationContext applicationContext;

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, DataSource> dataSources = applicationContext.getBeansOfType(DataSource.class);
        if (!Objects.isNull(dataSources) && dataSources.size() > 0) {
            ConfigurationPropertiesBinder binder = new ConfigurationPropertiesBinder(this.applicationContext);
            BindHandler bindHandler = binder.getHandler();
            for (Map.Entry<String, DataSource> entry : dataSources.entrySet()) {
                switch (entry.getKey()) {
                    case "org.apache.tomcat.jdbc.pool.DataSource":
                        ResolvableType bindType = ResolvableType.forClass(org.apache.tomcat.jdbc.pool.DataSource.class);
                        Bindable<Object> bindTarget = Bindable.of(bindType).withAnnotations(null);
                        if (entry.getValue() != null) {
                            bindTarget = bindTarget.withExistingValue(entry.getValue());
                        }
                        binder.getBinder().bind("spring.datasource.tomcat", bindTarget, bindHandler);
                        break;
                    case "org.apache.commons.dbcp2.BasicDataSource":
                        bindType = ResolvableType.forClass(org.apache.commons.dbcp2.BasicDataSource.class);
                        bindTarget = Bindable.of(bindType).withAnnotations(null);
                        if (entry.getValue() != null) {
                            bindTarget = bindTarget.withExistingValue(entry.getValue());
                        }
                        binder.getBinder().bind("spring.datasource.dbcp2", bindTarget, bindHandler);
                        break;
                    default:
                        bindType = ResolvableType.forClass(com.zaxxer.hikari.HikariDataSource.class);
                        bindTarget = Bindable.of(bindType).withAnnotations(null);
                        if (entry.getValue() != null) {
                            bindTarget = bindTarget.withExistingValue(entry.getValue());
                        }
                        binder.getBinder().bind("spring.datasource.hikari", bindTarget, bindHandler);
                }
            }
        }
    }
}
