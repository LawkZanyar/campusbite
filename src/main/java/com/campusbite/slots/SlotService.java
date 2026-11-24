package com.campusbite.slots;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.campusbite.common.BusinessException;
import com.campusbite.common.NotFoundException;

/**
 * M6 Pickup Slots (owner: Hananeh) - CONTRACT used by checkout.
 * Business rules for pickup slots (business rule 2):
 * 15-minute slots, a capacity of active orders, and orders must be placed at
 * least 15 minutes before the slot starts.
 */
@Service
public class SlotService {

    /** Slot length in minutes. */
    public static final int SLOT_MINUTES = 15;
    /** An order must be placed at least this many minutes before its slot starts. */
    public static final int CUTOFF_MINUTES = 15;

    private final SlotRepository slotRepository;
    private final SlotGenerator slotGenerator;

    /** Spring passes the SlotRepository of the active profile and the generator. */
    public SlotService(SlotRepository slotRepository, SlotGenerator slotGenerator) {
        this.slotRepository = slotRepository;
        this.slotGenerator = slotGenerator;
    }

    /** All slots of a day, sorted by time, with used/remaining places. */
    public List<PickupSlot> listForDate(LocalDate date) {
        return slotRepository.findByDate(date);
    }

    /**
     * One slot.
     *
     * @throws NotFoundException if the id does not exist
     */
    public PickupSlot findById(Long id) {
        return slotRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Pickup slot " + id + " was not found."));
    }

    /**
     * Creates the slots of one day, skipping times that already have a slot.
     *
     * @return how many slots were created
     * @throws BusinessException if the start time is not before the end time
     */
    public int generateForDate(SlotForm form) {
        if (!form.getFrom().isBefore(form.getTo())) {
            throw new BusinessException("The start time must be before the end time.");
        }
        List<PickupSlot> slots = slotGenerator.generate(form.getDate(), form.getFrom(), form.getTo(),
                SLOT_MINUTES, form.getCapacity());
        int created = 0;
        for (PickupSlot slot : slots) {
            if (!slotRepository.exists(slot.getDate(), slot.getStart())) {
                slotRepository.save(slot);
                created++;
            }
        }
        return created;
    }

    /** True if a student may still choose this slot at time "now": a place is free and the cut-off is not passed. */
    public boolean isBookable(PickupSlot slot, LocalDateTime now) {
        boolean beforeCutoff = !slot.getStartDateTime().isBefore(now.plusMinutes(CUTOFF_MINUTES));
        return beforeCutoff && slot.getRemaining() > 0;
    }

    /** The slots of one day a student can still choose. */
    public List<PickupSlot> findAvailable(LocalDate date, LocalDateTime now) {
        List<PickupSlot> result = new ArrayList<>();
        for (PickupSlot slot : slotRepository.findByDate(date)) {
            if (isBookable(slot, now)) {
                result.add(slot);
            }
        }
        return result;
    }

    /** Bookable slots for the checkout page: today and tomorrow. */
    public List<PickupSlot> findAvailableForCheckout(LocalDateTime now) {
        List<PickupSlot> result = findAvailable(now.toLocalDate(), now);
        result.addAll(findAvailable(now.toLocalDate().plusDays(1), now));
        return result;
    }

    /**
     * Rule 2 cut-off: the slot must start at least 15 minutes after "now".
     *
     * @throws BusinessException if it is too late for this slot
     */
    public void checkCutoff(PickupSlot slot, LocalDateTime now) {
        if (slot.getStartDateTime().isBefore(now.plusMinutes(CUTOFF_MINUTES))) {
            throw new BusinessException("Orders must be placed at least " + CUTOFF_MINUTES
                    + " minutes before the pickup time. Please choose a later time.");
        }
    }

    /**
     * Locks the slot row, then checks there is still a place. Must be called inside the
     * caller's @Transactional method (placeOrder): the lock is held until that transaction
     * ends, so a second student waiting for the same slot sees the updated count.
     *
     * @throws SlotFullException if every place is taken
     */
    public void checkCapacityForUpdate(Long slotId) {
        slotRepository.lockSlot(slotId);
        PickupSlot slot = findById(slotId);
        int used = slotRepository.countActiveOrders(slotId);
        if (used >= slot.getCapacity()) {
            throw new SlotFullException(slot.getLabel());
        }
    }
}
