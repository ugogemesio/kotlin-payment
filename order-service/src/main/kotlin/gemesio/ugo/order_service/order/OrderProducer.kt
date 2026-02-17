package gemesio.ugo.order_service.order

import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class OrderProducer(
    private val kafkaTemplate: KafkaTemplate<String, OrderEvent>
) {
    fun createOrder(request: OrderRequest): OrderResponse {
        val orderId = UUID.randomUUID().toString()
        val event = OrderEvent(orderId, request.customerId, request.amount, "CREATED")
        kafkaTemplate.send("orders", orderId, event)
        return OrderResponse(event.orderId, event.customerId, event.amount, "CREATED")
    }
}
