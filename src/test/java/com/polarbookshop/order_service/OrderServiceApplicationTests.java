package com.polarbookshop.order_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.polarbookshop.order_service.config.MyTestContainers;


@SpringBootTest
@ImportTestcontainers(MyTestContainers.class)
@Testcontainers
class OrderServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
