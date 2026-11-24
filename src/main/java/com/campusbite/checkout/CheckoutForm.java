package com.campusbite.checkout;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * M5 Cart & Checkout (owner: Hananeh).
 * What the student chooses on the checkout page: a pickup slot and an optional note.
 */
public class CheckoutForm {

    @NotNull(message = "Please choose a pickup time")
    private Long slotId;

    @Size(max = 255, message = "The note can be at most 255 characters")
    private String note;

    public Long getSlotId() { return slotId; }
    public void setSlotId(Long slotId) { this.slotId = slotId; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
