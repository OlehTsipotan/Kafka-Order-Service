package com.service.order.service;

import com.domain.avro.model.AvroOrder;
import com.domain.avro.model.AvroProduct;
import com.service.order.container.SchemaRegistryContainer;
import com.service.order.model.Order;
import com.service.order.model.OrderStatus;
import com.service.order.model.Product;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.specific.SpecificData;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.rnorth.ducttape.unreliables.Unreliables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class KafkaOrderProducerServiceIntegrationTest {

    public static final String CONFLUENT_PLATFORM_VERSION = "7.4.0";

    private static final Network KAFKA_NETWORK = Network.newNetwork();
    private static final DockerImageName KAFKA_IMAGE =
            DockerImageName.parse("confluentinc/cp-kafka").withTag(CONFLUENT_PLATFORM_VERSION);
    private static final KafkaContainer KAFKA =
            new KafkaContainer(KAFKA_IMAGE).withNetwork(KAFKA_NETWORK).withExposedPorts(9093).withKraft();

    private static final SchemaRegistryContainer SCHEMA_REGISTRY =
            new SchemaRegistryContainer(CONFLUENT_PLATFORM_VERSION);

    @Autowired
    private KafkaOrderProducerService kafkaOrderProducerService;

    @BeforeAll
    static void startKafkaContainer() {
        KAFKA.start();
        SCHEMA_REGISTRY.withKafka(KAFKA).start();
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", KAFKA::getBootstrapServers);
        registry.add("spring.kafka.producer.properties.schema.registry.url",
                () -> "http://" + SCHEMA_REGISTRY.getHost() + ":" + SCHEMA_REGISTRY.getFirstMappedPort());
    }

    @NotNull
    private static Order getOrder() {
        UUID id = UUID.randomUUID();
        Long customerId = 1L;
        String source = "Source";
        Long productId = 1L;
        Integer productQuantity = 1;
        Long productPrice = 100L;

        Product product = new Product();
        product.setId(productId);
        product.setQuantity(productQuantity);
        product.setPrice(productPrice);

        Order order = new Order();
        order.setId(id);
        order.setStatus(OrderStatus.NEW);
        order.setProduct(product);
        order.setSource(source);
        order.setCustomerId(customerId);
        return order;
    }

    @NotNull
    private static KafkaConsumer<String, GenericRecord> getStringGenericRecordKafkaConsumer(String bootstrapServers) {
        Properties props = new Properties();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "group");


        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                "io.confluent.kafka.serializers.KafkaAvroDeserializer");
        props.put("schema.registry.url",
                "http://" + SCHEMA_REGISTRY.getHost() + ":" + SCHEMA_REGISTRY.getFirstMappedPort());

        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        return new KafkaConsumer<>(props);
    }

    @Test
    public void sendOrder_whenOrderIsSentToKafka_success() {
        String topicName = "orders";
        String bootstrapServers = KAFKA.getBootstrapServers();

        Order order = getOrder();


        KafkaConsumer<String, GenericRecord> consumer = getStringGenericRecordKafkaConsumer(bootstrapServers);
        consumer.subscribe(Collections.singletonList(topicName));

        kafkaOrderProducerService.sendOrder(order);

        Unreliables.retryUntilTrue(20, TimeUnit.SECONDS, () -> {
            ConsumerRecords<String, GenericRecord> records = consumer.poll(Duration.ofMillis(100));
            if (records.isEmpty()) {
                return false;
            }
            for (ConsumerRecord<String, GenericRecord> record : records) {
                AvroOrder receivedOrder =
                        (AvroOrder) SpecificData.get().deepCopy(record.value().getSchema(), record.value());
                assertNotNull(receivedOrder);

                assertEquals(order.getId().toString(), receivedOrder.getId().toString());
                assertEquals(order.getCustomerId(), receivedOrder.getCustomerId());
                assertEquals(order.getStatus().toString(), receivedOrder.getStatus().toString());
                assertEquals(order.getSource(), receivedOrder.getSource().toString());

                AvroProduct avroProduct = receivedOrder.getProduct();
                assertNotNull(avroProduct);
                assertEquals(order.getProduct().getId(), avroProduct.getId());
                assertEquals(order.getProduct().getQuantity(), avroProduct.getQuantity());
                assertEquals(order.getProduct().getPrice(), avroProduct.getPrice());
            }
            return true;
        });
        consumer.unsubscribe();
    }
}

