package com.polarbookshop.order_service.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.polarbookshop.order_service.domain.Order;
import com.polarbookshop.order_service.domain.OrderService;
import com.polarbookshop.order_service.domain.OrderStatus;

import reactor.core.publisher.Mono;

@WebFluxTest(OrderController.class)
class OrderControllerTest {
	
	@Autowired
	private WebTestClient webClient;
	
	@MockitoBean
	private OrderService orderService;

	@Test
	void whenBookNotAvailableThenRejectOrder() {
		var orderRequest = new OrderRequest("1234567890", 3);
		var expectedOrder = Order.of(orderRequest.isbn(), null, 0, 3, OrderStatus.REJECTED);
		when(orderService.submitOrder(orderRequest.isbn(), orderRequest.quantity()))
			.thenReturn(Mono.just(expectedOrder));
		
		this.webClient
		    .post()
		    .uri("/orders")
		    .bodyValue(orderRequest)
		    .exchange()
		    .expectStatus().is2xxSuccessful()
		    .expectBody(Order.class).value(order -> {
		    		assertThat(order).isNotNull();
		    		assertThat(order.status()).isEqualTo(OrderStatus.REJECTED);
		    });
	}

}
