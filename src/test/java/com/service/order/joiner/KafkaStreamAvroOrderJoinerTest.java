package com.service.order.joiner;

import com.service.avro.model.AvroOrder;
import com.service.avro.model.AvroOrderStatus;
import com.service.avro.model.AvroProduct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by olehtsipotan on 07.02.2024.
 */
public class KafkaStreamAvroOrderJoinerTest {

    public KafkaStreamAvroOrderJoiner kafkaStreamAvroOrderJoiner;

    // This method provides the parameters for the test
    static Stream<Arguments> orderStatusProvider() {
        return Stream.of(Arguments.of(AvroOrderStatus.REJECT, AvroOrderStatus.ACCEPT),
                Arguments.of(AvroOrderStatus.ACCEPT, AvroOrderStatus.REJECT));
    }

    @BeforeEach
    public void setUp() {
        kafkaStreamAvroOrderJoiner = new KafkaStreamAvroOrderJoiner();
    }

    @Test
    public void apply_whenStockOrderAndPaymentOrderStatusIsReject_returnAvroOrderWithRollbackStatus() {
        AvroProduct avroProduct = new AvroProduct();
        avroProduct.setId(1L);
        avroProduct.setPrice(100L);
        avroProduct.setQuantity(1);

        AvroOrder order = new AvroOrder();
        order.setId("someId");
        order.setCustomerId(1L);
        order.setSource("someSource");
        order.setProduct(avroProduct);
        order.setStatus(AvroOrderStatus.REJECT);

        AvroOrder joinedOrder = kafkaStreamAvroOrderJoiner.apply(order, order);

        assertEquals(AvroOrderStatus.ROLLBACK, joinedOrder.getStatus());
        assertEquals(order.getId(), joinedOrder.getId());
        assertEquals(order.getCustomerId(), joinedOrder.getCustomerId());
        assertEquals(order.getSource(), joinedOrder.getSource());
        assertEquals(order.getProduct(), joinedOrder.getProduct());
    }

    @Test
    public void apply_whenStockOrderAndPaymentOrderStatusIsAccept_returnAvroOrderWithConfirmationStatus() {
        AvroProduct avroProduct = new AvroProduct();
        avroProduct.setId(1L);
        avroProduct.setPrice(100L);
        avroProduct.setQuantity(1);

        AvroOrder order = new AvroOrder();
        order.setId("someId");
        order.setCustomerId(1L);
        order.setSource("someSource");
        order.setProduct(avroProduct);
        order.setStatus(AvroOrderStatus.ACCEPT);

        AvroOrder joinedOrder = kafkaStreamAvroOrderJoiner.apply(order, order);

        assertEquals(AvroOrderStatus.CONFIRMATION, joinedOrder.getStatus());
        assertEquals(order.getId(), joinedOrder.getId());
        assertEquals(order.getCustomerId(), joinedOrder.getCustomerId());
        assertEquals(order.getSource(), joinedOrder.getSource());
        assertEquals(order.getProduct(), joinedOrder.getProduct());
    }

    @ParameterizedTest
    @MethodSource("orderStatusProvider")
    public void apply_whenOneOrderIsRejectedAndOtherOneIsAccepted_returnAvroOrderWithRollbackStatus(
            AvroOrderStatus stockOrderStatus, AvroOrderStatus paymentOrderStatus) {
        AvroProduct avroProduct = new AvroProduct();
        avroProduct.setId(1L);
        avroProduct.setPrice(100L);
        avroProduct.setQuantity(1);

        AvroOrder stockOrder = new AvroOrder();
        stockOrder.setId("someId");
        stockOrder.setCustomerId(1L);
        stockOrder.setSource("someSource");
        stockOrder.setProduct(avroProduct);
        stockOrder.setStatus(stockOrderStatus);

        AvroOrder paymentOrder = new AvroOrder();
        paymentOrder.setId("someId");
        paymentOrder.setCustomerId(1L);
        paymentOrder.setSource("someSource");
        paymentOrder.setProduct(avroProduct);
        paymentOrder.setStatus(paymentOrderStatus);

        AvroOrder joinedOrder = kafkaStreamAvroOrderJoiner.apply(stockOrder, paymentOrder);

        assertEquals(AvroOrderStatus.ROLLBACK, joinedOrder.getStatus());
        assertEquals(stockOrder.getId(), joinedOrder.getId());
        assertEquals(stockOrder.getCustomerId(), joinedOrder.getCustomerId());
        assertEquals(stockOrder.getSource(), joinedOrder.getSource());
        assertEquals(stockOrder.getProduct(), joinedOrder.getProduct());
    }

    @Test
    public void apply_whenOrderSourceIsNeitherAcceptNorReject_returnAvroOrderWithNewStatus() {
        AvroProduct avroProduct = new AvroProduct();
        avroProduct.setId(1L);
        avroProduct.setPrice(100L);
        avroProduct.setQuantity(1);

        AvroOrder order = new AvroOrder();
        order.setId("someId");
        order.setCustomerId(1L);
        order.setSource("someSource");
        order.setProduct(avroProduct);
        order.setStatus(AvroOrderStatus.NEW);

        AvroOrder joinedOrder = kafkaStreamAvroOrderJoiner.apply(order, order);

        assertEquals(AvroOrderStatus.ROLLBACK, joinedOrder.getStatus());
        assertEquals(order.getId(), joinedOrder.getId());
        assertEquals(order.getCustomerId(), joinedOrder.getCustomerId());
        assertEquals(order.getSource(), joinedOrder.getSource());
        assertEquals(order.getProduct(), joinedOrder.getProduct());
    }
}
