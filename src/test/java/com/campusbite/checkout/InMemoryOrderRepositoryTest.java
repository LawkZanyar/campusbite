package com.campusbite.checkout;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * M5 Cart & Checkout (owner: Hananeh).
 * Checks the order contract with the in-memory store.
 */
class InMemoryOrderRepositoryTest {

    private InMemoryOrderRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryOrderRepository();
    }

    @Test
    void startsWithFourSampleOrdersForToday() {
        List<Order> today = repository.findBySlotDate(LocalDate.now());

        assertThat(today).hasSize(4);
        assertThat(today.get(0).getSlotLabel()).isEqualTo("12:00 - 12:15");
    }

    @Test
    void saveGivesIdsToOrderAndItems() {
        Order order = new Order();
        order.setUserId(1L);
        order.setSlotId(1L);
        order.setSlotStart(LocalDate.now().atTime(11, 0));
        order.setSlotEnd(LocalDate.now().atTime(11, 15));
        order.setStatus(OrderStatus.PLACED);
        order.setCreatedAt(LocalDate.now().atTime(10, 0));
        order.getItems().add(new OrderItem(4L, "Lentil soup", 2, 1500));
        order.setTotalIqd(3000);

        Order saved = repository.save(order);

        assertThat(saved.getId()).isEqualTo(5L);
        assertThat(saved.getItems().get(0).getOrderId()).isEqualTo(5L);
        assertThat(saved.getTotalLabel()).isEqualTo("3,000 IQD");
    }

    @Test
    void updateStatusChangesTheOrder() {
        repository.updateStatus(1L, OrderStatus.PREPARING);

        assertThat(repository.findById(1L).get().getStatus()).isEqualTo(OrderStatus.PREPARING);
    }

    @Test
    void hasCollectedItemOnlyForCollectedOrders() {
        assertThat(repository.hasCollectedItem(1L, 14L)).isTrue();   // baklava, COLLECTED
        assertThat(repository.hasCollectedItem(1L, 1L)).isFalse();   // biryani, only PLACED
        assertThat(repository.hasCollectedItem(2L, 14L)).isFalse();  // another user
    }

    @Test
    void findByUserIdIsNewestFirst() {
        List<Order> mine = repository.findByUserId(1L);

        assertThat(mine).hasSize(4);
        assertThat(mine.get(0).getCreatedAt()).isAfter(mine.get(3).getCreatedAt());
    }
}
