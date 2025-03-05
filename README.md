# order-service
Demo functionality for purchasing books.

# API First Approach

1. Submits a new order for a given book in a given quantity</br>

	POST: /orders
	Request body :  OrderRequest
	Response body:  Orders
 
2. Retrieves all the orders</br>

	GET: /orders
	Response body:  Orders[]
	
# pub/sub model (spring-cloud-stream with rabbitMQ)

1. When an order is accepted:</br>

   	– Order Service should notify interested consumers of the event (order-accepted-event).</br>
   	– Dispatcher Service should execute some logic to dispatch the order.</br>
   	
2. When an order is dispatched:</br>

   	– Dispatcher Service should notify consumers interested in such an event(order-dispatched-event).</br>
   	– Order Service should update the order status in the data


