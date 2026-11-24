package com.campusbite.kitchen;

import java.util.List;

import com.campusbite.checkout.Order;
import com.campusbite.checkout.OrderStatus;

/**
 * M7 Kitchen Order Board (owner: Fatemeh).
 * What one card on the board needs: the order and the buttons to draw
 * (the statuses the kitchen may move it to). Built by KitchenBoardService,
 * so the template contains no business logic.
 */
public class BoardCard {

    private final Order order;
    private final List<OrderStatus> nextStatuses;

    /** Creates a card for one order. */
    public BoardCard(Order order, List<OrderStatus> nextStatuses) {
        this.order = order;
        this.nextStatuses = nextStatuses;
    }

    public Order getOrder() { return order; }
    public List<OrderStatus> getNextStatuses() { return nextStatuses; }
}
