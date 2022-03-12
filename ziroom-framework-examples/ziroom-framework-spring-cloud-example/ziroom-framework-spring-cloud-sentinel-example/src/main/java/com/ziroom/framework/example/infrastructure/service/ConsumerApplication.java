/*
 * Copyright 2013-2018 the original author or authors.
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

package com.ziroom.framework.example.infrastructure.service;

import com.alibaba.cloud.sentinel.annotation.SentinelRestTemplate;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;

@Component
public class ConsumerApplication {

    @LoadBalanced
    @Bean
    @SentinelRestTemplate(urlCleanerClass = UrlCleaner.class, urlCleaner = "clean")
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @LoadBalanced
    @Bean
    @SentinelRestTemplate
    public RestTemplate restTemplate1() {
        return new RestTemplate();
    }

    @FeignClient(name = "mycenter", fallback = MycenterServiceFallback.class, configuration = FeignConfiguration.class)
    public interface MycenterService {

        @PostMapping("/api/mycenter/base/config")
        String config();

    }
}

class FeignConfiguration {
    @Bean
    public MycenterServiceFallback mycenterServiceFallback() {
        return new MycenterServiceFallback();
    }

}

class MycenterServiceFallback implements ConsumerApplication.MycenterService {

    @Override
    public String config() {
        return "config fallback";
    }

}