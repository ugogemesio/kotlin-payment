package gemesio.ugo.order_service.order


import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/orders")
class OrderController(
    private val orderService: OrderProducer
) {
    @PostMapping
    fun createOrder(@RequestBody request: OrderRequest): ResponseEntity<OrderResponse> {
        val order = orderService.createOrder(request)
        return ResponseEntity.ok(order)
    }
}