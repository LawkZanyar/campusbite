package com.campusbite.slots;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.campusbite.checkout.InMemoryOrderRepository;
import com.campusbite.common.BusinessException;
import com.campusbite.common.NotFoundException;

/**
 * M6 Pickup Slots (owner: Hananeh).
 * Unit tests for SlotService, SlotGenerator and the in-memory slot store.
 */
class SlotServiceTest {

    private InMemorySlotRepository repository;
    private SlotService slotService;
    private final LocalDate today = LocalDate.now();

    @BeforeEach
    void setUp() {
        repository = new InMemorySlotRepository(new InMemoryOrderRepository());
        slotService = new SlotService(repository, new SlotGenerator());
    }

    private SlotForm form(LocalDate date, int fromHour, int toHour, int capacity) {
        SlotForm form = new SlotForm();
        form.setDate(date);
        form.setFrom(LocalTime.of(fromHour, 0));
        form.setTo(LocalTime.of(toHour, 0));
        form.setCapacity(capacity);
        return form;
    }

    @Test
    void todayHasTwelveSlotsFrom11To14() {
        List<PickupSlot> slots = slotService.listForDate(today);

        assertThat(slots).hasSize(12);
        assertThat(slots.get(0).getLabel()).isEqualTo("11:00 - 11:15");
        assertThat(slots.get(11).getEnd()).isEqualTo(LocalTime.of(14, 0));
    }

    @Test
    void usedCountsTheSampleOrders() {
        PickupSlot noon = slotService.findById(5L);      // 12:00 has two sample orders

        assertThat(noon.getUsed()).isEqualTo(2);
        assertThat(noon.getRemaining()).isEqualTo(18);
    }

    @Test
    void generatorMakesTwelveSlotsBetween11And14() {
        List<PickupSlot> slots = new SlotGenerator().generate(today, LocalTime.of(11, 0), LocalTime.of(14, 0), 15, 20);

        assertThat(slots).hasSize(12);
        assertThat(slots.get(11).getStart()).isEqualTo(LocalTime.of(13, 45));
    }

    @Test
    void generateSkipsSlotsThatAlreadyExist() {
        int created = slotService.generateForDate(form(today, 13, 15, 20));   // 13:00-14:00 exist

        assertThat(created).isEqualTo(4);                                      // only 14:00-15:00
        assertThat(slotService.listForDate(today)).hasSize(16);
    }

    @Test
    void generateRejectsStartAfterEnd() {
        assertThatThrownBy(() -> slotService.generateForDate(form(today.plusDays(3), 14, 11, 20)))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("before the end time");
    }

    @Test
    void slotStartingIn10MinutesIsNotAvailableButIn20MinutesIs() {
        LocalDateTime now = today.atTime(11, 50);              // 12:00 is 10 min away, 12:15 is 25 min away

        List<PickupSlot> available = slotService.findAvailable(today, now);

        assertThat(available.get(0).getLabel()).isEqualTo("12:15 - 12:30");
        assertThatThrownBy(() -> slotService.checkCutoff(slotService.findById(5L), now))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void fullSlotIsNotAvailableAndFailsTheCapacityCheck() {
        repository.updateCapacity(5L, 2);                       // 12:00 already has 2 orders

        LocalDateTime early = today.atTime(9, 0);
        assertThat(slotService.findAvailable(today, early)).noneMatch(s -> s.getId().equals(5L));
        assertThatThrownBy(() -> slotService.checkCapacityForUpdate(5L))
                .isInstanceOf(SlotFullException.class)
                .hasMessageContaining("12:00 - 12:15");
    }

    @Test
    void checkoutOffersTodayAndTomorrow() {
        List<PickupSlot> slots = slotService.findAvailableForCheckout(today.atTime(9, 0));

        assertThat(slots).hasSize(24);
    }

    @Test
    void unknownSlotThrowsNotFound() {
        assertThatThrownBy(() -> slotService.findById(999L)).isInstanceOf(NotFoundException.class);
    }
}
