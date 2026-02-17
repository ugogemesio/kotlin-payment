package gemesio.ugo.order_service.order

import java.math.BigDecimal

data class OrderResponse(
    val orderId: String,
    val customerId: String,
    val amount: BigDecimal,
    val message: String
)