package com.polarbookshop.order_service.domain;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.polarbookshop.order_service.config.DataConfig;

import reactor.test.StepVerifier;

@DataR2dbcTest
@Testcontainers
@Import(DataConfig.class)
class OrderRepositoryR2dbcTest {
	
	@Container
	@ServiceConnection
	static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest");
	
	@Autowired
	private OrderRepository orderRepository;
	

	@Test
	void test() {
		var rejectedOrder =  Order.of("1234567890", null, 0.0, 3, OrderStatus.REJECTED);
		StepVerifier.create(orderRepository.save(rejectedOrder))
		 			.expectNextMatches(order -> order.status() == OrderStatus.REJECTED)
		 			.verifyComplete();
	}

}
