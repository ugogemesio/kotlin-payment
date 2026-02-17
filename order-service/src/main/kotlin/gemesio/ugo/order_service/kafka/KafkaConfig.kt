package gemesio.ugo.order_service.kafka

import gemesio.ugo.order_service.order.OrderEvent
import gemesio.ugo.order_service.order.OrderEventSerializer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate

@Configuration
class KafkaConfig {

    @Bean
    fun producerFactory(): DefaultKafkaProducerFactory<String, OrderEvent> {
        val configProps = mapOf(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to "localhost:9092",
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to OrderEventSerializer::class.java
        )
        return DefaultKafkaProducerFactory(configProps)
    }


    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, OrderEvent> {
        return KafkaTemplate(producerFactory())
    }
}