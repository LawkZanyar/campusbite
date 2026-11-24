package com.campusbite.checkout;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.campusbite.auth.SessionUser;
import com.campusbite.common.BusinessException;
import com.campusbite.kitchen.InMemoryStatusHistoryRepository;
import com.campusbite.kitchen.OrderStatusService;
import com.campusbite.menu.InMemoryMenuRepository;
import com.campusbite.menu.MenuService;
import com.campusbite.menuadmin.OutOfStockException;
import com.campusbite.menuadmin.StockService;
import com.campusbite.slots.InMemorySlotRepository;
import com.campusbite.slots.SlotFullException;
import com.campusbite.slots.SlotGenerator;
import com.campusbite.slots.SlotService;
import com.campusbite.users.Role;

/**
 * M5 Cart & Checkout (owner: Hananeh).
 * placeOrder with the in-memory stores of all modules and a FIXED clock (today 09:00),
 * so the 15-minute rule gives the same result whenever the test runs.
 */
class CheckoutServiceTest {

    private InMemoryMenuRepository menu;
    private InMemoryOrderRepository orders;
    private InMemorySlotRepository slots;
    private CheckoutService checkout;
    private Cart cart;
    private final SessionUser student = new SessionUser(1L, "Demo Student", "student@campusbite.test", Role.STUDENT);

    @BeforeEach
    void setUp() {
        ZoneId zone = ZoneId.systemDefault();
        Clock nineOClock = Clock.fixed(LocalDate.now().atTime(9, 0).atZone(zone).toInstant(), zone);
        menu = new InMemoryMenuRepository();
        orders = new InMemoryOrderRepository();
        slots = new InMemorySlotRepository(orders);
        MenuService menuService = new MenuService(menu);
        checkout = new CheckoutService(menuService, new StockService(menu),
                new SlotService(slots, new SlotGenerator()), orders,
                new OrderStatusService(orders, new InMemoryStatusHistoryRepository(), nineOClock),
                new PickupCodeGenerator(), nineOClock);
        cart = new Cart();
        cart.add(menuService.findById(1L), 2);       // 2 x Chicken biryani (5,000), stock 30
        cart.add(menuService.findById(10L), 1);      // 1 x Ayran (750), unlimited
    }

    private CheckoutForm form(long slotId) {
        CheckoutForm form = new CheckoutForm();
        form.setSlotId(slotId);
        form.setNote("  No onions ");
        return form;
    }

    @Test
    void placeOrderSavesOrderTakesStockAndMakesCode() {
        Long id = checkout.placeOrder(student, cart, form(1L));

        Order order = orders.findById(id).get();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PLACED);
        assertThat(order.getTotalIqd()).isEqualTo(10750);
        assertThat(order.getItems()).hasSize(2);
        assertThat(order.getPickupCode()).hasSize(6);
        assertThat(order.getNote()).isEqualTo("No onions");
        assertThat(order.getSlotLabel()).isEqualTo("11:00 - 11:15");
        assertThat(menu.findById(1L).get().getStockLeft()).isEqualTo(28);
    }

    @Test
    void totalUsesTheMenuPriceNotTheCartPrice() {
        menu.findById(1L).get().setPriceIqd(6000);     // price changed after adding to cart

        Long id = checkout.placeOrder(student, cart, form(1L));

        assertThat(orders.findById(id).get().getTotalIqd()).isEqualTo(2 * 6000 + 750);
    }

    @Test
    void emptyCartIsRejected() {
        assertThatThrownBy(() -> checkout.placeOrder(student, new Cart(), form(1L)))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("empty");
    }

    @Test
    void fullSlotIsRejected() {
        slots.updateCapacity(5L, 2);                     // 12:00 already has 2 sample orders

        assertThatThrownBy(() -> checkout.placeOrder(student, cart, form(5L)))
                .isInstanceOf(SlotFullException.class);
    }

    @Test
    void notEnoughStockIsRejected() {
        menu.updateStockLeft(1L, 1);                     // only 1 biryani left, cart has 2

        assertThatThrownBy(() -> checkout.placeOrder(student, cart, form(1L)))
                .isInstanceOf(OutOfStockException.class);
        assertThat(orders.findByUserId(1L)).hasSize(4);  // no new order
    }

    @Test
    void slotInThePastIsRejected() {
        slots.findById(1L).get().setDate(LocalDate.now().minusDays(1));

        assertThatThrownBy(() -> checkout.placeOrder(student, cart, form(1L)))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("15 minutes");
    }

    @Test
    void checkoutOffersTwentyFourSlots() {
        assertThat(checkout.availableSlots()).hasSize(24);
    }
}
