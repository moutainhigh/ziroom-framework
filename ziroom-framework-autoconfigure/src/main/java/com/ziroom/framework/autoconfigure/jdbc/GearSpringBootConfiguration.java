/*
 * Copyright 2016-2018 shardingsphere.io.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

package com.ziroom.framework.autoconfigure.jdbc;

import lombok.RequiredArgsConstructor;
import org.apache.shardingsphere.core.yaml.swapper.MasterSlaveRuleConfigurationYamlSwapper;
import org.apache.shardingsphere.core.yaml.swapper.ShardingRuleConfigurationYamlSwapper;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.jdbc.DataSourceHealthIndicator;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.sql.SQLException;

/**
 * Spring boot sharding and master-slave configuration.
 *
 * @author caohao
 */
@Configuration
@ComponentScan("org.apache.shardingsphere.spring.boot.converter")
@EnableConfigurationProperties({
        SpringBootShardingRuleConfigurationProperties.class,
        SpringBootMasterSlaveRuleConfigurationProperties.class,
        SpringBootPropertiesConfigurationProperties.class})
//@ConditionalOnProperty(prefix = "spring.shardingsphere", name = "enabled", havingValue = "true", matchIfMissing = true)
@AutoConfigureBefore(DataSourceAutoConfiguration.class)
@RequiredArgsConstructor
@Import({GearDefinition.class})
public class GearSpringBootConfiguration {


    private final SpringBootShardingRuleConfigurationProperties shardingRule;

    private final SpringBootMasterSlaveRuleConfigurationProperties masterSlaveRule;

    private final SpringBootPropertiesConfigurationProperties props;


    /**
     * Get data source bean.
     *
     * @return data source bean
     * @throws SQLException SQL exception
     */
    @Bean(name = {"dataSource", "gearDatasource"})
    public GearDatasource gearDatasource() {
        if (masterSlaveRule != null && masterSlaveRule.getName() != null) {
            GearContext.masterSlaveRuleConfiguration = new MasterSlaveRuleConfigurationYamlSwapper().swap(masterSlaveRule);
        }
        if (shardingRule != null && (!shardingRule.getTables().isEmpty())) {
            GearContext.shardingRuleConfiguration = new ShardingRuleConfigurationYamlSwapper().swap(shardingRule);
        }
        GearContext.gearConfigProps = props.getProps();

        return new GearDatasource();
    }

    @ConditionalOnClass(DataSourceHealthIndicator.class)
    @Bean
    public HealthIndicator dbHealthIndicator() {
        DataSourceHealthIndicator dataSourceHealthIndicator = new DataSourceHealthIndicator(gearDatasource());
        dataSourceHealthIndicator.setQuery("select 1");
        return dataSourceHealthIndicator;
    }
}
