package com.campusbite.checkout;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * M5 Cart & Checkout (owner: Hananeh) - CONTRACT.
 * Used by checkout (Hananeh), the kitchen board and order history (Fatemeh)
 * and reviews (Pasha). Implemented by InMemoryOrderRepository (Phase 1) and
 * JdbcOrderRepository (MySQL, Week 08).
 */
public interface OrderRepository {

    /** Inserts the order and all its items, sets their ids and returns the order. */
    Order save(Order order);

    /** One order with its items, empty if the id does not exist. */
    Optional<Order> findById(Long id);

    /**
     * Same as findById, but in MySQL it also LOCKS the order row (SELECT ... FOR UPDATE)
     * until the current transaction ends. Used before changing an order's status, so
     * two kitchen staff clicking at the same moment cannot both change it.
     */
    Optional<Order> findByIdForUpdate(Long id);

    /** All orders of one student, newest first. */
    List<Order> findByUserId(Long userId);

    /** All orders whose pickup slot is on this date, sorted by slot time then creation time. */
    List<Order> findBySlotDate(LocalDate date);

    /** Changes the status column of one order. */
    void updateStatus(Long orderId, OrderStatus status);

    /** True if this user has a COLLECTED order that contains this menu item (review rule). */
    boolean hasCollectedItem(Long userId, Long menuItemId);
}
