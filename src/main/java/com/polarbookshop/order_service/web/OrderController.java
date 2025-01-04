package com.polarbookshop.order_service.web;

import java.util.Objects;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.polarbookshop.order_service.domain.Order;
import com.polarbookshop.order_service.domain.OrderService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("orders")
@Slf4j
public class OrderController {

	private final OrderService orderService;

	public OrderController(OrderService orderService) {
		this.orderService = orderService;
	}

	@GetMapping
	public Flux<Order> getAllOrders(@AuthenticationPrincipal Jwt jwt , @RequestParam(name = "status", required = false) String bookStatus) {
		log.info("Fetching all orders for the user");
		return Objects.isNull(bookStatus) ? orderService.getAllOrders(jwt.getSubject()) 
				                          : orderService.viewOrderByStatus(bookStatus);
	}
	
	@GetMapping("{isbn}")
	public Flux<Order> findByIsbn(@PathVariable String isbn) {
		log.info("Fetching all orders with ISBN - {}", isbn);
		return orderService.viewOrderDetails(isbn);
	}
	
	
	@PostMapping
	public Mono<Order> submitOrder(@RequestBody @Valid OrderRequest orderRequest) {
		log.info("Order for {} copies of the book with ISBN {}", orderRequest.quantity(), orderRequest.isbn());
		return orderService.submitOrder(
					orderRequest.isbn(), orderRequest.quantity());
	}

}
