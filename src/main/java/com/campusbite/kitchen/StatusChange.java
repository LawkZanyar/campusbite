package com.campusbite.kitchen;

import java.time.LocalDateTime;

import com.campusbite.checkout.OrderStatus;

/**
 * M7 Kitchen Order Board (owner: Fatemeh).
 * One row of order_status_history: "order 12 went from PLACED to PREPARING,
 * changed by user 2 at 12:03". fromStatus is null for the very first row
 * (when the order is placed).
 */
public class StatusChange {

    private Long id;
    private Long orderId;
    private OrderStatus fromStatus;
    private OrderStatus toStatus;
    private Long changedBy;
    private String changedByName;
    private LocalDateTime changedAt;

    /** Empty constructor for the repositories. */
    public StatusChange() {
    }

    /** Creates a new history row (the id is set when it is saved). */
    public StatusChange(Long orderId, OrderStatus fromStatus, OrderStatus toStatus,
                        Long changedBy, LocalDateTime changedAt) {
        this.orderId = orderId;
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
        this.changedBy = changedBy;
        this.changedAt = changedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public OrderStatus getFromStatus() { return fromStatus; }
    public void setFromStatus(OrderStatus fromStatus) { this.fromStatus = fromStatus; }

    public OrderStatus getToStatus() { return toStatus; }
    public void setToStatus(OrderStatus toStatus) { this.toStatus = toStatus; }

    public Long getChangedBy() { return changedBy; }
    public void setChangedBy(Long changedBy) { this.changedBy = changedBy; }

    public String getChangedByName() { return changedByName; }
    public void setChangedByName(String changedByName) { this.changedByName = changedByName; }

    public LocalDateTime getChangedAt() { return changedAt; }
    public void setChangedAt(LocalDateTime changedAt) { this.changedAt = changedAt; }
}
