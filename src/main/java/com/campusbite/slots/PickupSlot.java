package com.campusbite.slots;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * M6 Pickup Slots (owner: Hananeh) - CONTRACT used by checkout.
 * A 15-minute pickup window, e.g. 12:15 - 12:30 on 2026-11-16, with a capacity.
 * "used" is not a column: the repository counts the slot's active (not cancelled)
 * orders and puts the number here when it reads the slot.
 */
public class PickupSlot {

    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm");

    private Long id;
    private LocalDate date;
    private LocalTime start;
    private LocalTime end;
    private int capacity;
    private int used;

    /** Empty constructor for the repositories. */
    public PickupSlot() {
    }

    /** Creates a new slot that has no id yet. */
    public PickupSlot(LocalDate date, LocalTime start, LocalTime end, int capacity) {
        this.date = date;
        this.start = start;
        this.end = end;
        this.capacity = capacity;
    }

    /** Places left = capacity - used (never below 0). */
    public int getRemaining() {
        return Math.max(0, capacity - used);
    }

    /** True when no places are left. */
    public boolean isFull() {
        return getRemaining() == 0;
    }

    /** "12:15 - 12:30" for the pages. */
    public String getLabel() {
        return start.format(TIME) + " - " + end.format(TIME);
    }

    /** Date and start time together, used for the 15-minute cut-off rule. */
    public LocalDateTime getStartDateTime() {
        return LocalDateTime.of(date, start);
    }

    /** Date and end time together. */
    public LocalDateTime getEndDateTime() {
        return LocalDateTime.of(date, end);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalTime getStart() { return start; }
    public void setStart(LocalTime start) { this.start = start; }

    public LocalTime getEnd() { return end; }
    public void setEnd(LocalTime end) { this.end = end; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public int getUsed() { return used; }
    public void setUsed(int used) { this.used = used; }
}
