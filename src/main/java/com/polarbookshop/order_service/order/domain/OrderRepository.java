package com.polarbookshop.order_service.order.domain;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Flux;

public interface OrderRepository extends ReactiveCrudRepository<Order, Long>{

	Flux<Order> findByBookIsbn(String isbn);

	Flux<Order> findByStatus(OrderStatus status);

	Flux<Order> findAllByCreatedBy(String createdBy);
	

}
