package com.campusbite.slots;

import java.time.LocalDate;
import java.time.LocalTime;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * M6 Pickup Slots (owner: Hananeh).
 * The "generate slots" form: a day, a time range and the capacity of each slot.
 * @DateTimeFormat tells Spring the text format the browser's date/time inputs send.
 */
public class SlotForm {

    @NotNull(message = "Please choose a date")
    @FutureOrPresent(message = "The date can't be in the past")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate date = LocalDate.now();

    @NotNull(message = "Please enter a start time")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime from = LocalTime.of(11, 0);

    @NotNull(message = "Please enter an end time")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime to = LocalTime.of(14, 0);

    @NotNull(message = "Please enter a capacity")
    @Min(value = 1, message = "Capacity must be at least 1")
    @Max(value = 100, message = "Capacity can be at most 100")
    private Integer capacity = 20;

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalTime getFrom() { return from; }
    public void setFrom(LocalTime from) { this.from = from; }

    public LocalTime getTo() { return to; }
    public void setTo(LocalTime to) { this.to = to; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
}
