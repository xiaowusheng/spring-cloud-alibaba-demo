package org.example.service;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@DubboService(protocol = { "dubbo" })
@RefreshScope
class EchoServiceImpl implements EchoService {

	@Value("${name:sp-alibaba}")
	private String name;

	@Override
	@SentinelResource(value = "sayHello")
	public String echo(String message) {
		return name + "[echo] Hello, " + message;
	}

}