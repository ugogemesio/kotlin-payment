package gemesio.ugo.payment_service.payment

import gemesio.ugo.payment_service.order.OrderEvent
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class PaymentConsumer(
    private val kafkaTemplate: KafkaTemplate<String, Any>
) {
    @KafkaListener(topics = ["orders"], groupId = "payment-service")
    fun consumeOrderEvent(event: OrderEvent) {
        println("Processando pagamento para pedido: ${event.orderId}")
        // Simulação de processamento
        val paymentStatus = if (event.amount > 0.toBigDecimal()) "SUCCESS" else "FAILED"
        val paymentEvent = PaymentEvent(event.orderId, paymentStatus, "Pagamento processado")
        println(paymentEvent)
        kafkaTemplate.send("payments", event.orderId, paymentEvent)
    }
}