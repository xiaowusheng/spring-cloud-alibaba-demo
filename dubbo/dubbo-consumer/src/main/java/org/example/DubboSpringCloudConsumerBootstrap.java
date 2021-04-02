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

package org.example;

import com.alibaba.cloud.dubbo.annotation.DubboTransported;
import org.apache.dubbo.config.annotation.DubboReference;
import org.example.service.EchoService;
import org.example.service.RestService;
import org.example.service.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Dubbo Spring Cloud Client Bootstrap.
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 */
@EnableDiscoveryClient
@EnableAutoConfiguration
@EnableFeignClients
@RestController
public class DubboSpringCloudConsumerBootstrap {

    @DubboReference
    private EchoService echoService;

    @DubboReference(version = "1.0.0", protocol = "dubbo")
    private RestService restService;

    @Autowired
    @Lazy
    private FeignRestService feignRestService;

    @Autowired
    @Lazy
    private DubboFeignRestService dubboFeignRestService;

    @Value("${provider.application.name}")
    private String providerApplicationName;

    @Autowired
    @LoadBalanced
    private RestTemplate restTemplate;

    @GetMapping("/echo")
    public String echo(String message) {
        return echoService.echo(message);
    }


    @GetMapping("/pathVariables")
    public String pathVariables() {

        System.out.println(restService.pathVariables("a", "b", "c"));

        System.out.println(dubboFeignRestService.pathVariables("c", "b", "a"));

        return "success";
    }

    @GetMapping("/callHeaders")
    public String callHeaders() {

        System.out.println(restService.headers("a", "b", 10));

        System.out.println(dubboFeignRestService.headers("b", 10, "a"));
        return "success";
    }

    @GetMapping("/callParam")
    public String callParam() {

        System.out.println(restService.param("mercyblitz"));

        System.out.println(dubboFeignRestService.param("mercyblitz"));

//        System.out.println(feignRestService.param("mercyblitz"));

        return "success";
    }

    @GetMapping("/callParams")
    public String callParams() {


        System.out.println(restTemplate.getForEntity(
                "http://" + providerApplicationName + "/param?param=小马哥", String.class));

        return "success";
    }

    @GetMapping("/callRequestBodyMap")
    public String callRequestBodyMap() {

        Map<String, Object> data = new HashMap<>();
        data.put("id", 1);
        data.put("name", "小马哥");
        data.put("age", 33);


        System.out.println(restService.requestBodyMap(data, "Hello,World"));

        System.out.println(dubboFeignRestService.requestBody("Hello,World", data));

        return "success";
    }

    public static void main(String[] args) {
        SpringApplication.run(DubboSpringCloudConsumerBootstrap.class);
    }

    @Bean
    @LoadBalanced
    @DubboTransported
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @FeignClient("${provider.application.name}")
    public interface FeignRestService {

        @GetMapping("/param")
        String param(@RequestParam("param") String param);

        @PostMapping("/params")
        String params(@RequestParam("b") String b, @RequestParam("a") int a);

        @PostMapping(value = "/request/body/map", produces = APPLICATION_JSON_VALUE)
        User requestBody(@RequestParam("param") String param,
                         @RequestBody Map<String, Object> data);

        @GetMapping("/headers")
        String headers(@RequestHeader("h2") String header2,
                       @RequestHeader("h") String header, @RequestParam("v") Integer value);

        @GetMapping("/path-variables/{p1}/{p2}")
        String pathVariables(@PathVariable("p2") String path2,
                             @PathVariable("p1") String path1, @RequestParam("v") String param);

    }

    @FeignClient("${provider.application.name}")
    @DubboTransported(protocol = "dubbo")
    public interface DubboFeignRestService {

        @GetMapping("/param")
        String param(@RequestParam("param") String param);

        @PostMapping("/params")
        String params(@RequestParam("b") String paramB, @RequestParam("a") int paramA);

        @PostMapping(value = "/request/body/map", produces = APPLICATION_JSON_UTF8_VALUE)
        User requestBody(@RequestParam("param") String param,
                         @RequestBody Map<String, Object> data);

        @GetMapping("/headers")
        String headers(@RequestHeader("h2") String header2,
                       @RequestParam("v") Integer value, @RequestHeader("h") String header);

        @GetMapping("/path-variables/{p1}/{p2}")
        String pathVariables(@RequestParam("v") String param,
                             @PathVariable("p2") String path2, @PathVariable("p1") String path1);

    }
}
