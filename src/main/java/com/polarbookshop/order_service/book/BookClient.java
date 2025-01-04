package com.polarbookshop.order_service.book;

import java.time.Duration;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

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

}
