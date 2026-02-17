package gemesio.ugo.order_service.order

import java.math.BigDecimal
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive

data class OrderRequest(
    @field:NotBlank(message = "Customer ID é obrigatório")
    val customerId: String,

    @field:Positive(message = "Amount deve ser positivo")
    val amount: BigDecimal
)