package gemesio.ugo.order_service.order

import org.apache.kafka.common.serialization.Serializer
import com.fasterxml.jackson.databind.ObjectMapper

class OrderEventSerializer : Serializer<OrderEvent> {
    private val objectMapper = ObjectMapper()
    override fun serialize(topic: String, data: OrderEvent?): ByteArray? {
        return data?.let { objectMapper.writeValueAsBytes(it) }
    }
}
