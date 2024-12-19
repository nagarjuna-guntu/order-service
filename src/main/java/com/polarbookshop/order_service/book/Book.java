package com.polarbookshop.order_service.book;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Book(
		 String isbn,
		 String title,
		 String author,
		 double price
		) {

}
