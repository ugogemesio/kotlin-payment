package gemesio.ugo.order_service.order

import java.math.BigDecimal

data class OrderEvent(
    val orderId: String,
    val customerId: String,
    val amount: BigDecimal,
    val status: String
)