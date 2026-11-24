package com.campusbite.slots;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.campusbite.common.NotFoundException;

/**
 * M6 Pickup Slots (owner: Hananeh) - CONTRACT used by checkout.
 * Business rules for pickup slots. More methods (generate, edit, delete,
 * capacity check) are added in the next weeks.
 */
@Service
public class SlotService {

    private final SlotRepository slotRepository;

    /** Spring passes the SlotRepository of the active profile. */
    public SlotService(SlotRepository slotRepository) {
        this.slotRepository = slotRepository;
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
}
