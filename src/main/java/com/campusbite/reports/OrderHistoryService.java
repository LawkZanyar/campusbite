package com.campusbite.reports;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campusbite.auth.SessionUser;
import com.campusbite.checkout.Order;
import com.campusbite.checkout.OrderItem;
import com.campusbite.checkout.OrderRepository;
import com.campusbite.checkout.OrderStatus;
import com.campusbite.common.NotFoundException;
import com.campusbite.kitchen.OrderStatusService;
import com.campusbite.kitchen.StatusChange;
import com.campusbite.menuadmin.StockService;

/**
 * M8 Order History & Reports (owner: Fatemeh).
 * A student's own orders: list, detail and cancel.
 */
@Service
public class OrderHistoryService {

    private final OrderRepository orderRepository;
    private final OrderStatusService orderStatusService;
    private final StockService stockService;

    /** Hananeh's OrderRepository, my OrderStatusService and Pasha's StockService (contracts). */
    public OrderHistoryService(OrderRepository orderRepository, OrderStatusService orderStatusService,
                               StockService stockService) {
        this.orderRepository = orderRepository;
        this.orderStatusService = orderStatusService;
        this.stockService = stockService;
    }

    /** All orders of this user, newest first. */
    public List<Order> findMyOrders(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    /**
     * One order for the detail page. A student may only see their OWN orders; staff may
     * see any order. For somebody else's order we say "not found" (404), not "forbidden"
     * (403): a 403 would tell the student that this order id exists.
     *
     * @throws NotFoundException if the order does not exist or is not the student's
     */
    public Order getOrderForViewer(SessionUser viewer, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order " + orderId + " was not found."));
        boolean isOwner = order.getUserId().equals(viewer.getId());
        if (!isOwner && !viewer.isStaff()) {
            throw new NotFoundException("Order " + orderId + " was not found.");
        }
        return order;
    }

    /** The status timeline of an order (oldest first). */
    public List<StatusChange> getTimeline(Long orderId) {
        return orderStatusService.getHistory(orderId);
    }

    /**
     * The student cancels their own order. One transaction:
     * 1. owner check (somebody else's order -> NotFoundException)
     * 2. PLACED -> CANCELLED through OrderStatusService (fails if already PREPARING or later)
     * 3. give every item's quantity back to today's stock (Pasha's StockService)
     */
    @Transactional
    public void cancel(SessionUser student, Long orderId) {
        Order order = getOrderForViewer(student, orderId);
        if (!order.getUserId().equals(student.getId())) {
            throw new NotFoundException("Order " + orderId + " was not found.");
        }
        orderStatusService.changeStatus(orderId, OrderStatus.CANCELLED, student.getId());
        for (OrderItem item : order.getItems()) {
            stockService.restore(item.getMenuItemId(), item.getQuantity());
        }
    }
}
