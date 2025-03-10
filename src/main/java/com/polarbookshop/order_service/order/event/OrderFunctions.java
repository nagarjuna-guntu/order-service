package com.polarbookshop.order_service.order.event;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.polarbookshop.order_service.order.domain.OrderService;

import reactor.core.publisher.Flux;

@Configuration
public class OrderFunctions {
	 private static final Logger log =
			    LoggerFactory.getLogger(OrderFunctions.class);
	 
	 @Bean
	 Consumer<Flux<OrderDispatchedMessage>> dispatchOrder(OrderService orderService) {
		 return flux ->
	      orderService.consumeOrderDispatchedEvent(flux)  
	      			  .doOnNext(order -> log.info("The order with id {} is dispatched",
	      					  order.id())).subscribe();
	 }

}
