package com.campusbite.slots;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.campusbite.checkout.InMemoryOrderRepository;
import com.campusbite.common.NotFoundException;

/**
 * M6 Pickup Slots (owner: Hananeh).
 * Unit tests for SlotService and the in-memory slot store.
 */
class SlotServiceTest {

    private SlotService slotService;

    @BeforeEach
    void setUp() {
        InMemorySlotRepository repository = new InMemorySlotRepository(new InMemoryOrderRepository());
        slotService = new SlotService(repository);
    }

    @Test
    void todayHasTwelveSlotsFrom11To14() {
        List<PickupSlot> slots = slotService.listForDate(LocalDate.now());

        assertThat(slots).hasSize(12);
        assertThat(slots.get(0).getStart()).isEqualTo(LocalTime.of(11, 0));
        assertThat(slots.get(11).getEnd()).isEqualTo(LocalTime.of(14, 0));
        assertThat(slots.get(0).getLabel()).isEqualTo("11:00 - 11:15");
    }

    @Test
    void usedCountsTheSampleOrders() {
        PickupSlot noon = slotService.findById(5L);      // 12:00 has two sample orders

        assertThat(noon.getUsed()).isEqualTo(2);
        assertThat(noon.getRemaining()).isEqualTo(18);
        assertThat(noon.isFull()).isFalse();
    }

    @Test
    void remainingNeverGoesBelowZero() {
        PickupSlot slot = new PickupSlot(LocalDate.now(), LocalTime.NOON, LocalTime.NOON.plusMinutes(15), 1);
        slot.setUsed(3);

        assertThat(slot.getRemaining()).isZero();
        assertThat(slot.isFull()).isTrue();
    }

    @Test
    void unknownSlotThrowsNotFound() {
        assertThatThrownBy(() -> slotService.findById(999L)).isInstanceOf(NotFoundException.class);
    }
}
