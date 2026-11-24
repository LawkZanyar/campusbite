package com.campusbite.kitchen;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.jupiter.api.Test;

import com.campusbite.checkout.OrderStatus;

/**
 * M7 Kitchen Order Board (owner: Fatemeh).
 * Checks ALL 36 (6 x 6) pairs of statuses: exactly the 5 allowed moves are true.
 */
class StatusFlowTest {

    /** The allowed moves written as "FROM>TO". */
    private static final Set<String> ALLOWED = Set.of(
            "PLACED>PREPARING", "PLACED>CANCELLED",
            "PREPARING>READY",
            "READY>COLLECTED", "READY>NO_SHOW");

    @Test
    void onlyTheFiveAllowedMovesAreTrue() {
        int checked = 0;
        for (OrderStatus from : OrderStatus.values()) {
            for (OrderStatus to : OrderStatus.values()) {
                boolean expected = ALLOWED.contains(from + ">" + to);
                assertThat(StatusFlow.canMove(from, to))
                        .as(from + " -> " + to)
                        .isEqualTo(expected);
                checked++;
            }
        }
        assertThat(checked).isEqualTo(36);
    }

    @Test
    void nextStatusesListsTheButtons() {
        assertThat(StatusFlow.nextStatuses(OrderStatus.PLACED))
                .containsExactly(OrderStatus.PREPARING, OrderStatus.CANCELLED);
        assertThat(StatusFlow.nextStatuses(OrderStatus.READY))
                .containsExactly(OrderStatus.COLLECTED, OrderStatus.NO_SHOW);
    }

    @Test
    void finalStatuses() {
        assertThat(StatusFlow.isFinal(OrderStatus.COLLECTED)).isTrue();
        assertThat(StatusFlow.isFinal(OrderStatus.CANCELLED)).isTrue();
        assertThat(StatusFlow.isFinal(OrderStatus.NO_SHOW)).isTrue();
        assertThat(StatusFlow.isFinal(OrderStatus.PLACED)).isFalse();
    }

    @Test
    void nullIsNeverAllowed() {
        assertThat(StatusFlow.canMove(null, OrderStatus.READY)).isFalse();
        assertThat(StatusFlow.canMove(OrderStatus.PLACED, null)).isFalse();
    }
}
