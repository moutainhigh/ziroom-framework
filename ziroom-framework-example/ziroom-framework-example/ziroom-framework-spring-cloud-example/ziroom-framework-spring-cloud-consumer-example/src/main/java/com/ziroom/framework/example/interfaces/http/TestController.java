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

package com.ziroom.framework.example.interfaces.http;

import com.ziroom.framework.example.infrastructure.service.ConsumerApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
public class TestController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RestTemplate restTemplate1;


    @Autowired
    private ConsumerApplication.MycenterService mycenterService;

    @Autowired
    private DiscoveryClient discoveryClient;

    @PostMapping("/mycenter-feign/api/mycenter/base/config")
    public Object mycenterFeignConfig() {
        return mycenterService.config();
    }

    @PostMapping("/mycenter-rest/api/mycenter/base/config")
    public String mycenterRestConfig() {
        return restTemplate.postForObject("http://mycenter/api/mycenter/base/config", null,
                String.class);
    }


}
