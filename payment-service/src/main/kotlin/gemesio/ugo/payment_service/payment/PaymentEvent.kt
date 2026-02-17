package gemesio.ugo.payment_service.payment

data class PaymentEvent(
    val orderId: String,
    val status: String,
    val message: String?
)