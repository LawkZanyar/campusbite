package com.campusbite.checkout;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

/**
 * M5 Cart & Checkout (owner: Hananeh).
 * Phase 1 storage for orders ("memory" profile only). Starts with 4 sample orders
 * for today, all from the demo student (user id 1), each with a different status,
 * so the kitchen board and order history have something to show.
 * The slot ids 5, 6, 7 match InMemorySlotRepository (12:00, 12:15, 12:30 today).
 */
@Repository
@Profile("memory")
public class InMemoryOrderRepository implements OrderRepository {

    private final Map<Long, Order> orders = new LinkedHashMap<>();
    private long nextOrderId = 1;
    private long nextItemId = 1;

    /** Creates the store with the 4 sample orders. */
    public InMemoryOrderRepository() {
        LocalDate today = LocalDate.now();
        addSample(today, 5L, LocalTime.of(12, 0), OrderStatus.PLACED, "K7P3QX", "No onions please",
                new OrderItem(1L, "Chicken biryani", 1, 5000), new OrderItem(10L, "Ayran", 1, 750));
        addSample(today, 5L, LocalTime.of(12, 0), OrderStatus.PREPARING, "M4ZT8A", null,
                new OrderItem(6L, "Falafel wrap", 2, 2500));
        addSample(today, 6L, LocalTime.of(12, 15), OrderStatus.READY, "H2WD9R", null,
                new OrderItem(7L, "Chicken shawarma", 1, 3500), new OrderItem(11L, "Black tea", 1, 500));
        addSample(today, 7L, LocalTime.of(12, 30), OrderStatus.COLLECTED, "R8N5CB", null,
                new OrderItem(14L, "Baklava (3 pieces)", 1, 2000));
    }

    /** Helper for the constructor: builds and saves one sample order. */
    private void addSample(LocalDate day, Long slotId, LocalTime slotStart, OrderStatus status,
                           String code, String note, OrderItem... lines) {
        Order order = new Order();
        order.setUserId(1L);
        order.setUserName("Demo Student");
        order.setSlotId(slotId);
        order.setSlotStart(LocalDateTime.of(day, slotStart));
        order.setSlotEnd(LocalDateTime.of(day, slotStart.plusMinutes(15)));
        order.setStatus(status);
        order.setPickupCode(code);
        order.setNote(note);
        order.setCreatedAt(LocalDateTime.of(day, LocalTime.of(9, 0)).plusMinutes(nextOrderId * 7));
        int total = 0;
        for (OrderItem line : lines) {
            order.getItems().add(line);
            total += line.getLineTotal();
        }
        order.setTotalIqd(total);
        save(order);
    }

    @Override
    public synchronized Order save(Order order) {
        order.setId(nextOrderId);
        nextOrderId++;
        for (OrderItem item : order.getItems()) {
            item.setId(nextItemId);
            nextItemId++;
            item.setOrderId(order.getId());
        }
        orders.put(order.getId(), order);
        return order;
    }

    @Override
    public synchronized Optional<Order> findById(Long id) {
        return Optional.ofNullable(orders.get(id));
    }

    @Override
    public synchronized Optional<Order> findByIdForUpdate(Long id) {
        // In memory there is no row to lock; "synchronized" already lets one thread in at a time.
        return findById(id);
    }

    @Override
    public synchronized List<Order> findByUserId(Long userId) {
        List<Order> result = new ArrayList<>();
        for (Order order : orders.values()) {
            if (order.getUserId().equals(userId)) {
                result.add(order);
            }
        }
        result.sort(Comparator.comparing(Order::getCreatedAt).reversed());
        return result;
    }

    @Override
    public synchronized List<Order> findBySlotDate(LocalDate date) {
        List<Order> result = new ArrayList<>();
        for (Order order : orders.values()) {
            if (order.getSlotStart().toLocalDate().equals(date)) {
                result.add(order);
            }
        }
        result.sort(Comparator.comparing(Order::getSlotStart).thenComparing(Order::getCreatedAt));
        return result;
    }

    @Override
    public synchronized void updateStatus(Long orderId, OrderStatus status) {
        Order order = orders.get(orderId);
        if (order != null) {
            order.setStatus(status);
        }
    }

    @Override
    public synchronized boolean hasCollectedItem(Long userId, Long menuItemId) {
        for (Order order : orders.values()) {
            if (order.getUserId().equals(userId) && order.getStatus() == OrderStatus.COLLECTED) {
                for (OrderItem item : order.getItems()) {
                    if (item.getMenuItemId().equals(menuItemId)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
