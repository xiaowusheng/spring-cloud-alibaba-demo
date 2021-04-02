package org.example.service;

import org.apache.dubbo.config.annotation.DubboService;

@DubboService(protocol = { "dubbo" })
class EchoServiceImpl implements EchoService {

	@Override
	public String echo(String message) {
		return "[echo] Hello, " + message;
	}

}