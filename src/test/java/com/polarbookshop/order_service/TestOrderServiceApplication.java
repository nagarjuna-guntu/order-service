package com.polarbookshop.order_service;

import org.springframework.boot.SpringApplication;
import com.polarbookshop.order_service.config.TestContainersConfig;

public class TestOrderServiceApplication {

	public static void main(String[] args) {
		SpringApplication.from(OrderServiceApplication::main)
						 .with(TestContainersConfig.class).run(args);
	}

}
