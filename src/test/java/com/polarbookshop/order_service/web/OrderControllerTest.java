package com.polarbookshop.order_service.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.polarbookshop.order_service.config.SecurityConfig;
import com.polarbookshop.order_service.order.domain.Order;
import com.polarbookshop.order_service.order.domain.OrderService;
import com.polarbookshop.order_service.order.domain.OrderStatus;
import com.polarbookshop.order_service.order.web.OrderController;
import com.polarbookshop.order_service.order.web.OrderRequest;

import reactor.core.publisher.Mono;

@WebFluxTest(OrderController.class)
@Import(SecurityConfig.class)
class OrderControllerTest {
	
	@Autowired
	private WebTestClient webClient;
	
	@MockitoBean
	private OrderService orderService;
	
	@MockitoBean
	private ReactiveJwtDecoder reactiveJwtDecoder;

	@Test
	@Disabled
	void whenBookNotAvailableThenRejectOrder() {
		var orderRequest = new OrderRequest("1234567890", 3);
		var expectedOrder = Order.of(orderRequest.isbn(), null, 0, 3, OrderStatus.REJECTED);
		when(orderService.submitOrder(orderRequest.isbn(), orderRequest.quantity()))
			.thenReturn(Mono.just(expectedOrder));
		
		this.webClient
		    .mutateWith(mockJwt().authorities(new SimpleGrantedAuthority("ROLE_customer")))
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
