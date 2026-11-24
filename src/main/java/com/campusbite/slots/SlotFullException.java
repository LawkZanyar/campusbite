package com.campusbite.slots;

import com.campusbite.common.BusinessException;

/**
 * M6 Pickup Slots (owner: Hananeh).
 * Thrown when a slot has no places left. A RuntimeException, so it rolls back
 * the order that was being placed.
 */
public class SlotFullException extends BusinessException {

    /** @param slotLabel e.g. "12:00 - 12:15" */
    public SlotFullException(String slotLabel) {
        super("The pickup time " + slotLabel + " just filled up. Please pick another time.");
    }
}
