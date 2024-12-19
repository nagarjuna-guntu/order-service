package com.polarbookshop.order_service.book;


import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class BookClientTest {
	
	private static MockWebServer mockBackEnd;
	private BookClient bookClient;
	
	@BeforeAll
	static void setUp() throws IOException {
		mockBackEnd = new MockWebServer();
		mockBackEnd.start();
	}
	
	@AfterAll
	static void tearDown() throws IOException {
		mockBackEnd.shutdown();
	}
	
	@BeforeEach
	void init() {
		 var webClient = WebClient.builder()        
			      .baseUrl(mockBackEnd.url("/").uri().toString())
			      .build();
		 this.bookClient = new BookClient(webClient);
	}
	

	@Test
	void whenBookExistsThenReturnBook() {
		var bookIsbn = "1234567890";

		var mockResponse = new MockResponse().addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.setBody("""
						{
							"isbn": %s,
						   "title": "Title",
						   "author": "Author",
						   "price": 9.90,
						   "publisher": "Polarsophia"
						}""".formatted(bookIsbn));

		mockBackEnd.enqueue(mockResponse); // Adds a mock response to the queue processed by the mock server.

		Mono<Book> book = bookClient.getBookByIsbn(bookIsbn);
		
		StepVerifier.create(book)
		   			.expectNextMatches(ele -> ele.isbn().equalsIgnoreCase(bookIsbn))
		   			.verifyComplete();
	}

}
