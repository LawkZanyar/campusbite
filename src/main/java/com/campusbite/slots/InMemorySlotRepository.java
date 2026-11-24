package com.campusbite.slots;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.campusbite.checkout.Order;
import com.campusbite.checkout.OrderRepository;
import com.campusbite.checkout.OrderStatus;

/**
 * M6 Pickup Slots (owner: Hananeh).
 * Phase 1 storage for slots ("memory" profile only). Starts with slots every
 * 15 minutes from 11:00 to 14:00, capacity 20, for today (ids 1-12) and
 * tomorrow (ids 13-24). To know how many places are used it asks the
 * OrderRepository, just like the MySQL version will count rows in the orders table.
 */
@Repository
@Profile("memory")
public class InMemorySlotRepository implements SlotRepository {

    private final Map<Long, PickupSlot> slots = new LinkedHashMap<>();
    private final OrderRepository orderRepository;
    private long nextId = 1;

    /** Creates today's and tomorrow's demo slots. */
    public InMemorySlotRepository(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
        addDay(LocalDate.now());
        addDay(LocalDate.now().plusDays(1));
    }

    /** Helper: 12 slots of 15 minutes between 11:00 and 14:00. */
    private void addDay(LocalDate date) {
        LocalTime start = LocalTime.of(11, 0);
        while (start.isBefore(LocalTime.of(14, 0))) {
            save(new PickupSlot(date, start, start.plusMinutes(15), 20));
            start = start.plusMinutes(15);
        }
    }

    @Override
    public synchronized List<PickupSlot> findByDate(LocalDate date) {
        List<PickupSlot> result = new ArrayList<>();
        for (PickupSlot slot : slots.values()) {
            if (slot.getDate().equals(date)) {
                slot.setUsed(countActiveOrders(slot.getId()));
                result.add(slot);
            }
        }
        result.sort(Comparator.comparing(PickupSlot::getStart));
        return result;
    }

    @Override
    public synchronized Optional<PickupSlot> findById(Long id) {
        PickupSlot slot = slots.get(id);
        if (slot != null) {
            slot.setUsed(countActiveOrders(id));
        }
        return Optional.ofNullable(slot);
    }

    @Override
    public synchronized boolean exists(LocalDate date, LocalTime start) {
        for (PickupSlot slot : slots.values()) {
            if (slot.getDate().equals(date) && slot.getStart().equals(start)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public synchronized PickupSlot save(PickupSlot slot) {
        slot.setId(nextId);
        nextId++;
        slots.put(slot.getId(), slot);
        return slot;
    }

    @Override
    public synchronized void updateCapacity(Long slotId, int capacity) {
        PickupSlot slot = slots.get(slotId);
        if (slot != null) {
            slot.setCapacity(capacity);
        }
    }

    @Override
    public synchronized void delete(Long slotId) {
        slots.remove(slotId);
    }

    @Override
    public synchronized int countActiveOrders(Long slotId) {
        int count = 0;
        for (Order order : ordersOfSlot(slotId)) {
            if (order.getStatus() != OrderStatus.CANCELLED) {
                count++;
            }
        }
        return count;
    }

    @Override
    public synchronized int countAllOrders(Long slotId) {
        return ordersOfSlot(slotId).size();
    }

    @Override
    public void lockSlot(Long slotId) {
        // Nothing to lock in memory. The MySQL version runs SELECT ... FOR UPDATE.
    }

    /** Helper: the orders whose slot id is slotId. */
    private List<Order> ordersOfSlot(Long slotId) {
        List<Order> result = new ArrayList<>();
        PickupSlot slot = slots.get(slotId);
        if (slot == null) {
            return result;
        }
        for (Order order : orderRepository.findBySlotDate(slot.getDate())) {
            if (order.getSlotId().equals(slotId)) {
                result.add(order);
            }
        }
        return result;
    }
}
