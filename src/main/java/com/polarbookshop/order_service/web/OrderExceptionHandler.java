package com.polarbookshop.order_service.web;


import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.polarbookshop.order_service.domain.OrderNotFoundException;

import reactor.core.publisher.Mono;

@ControllerAdvice
public class OrderExceptionHandler {
	
	@ExceptionHandler
	public Mono<ProblemDetail> handleBookNotFound(OrderNotFoundException exception) {
		var details = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
		return Mono.just(details);
		
	}

}
