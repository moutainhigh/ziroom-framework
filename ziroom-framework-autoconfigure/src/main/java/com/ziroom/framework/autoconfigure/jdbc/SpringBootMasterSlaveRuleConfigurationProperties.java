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

import org.apache.shardingsphere.core.yaml.config.masterslave.YamlMasterSlaveRuleConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Master-slave rule configuration properties.
 *
 * @author caohao
 */
@ConfigurationProperties(prefix = "gear.jdbc.config.masterslave")
public class SpringBootMasterSlaveRuleConfigurationProperties extends YamlMasterSlaveRuleConfiguration {
}