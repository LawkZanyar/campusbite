package com.campusbite.slots;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * M6 Pickup Slots (owner: Hananeh) - CONTRACT.
 * Reads and changes pickup slots. Implemented by InMemorySlotRepository (Phase 1)
 * and JdbcSlotRepository (MySQL, Week 08).
 */
public interface SlotRepository {

    /** All slots of one day, sorted by start time, each with its "used" count filled in. */
    List<PickupSlot> findByDate(LocalDate date);

    /** One slot with its "used" count, empty if the id does not exist. */
    Optional<PickupSlot> findById(Long id);

    /** True if a slot already starts at this date and time. */
    boolean exists(LocalDate date, LocalTime start);

    /** Inserts a new slot, sets its id and returns it. */
    PickupSlot save(PickupSlot slot);

    /** Changes the capacity of one slot. */
    void updateCapacity(Long slotId, int capacity);

    /** Deletes one slot. */
    void delete(Long slotId);

    /** Orders in this slot that are not CANCELLED (they take a place). */
    int countActiveOrders(Long slotId);

    /** All orders in this slot, any status (a slot with orders cannot be deleted). */
    int countAllOrders(Long slotId);

    /**
     * Locks the slot row until the current transaction ends (SELECT ... FOR UPDATE in MySQL),
     * so two students cannot take the last place at the same moment.
     */
    void lockSlot(Long slotId);
}
