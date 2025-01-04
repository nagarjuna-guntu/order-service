package com.polarbookshop.order_service.domain;

import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.polarbookshop.order_service.book.Book;
import com.polarbookshop.order_service.book.BookClient;
import com.polarbookshop.order_service.order.OrderAcceptedMessage;
import com.polarbookshop.order_service.order.OrderDispatchedMessage;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class OrderService {
	
	private final BookClient bookClient;
	private final OrderRepository orderRepository;
	private final StreamBridge streamBridge;
	
	
	public OrderService(BookClient bookClient, OrderRepository orderRepository, StreamBridge streamBridge) {
		this.bookClient = bookClient;
		this.orderRepository = orderRepository;
		this.streamBridge = streamBridge;
	}
	
	public Flux<Order> getAllOrders(String userId) {  
		 return orderRepository.findAllByCreatedBy(userId);
    }
	
	@Transactional
	public Mono<Order> submitOrder(String isbn, int quantity) {
		  return getBookFromCatalogbyIsbn(isbn)
				    .map(book -> buildAcceptedOrder(book, quantity))
				    .defaultIfEmpty(buildRejectedOrder(isbn, quantity))
				    .flatMap(orderRepository::save)
				    .doOnNext(this::publishOrderAcceptedEvent);	     
	}
	
	private Mono<Book> getBookFromCatalogbyIsbn(String isbn) {
		var result = bookClient.getBookByIsbn(isbn);
		result.doOnNext(book -> log.info("Result from book service {}", book));
		return result;
	}

	private void publishOrderAcceptedEvent(Order order) {
		if(order.status().equals(OrderStatus.ACCEPTED)) {
			var orderAcceptedMessage = new OrderAcceptedMessage(order.id());
			log.info("Sending order accepted event with id: {}", order.id());
			var result = streamBridge.send("acceptOrder-out-0", orderAcceptedMessage);
			log.info("Result of sending data for order with id {}: {}", order.id(), result);
		}			
	}

	private static Order buildAcceptedOrder(Book book, int quantity) {
		return Order.of(book.isbn(), book.title() + " - " + book.author(), book.price(), quantity, OrderStatus.ACCEPTED);
	}

	private static Order buildRejectedOrder(String bookIsbn, int quantity) {
		log.info("Building Rejected Order with Book ISBN - {} ", bookIsbn);
		 var rejectedOrder = Order.of(bookIsbn, null, 0.0, quantity, OrderStatus.REJECTED);
		 return rejectedOrder;
	}

	public Flux<Order> consumeOrderDispatchedEvent(Flux<OrderDispatchedMessage> flux) {
		
		return flux.flatMap(orderDispatchedMessage ->
			 				orderRepository.findById(orderDispatchedMessage.orderId()))  
			       .map(this::buildDispatchedOrder)    
			       .flatMap(orderRepository::save);
	}
	
	private Order buildDispatchedOrder(Order existingOrder) {
		return new Order(
				existingOrder.id(),
				existingOrder.bookIsbn(),
				existingOrder.bookName(),
				existingOrder.bookPrice(),
				existingOrder.quantity(),
				OrderStatus.DISPATCHED,
				existingOrder.createdDate(),
				existingOrder.lastModifiedDate(),
				existingOrder.createdBy(),
				existingOrder.lastModifiedBy(),
				existingOrder.version()
		);
	}

	public Flux<Order> viewOrderDetails(String isbn) {
		return orderRepository.findByBookIsbn(isbn)
				.switchIfEmpty(Flux.error(() -> new OrderNotFoundException(isbn)));
	}
	
	public Flux<Order> viewOrderByStatus(String status) {
		return orderRepository.findByStatus(OrderStatus.valueOf(status))
				.switchIfEmpty(Flux.error(() -> new OrderNotFoundException(status)));
	}

}
