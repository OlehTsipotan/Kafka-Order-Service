package com.service.order.config;

import com.service.avro.model.AvroOrder;
import com.service.order.service.AvroOrderStreamProcessor;
import io.confluent.kafka.streams.serdes.avro.GenericAvroSerde;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.StreamJoined;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;


/**
 * Created by olehtsipotan on 03.02.2024.
 */
@Configuration
@EnableKafkaStreams
@Slf4j
public class KafkaStreamsConfig {

    @Value("${spring.kafka.properties.schema.registry.url}")
    private String schemaRegistryUrl;

    @Autowired
    private AvroOrderStreamProcessor avroOrderStreamProcessor;

    @Bean
    public Serde<AvroOrder> avroOrderSerde() {
        final Map<String, String> serdeConfig = Collections.singletonMap("schema.registry.url", schemaRegistryUrl);

        final Serde<AvroOrder> valueSerde = new SpecificAvroSerde<>();
        valueSerde.configure(serdeConfig, true);
        return valueSerde;
    }

    @Bean
    public KStream<String, AvroOrder> stream(StreamsBuilder builder) {
        final Serde<AvroOrder> avroOrderSerde = avroOrderSerde();
        KStream<String, AvroOrder> stream =
                builder.stream("payment-orders", Consumed.with(Serdes.String(), avroOrderSerde));

        stream.join(builder.stream("stock-orders"),
                        avroOrderStreamProcessor::process,
                        JoinWindows.of(Duration.ofSeconds(10)),
                        StreamJoined.with(Serdes.String(), avroOrderSerde, avroOrderSerde))
                .peek((k, o) -> log.info("Output: {}", o)).to("orders");

        return stream;
    }
}
