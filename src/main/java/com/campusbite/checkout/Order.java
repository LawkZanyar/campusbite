package com.campusbite.checkout;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * M5 Cart & Checkout (owner: Hananeh) - CONTRACT used by the kitchen board and
 * order history (Fatemeh) and reviews (Pasha).
 * One row of the orders table plus its order items. userName, slotStart and
 * slotEnd are copied from the users and pickup_slots tables when we read it.
 */
public class Order {

    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm");

    private Long id;
    private Long userId;
    private String userName;
    private Long slotId;
    private LocalDateTime slotStart;
    private LocalDateTime slotEnd;
    private OrderStatus status;
    private int totalIqd;
    private String pickupCode;
    private String note;
    private LocalDateTime createdAt;
    private List<OrderItem> items = new ArrayList<>();

    /** Pickup time as text, e.g. "12:00 - 12:15". */
    public String getSlotLabel() {
        if (slotStart == null || slotEnd == null) {
            return "";
        }
        return slotStart.format(TIME) + " - " + slotEnd.format(TIME);
    }

    /** Total as text, e.g. "7,500 IQD". */
    public String getTotalLabel() {
        return String.format(Locale.US, "%,d IQD", totalIqd);
    }

    /** Number of pieces in the order (sum of all quantities). */
    public int getItemCount() {
        int count = 0;
        for (OrderItem item : items) {
            count += item.getQuantity();
        }
        return count;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public Long getSlotId() { return slotId; }
    public void setSlotId(Long slotId) { this.slotId = slotId; }

    public LocalDateTime getSlotStart() { return slotStart; }
    public void setSlotStart(LocalDateTime slotStart) { this.slotStart = slotStart; }

    public LocalDateTime getSlotEnd() { return slotEnd; }
    public void setSlotEnd(LocalDateTime slotEnd) { this.slotEnd = slotEnd; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public int getTotalIqd() { return totalIqd; }
    public void setTotalIqd(int totalIqd) { this.totalIqd = totalIqd; }

    public String getPickupCode() { return pickupCode; }
    public void setPickupCode(String pickupCode) { this.pickupCode = pickupCode; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }
}
