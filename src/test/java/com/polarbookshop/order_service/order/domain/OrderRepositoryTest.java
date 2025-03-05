package com.polarbookshop.order_service.order.domain;

import java.util.Objects;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.polarbookshop.order_service.config.DataAuditConfig;
import com.polarbookshop.order_service.config.MyTestContainers;

import reactor.test.StepVerifier;

@DataR2dbcTest
@Import(DataAuditConfig.class)
@Testcontainers
@ImportTestcontainers(MyTestContainers.class)
class OrderRepositoryTest {
	
	@Autowired
	private OrderRepository orderRepository;
	

	@Test
	void test() {
		var rejectedOrder =  Order.of("1234567890", null, 0.0, 3, OrderStatus.REJECTED);
		StepVerifier.create(orderRepository.save(rejectedOrder))
		 			.expectNextMatches(order -> order.status() == OrderStatus.REJECTED)
		 			.verifyComplete();
	}
	
	@Test
    void createRejectedOrder() {
        var rejectedOrder = Order.of("1234567890", null, 0.0, 3, OrderStatus.REJECTED);
        StepVerifier.create(orderRepository.save(rejectedOrder))
                .expectNextMatches(order -> order.status().equals(OrderStatus.REJECTED))
                .verifyComplete();
    }

    @Test
    void whenCreateOrderNotAuthenticatedThenNoAuditMetadata() {
        var rejectedOrder = Order.of("1234567890", null, 0.0, 3, OrderStatus.REJECTED);
        StepVerifier.create(orderRepository.save(rejectedOrder))
                .expectNextMatches(order -> Objects.isNull(order.createdBy()) &&
                        Objects.isNull(order.lastModifiedBy()))
                .verifyComplete();
    }

    @Test
    @WithMockUser("nagarjuna")
    void whenCreateOrderAuthenticatedThenAuditMetadata() {
        var rejectedOrder = Order.of("1234567890", null, 0.0, 3, OrderStatus.REJECTED);
        StepVerifier.create(orderRepository.save(rejectedOrder))
                .expectNextMatches(order -> order.createdBy().equals("nagarjuna") &&
                        order.lastModifiedBy().equals("nagarjuna"))
                .verifyComplete();
    }


}
