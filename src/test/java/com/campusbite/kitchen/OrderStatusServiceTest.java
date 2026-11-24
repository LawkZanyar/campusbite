package com.campusbite.kitchen;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.campusbite.checkout.InMemoryOrderRepository;
import com.campusbite.checkout.OrderStatus;
import com.campusbite.common.NotFoundException;

/**
 * M7 Kitchen Order Board (owner: Fatemeh).
 * Unit tests for OrderStatusService and KitchenBoardService with in-memory stores.
 * Sample orders: #1 PLACED, #2 PREPARING, #3 READY, #4 COLLECTED.
 */
class OrderStatusServiceTest {

    private InMemoryOrderRepository orders;
    private OrderStatusService service;
    private KitchenBoardService board;

    @BeforeEach
    void setUp() {
        orders = new InMemoryOrderRepository();
        service = new OrderStatusService(orders, new InMemoryStatusHistoryRepository(), Clock.systemDefaultZone());
        board = new KitchenBoardService(orders, service);
    }

    @Test
    void validChangeUpdatesStatusAndWritesHistory() {
        service.changeStatus(1L, OrderStatus.PREPARING, 2L);

        assertThat(orders.findById(1L).get().getStatus()).isEqualTo(OrderStatus.PREPARING);
        List<StatusChange> rows = service.getHistory(1L);
        assertThat(rows).hasSize(1);
        assertThat(rows.get(0).getFromStatus()).isEqualTo(OrderStatus.PLACED);
        assertThat(rows.get(0).getToStatus()).isEqualTo(OrderStatus.PREPARING);
        assertThat(rows.get(0).getChangedBy()).isEqualTo(2L);
    }

    @Test
    void invalidChangeThrowsAndChangesNothing() {
        assertThatThrownBy(() -> service.changeStatus(3L, OrderStatus.PREPARING, 2L))
                .isInstanceOf(InvalidStatusTransitionException.class)
                .hasMessageContaining("from READY to PREPARING");

        assertThat(orders.findById(3L).get().getStatus()).isEqualTo(OrderStatus.READY);
        assertThat(service.getHistory(3L)).isEmpty();
    }

    @Test
    void unknownOrderThrowsNotFound() {
        assertThatThrownBy(() -> service.changeStatus(99L, OrderStatus.READY, 2L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void recordNewOrderWritesFirstRowWithoutFromStatus() {
        service.recordNewOrder(1L, 1L);

        StatusChange first = service.getHistory(1L).get(0);
        assertThat(first.getFromStatus()).isNull();
        assertThat(first.getToStatus()).isEqualTo(OrderStatus.PLACED);
    }

    @Test
    void boardGroupsTodaysOrdersBySlotWithoutCancelButtons() {
        Map<String, List<BoardCard>> today = board.getBoard(LocalDate.now());

        assertThat(today.keySet()).containsExactly("12:00 - 12:15", "12:15 - 12:30", "12:30 - 12:45");
        BoardCard placed = today.get("12:00 - 12:15").get(0);
        assertThat(placed.getNextStatuses()).containsExactly(OrderStatus.PREPARING);
    }

    @Test
    void kitchenCannotCancelButCanMoveForward() {
        assertThatThrownBy(() -> board.updateStatus(1L, OrderStatus.CANCELLED, 2L))
                .isInstanceOf(InvalidStatusTransitionException.class)
                .hasMessageContaining("Only the student");

        board.updateStatus(2L, OrderStatus.READY, 2L);
        assertThat(orders.findById(2L).get().getStatus()).isEqualTo(OrderStatus.READY);
    }

    @Test
    void cancelledOrdersAreNotOnTheBoard() {
        service.changeStatus(1L, OrderStatus.CANCELLED, 1L);

        assertThat(board.getBoard(LocalDate.now()).get("12:00 - 12:15")).hasSize(1);
    }
}
