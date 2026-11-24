package com.campusbite.reports;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Clock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.campusbite.auth.SessionUser;
import com.campusbite.checkout.InMemoryOrderRepository;
import com.campusbite.checkout.OrderStatus;
import com.campusbite.common.NotFoundException;
import com.campusbite.kitchen.InMemoryStatusHistoryRepository;
import com.campusbite.kitchen.InvalidStatusTransitionException;
import com.campusbite.kitchen.OrderStatusService;
import com.campusbite.menu.InMemoryMenuRepository;
import com.campusbite.menuadmin.StockService;
import com.campusbite.users.Role;

/**
 * M8 Order History & Reports (owner: Fatemeh).
 * Sample orders all belong to user 1: #1 PLACED (1 x biryani, 1 x ayran),
 * #2 PREPARING, #3 READY, #4 COLLECTED.
 */
class OrderHistoryServiceTest {

    private InMemoryOrderRepository orders;
    private InMemoryMenuRepository menu;
    private OrderHistoryService service;

    private final SessionUser owner = new SessionUser(1L, "Demo Student", "student@campusbite.test", Role.STUDENT);
    private final SessionUser otherStudent = new SessionUser(7L, "Other", "other@uni.edu", Role.STUDENT);
    private final SessionUser kitchen = new SessionUser(2L, "Demo Kitchen", "kitchen@campusbite.test", Role.KITCHEN);

    @BeforeEach
    void setUp() {
        orders = new InMemoryOrderRepository();
        menu = new InMemoryMenuRepository();
        OrderStatusService statusService =
                new OrderStatusService(orders, new InMemoryStatusHistoryRepository(), Clock.systemDefaultZone());
        service = new OrderHistoryService(orders, statusService, new StockService(menu));
    }

    @Test
    void myOrdersAreNewestFirst() {
        assertThat(service.findMyOrders(1L)).extracting("id").containsExactly(4L, 3L, 2L, 1L);
        assertThat(service.findMyOrders(7L)).isEmpty();
    }

    @Test
    void ownerAndStaffCanSeeTheOrderButAnotherStudentGets404() {
        assertThat(service.getOrderForViewer(owner, 1L).getId()).isEqualTo(1L);
        assertThat(service.getOrderForViewer(kitchen, 1L).getId()).isEqualTo(1L);
        assertThatThrownBy(() -> service.getOrderForViewer(otherStudent, 1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void cancelPlacedOrderRestoresStockAndWritesHistory() {
        menu.updateStockLeft(1L, 10);                         // biryani: 10 left

        service.cancel(owner, 1L);

        assertThat(orders.findById(1L).get().getStatus()).isEqualTo(OrderStatus.CANCELLED);
        assertThat(menu.findById(1L).get().getStockLeft()).isEqualTo(11);
        assertThat(service.getTimeline(1L)).hasSize(1);
    }

    @Test
    void cancellingAPreparingOrderFailsAndChangesNothing() {
        menu.updateStockLeft(6L, 5);

        assertThatThrownBy(() -> service.cancel(owner, 2L))
                .isInstanceOf(InvalidStatusTransitionException.class);
        assertThat(orders.findById(2L).get().getStatus()).isEqualTo(OrderStatus.PREPARING);
        assertThat(menu.findById(6L).get().getStockLeft()).isEqualTo(5);
    }

    @Test
    void anotherStudentCannotCancel() {
        assertThatThrownBy(() -> service.cancel(otherStudent, 1L)).isInstanceOf(NotFoundException.class);
        assertThat(orders.findById(1L).get().getStatus()).isEqualTo(OrderStatus.PLACED);
    }

    @Test
    void staffCannotCancelThroughTheStudentPage() {
        assertThatThrownBy(() -> service.cancel(kitchen, 1L)).isInstanceOf(NotFoundException.class);
    }
}
