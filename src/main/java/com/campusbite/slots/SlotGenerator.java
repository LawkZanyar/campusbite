package com.campusbite.slots;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

/**
 * M6 Pickup Slots (owner: Hananeh).
 * Cuts a time range into slots of a fixed length, e.g. 11:00-14:00 in 15-minute
 * slots = 12 slots. It only builds the objects; SlotService saves them.
 */
@Component
public class SlotGenerator {

    /**
     * Builds the slots [from, from+minutes), [from+minutes, from+2*minutes) ... as long as a
     * whole slot still fits before "to".
     */
    public List<PickupSlot> generate(LocalDate date, LocalTime from, LocalTime to, int minutes, int capacity) {
        List<PickupSlot> slots = new ArrayList<>();
        LocalTime start = from;
        while (!start.plusMinutes(minutes).isAfter(to)) {
            slots.add(new PickupSlot(date, start, start.plusMinutes(minutes), capacity));
            start = start.plusMinutes(minutes);
        }
        return slots;
    }
}
