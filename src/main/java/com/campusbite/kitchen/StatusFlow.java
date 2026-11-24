package com.campusbite.kitchen;

import java.util.ArrayList;
import java.util.List;

import com.campusbite.checkout.OrderStatus;

/**
 * M7 Kitchen Order Board (owner: Fatemeh) - CONTRACT.
 * The ONE place that knows which status changes are allowed (business rule 1):
 *
 *   PLACED    -> PREPARING or CANCELLED
 *   PREPARING -> READY
 *   READY     -> COLLECTED or NO_SHOW
 *   anything else -> not allowed
 *
 * All methods are static because the rules never change while the app runs.
 */
public final class StatusFlow {

    private StatusFlow() {
        // only static methods
    }

    /** True if an order may move from "from" to "to". */
    public static boolean canMove(OrderStatus from, OrderStatus to) {
        if (from == null || to == null) {
            return false;
        }
        switch (from) {
            case PLACED:
                return to == OrderStatus.PREPARING || to == OrderStatus.CANCELLED;
            case PREPARING:
                return to == OrderStatus.READY;
            case READY:
                return to == OrderStatus.COLLECTED || to == OrderStatus.NO_SHOW;
            default:
                return false;   // COLLECTED, CANCELLED and NO_SHOW are final
        }
    }

    /** Every status the order may move to next (used to draw the buttons on the board). */
    public static List<OrderStatus> nextStatuses(OrderStatus from) {
        List<OrderStatus> result = new ArrayList<>();
        for (OrderStatus candidate : OrderStatus.values()) {
            if (canMove(from, candidate)) {
                result.add(candidate);
            }
        }
        return result;
    }

    /** True for COLLECTED, CANCELLED and NO_SHOW: nothing can happen to the order any more. */
    public static boolean isFinal(OrderStatus status) {
        return nextStatuses(status).isEmpty();
    }
}
