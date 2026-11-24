package com.campusbite.checkout;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campusbite.auth.SessionUser;
import com.campusbite.common.BusinessException;
import com.campusbite.kitchen.OrderStatusService;
import com.campusbite.menu.MenuItem;
import com.campusbite.menu.MenuService;
import com.campusbite.menuadmin.StockService;
import com.campusbite.slots.PickupSlot;
import com.campusbite.slots.SlotService;

/**
 * M5 Cart & Checkout (owner: Hananeh).
 * Turns the cart into an order. Uses the contracts of my teammates:
 * Pasha's MenuService (real prices) and StockService (stock), my SlotService,
 * and Fatemeh's OrderStatusService (first history row).
 */
@Service
public class CheckoutService {

    private final MenuService menuService;
    private final StockService stockService;
    private final SlotService slotService;
    private final OrderRepository orderRepository;
    private final OrderStatusService orderStatusService;
    private final PickupCodeGenerator codeGenerator;
    private final Clock clock;

    /** Constructor injection of everything placeOrder needs. */
    public CheckoutService(MenuService menuService, StockService stockService, SlotService slotService,
                           OrderRepository orderRepository, OrderStatusService orderStatusService,
                           PickupCodeGenerator codeGenerator, Clock clock) {
        this.menuService = menuService;
        this.stockService = stockService;
        this.slotService = slotService;
        this.orderRepository = orderRepository;
        this.orderStatusService = orderStatusService;
        this.codeGenerator = codeGenerator;
        this.clock = clock;
    }

    /** The pickup slots the student can still choose (today and tomorrow). */
    public List<PickupSlot> availableSlots() {
        return slotService.findAvailableForCheckout(LocalDateTime.now(clock));
    }

    /**
     * Places the order in ONE transaction. Any exception rolls back every step.
     * 1. the cart must not be empty
     * 2. the slot must be at least 15 minutes away and must have a free place (locked)
     * 3. for each line: read the item again (price from the menu, not the cart)
     *    and take the stock (throws OutOfStockException if not enough)
     * 4. save the order with its items, total and a random pickup code
     * 5. write the first status-history row (PLACED)
     *
     * @return the id of the new order
     */
    @Transactional
    public Long placeOrder(SessionUser student, Cart cart, CheckoutForm form) {
        if (cart.isEmpty()) {
            throw new BusinessException("Your cart is empty.");
        }
        LocalDateTime now = LocalDateTime.now(clock);
        PickupSlot slot = slotService.findById(form.getSlotId());
        slotService.checkCutoff(slot, now);
        slotService.checkCapacityForUpdate(slot.getId());

        Order order = new Order();
        int total = 0;
        for (CartLine line : cart.getLines()) {
            MenuItem item = menuService.findById(line.getMenuItemId());
            if (!item.isAvailable()) {
                throw new BusinessException("\"" + item.getName() + "\" is no longer on the menu.");
            }
            stockService.decrement(item.getId(), line.getQuantity());
            order.getItems().add(new OrderItem(item.getId(), item.getName(), line.getQuantity(), item.getPriceIqd()));
            total += item.getPriceIqd() * line.getQuantity();
        }

        order.setUserId(student.getId());
        order.setUserName(student.getFullName());
        order.setSlotId(slot.getId());
        order.setSlotStart(slot.getStartDateTime());
        order.setSlotEnd(slot.getEndDateTime());
        order.setStatus(OrderStatus.PLACED);
        order.setTotalIqd(total);
        order.setPickupCode(codeGenerator.next());
        order.setNote(cleanNote(form.getNote()));
        order.setCreatedAt(now);
        orderRepository.save(order);

        orderStatusService.recordNewOrder(order.getId(), student.getId());
        return order.getId();
    }

    /** Helper: an empty note is stored as null. */
    private String cleanNote(String note) {
        if (note == null || note.isBlank()) {
            return null;
        }
        return note.trim();
    }
}
