package com.polarbookshop.order_service.book;

import java.time.Duration;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.polarbookshop.order_service.order.domain.OrderService;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Component
@Slf4j
public class BookClient {
	
	 private static final String BOOKS_ROOT_API = "/books/";
	 private final WebClient webClient;
	 
	 public BookClient(WebClient webClient) {
		this.webClient = webClient;
	 }
	 
	 public Mono<Book> getBookByIsbn(String isbn) {
		log.info("Fetching the Book details with ISBN - {} from the catalog service api - {} ", isbn, BOOKS_ROOT_API);
		return webClient
				.get()
				.uri(BOOKS_ROOT_API + isbn)
				.retrieve()
				.bodyToMono(Book.class)
				.timeout(Duration.ofSeconds(3), Mono.empty())
				.onErrorResume(WebClientResponseException.NotFound.class, _ -> Mono.empty() )
				.retryWhen(
						Retry.backoff(3, Duration.ofMillis(100)))
				.onErrorResume(Exception.class, _ -> Mono.empty()
				);
		 
	 }
	 
	 @CircuitBreaker(name = "catalogService", fallbackMethod = "getBookByIsbnFallback")
	 @io.github.resilience4j.retry.annotation.Retry(name = "getCatalogService", fallbackMethod = "getBookByIsbnFallback")
	 @TimeLimiter(name = "catalogService", fallbackMethod = "getBookByIsbnFallback")
	 @RateLimiter(name = "catalogService") //It is client-side rate limiter controls out-going traffic.
	 public Mono<Book> getBookByIsbnWithFallback(String isbn) {
		log.info("Fetching the Book details with ISBN - {} from the catalog service api - {} ", isbn, BOOKS_ROOT_API);
		return webClient
				.get()
				.uri(BOOKS_ROOT_API + isbn)
				.retrieve()
				.bodyToMono(Book.class)
				.onErrorResume(Exception.class, _ -> Mono.empty());
								 
	 }
	 
	 
	 public Mono<Book> getBookByIsbnWithFallback(String isbn, Throwable cause) {
			log.info("Fetching the Book details failed with ISBN - {} from the catalog service api - {} and the failuredetails are - {} ", isbn, BOOKS_ROOT_API, cause.getMessage());
			return Mono.empty();
			 
		 }

}
