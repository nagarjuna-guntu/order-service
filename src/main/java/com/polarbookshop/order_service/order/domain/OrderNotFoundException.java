package com.polarbookshop.order_service.order.domain;

public class OrderNotFoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public OrderNotFoundException(String isbn) {
        super("The Oder not Found with ISBN- " + isbn);
    }
	
	public OrderNotFoundException(Throwable cause) {
        super(cause);
    }

}
