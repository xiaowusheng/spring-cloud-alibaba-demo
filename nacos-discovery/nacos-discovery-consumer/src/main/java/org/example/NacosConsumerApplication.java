package org.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class NacosConsumerApplication {

    @LoadBalanced
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @FeignClient(name = "service-provider")
    public interface EchoService {
        @GetMapping(value = "/echo/{str}")
        String echo(@PathVariable("str") String str);
    }


    @FeignClient(name = "service-gateway")
    public interface EchoServiceGateWay {
        @GetMapping(value = "/nacos/echo/{str}")
        String echo(@PathVariable("str") String str);
    }

    public static void main(String[] args) {
        SpringApplication.run(NacosConsumerApplication.class, args);
    }

    @RestController
    public class TestController {

        private RestTemplate restTemplate;

        @Autowired
        private EchoService echoService;

        @Autowired
        private EchoServiceGateWay echoServiceGateWay;

        @Autowired
        public TestController(RestTemplate restTemplate) {this.restTemplate = restTemplate;}

        @RequestMapping(value = "/echo/{str}", method = RequestMethod.GET)
        public String echo(@PathVariable String str) {
            return restTemplate.getForObject("http://service-provider/echo/" + str, String.class);
        }

        @GetMapping(value = "/echo-feign/{str}")
        public String feign(@PathVariable String str) {
            return echoService.echo(str);
        }

        @GetMapping(value = "/echo-gateway/{str}")
        public String gateway(@PathVariable String str) {
            return echoServiceGateWay.echo(str);
        }
    }
}