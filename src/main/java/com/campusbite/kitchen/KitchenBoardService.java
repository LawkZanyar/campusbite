package com.campusbite.kitchen;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.campusbite.checkout.Order;
import com.campusbite.checkout.OrderRepository;
import com.campusbite.checkout.OrderStatus;
import com.campusbite.common.NotFoundException;

/**
 * M7 Kitchen Order Board (owner: Fatemeh).
 * Builds the kitchen board and lets the kitchen move orders to the next status.
 */
@Service
public class KitchenBoardService {

    private final OrderRepository orderRepository;
    private final OrderStatusService orderStatusService;

    /** Spring passes Hananeh's OrderRepository (contract) and my OrderStatusService. */
    public KitchenBoardService(OrderRepository orderRepository, OrderStatusService orderStatusService) {
        this.orderRepository = orderRepository;
        this.orderStatusService = orderStatusService;
    }

    /**
     * The board for one date. Key = slot label ("12:00 - 12:15"), value = the cards
     * in that slot. Cancelled orders are left out. The repository returns orders
     * sorted by slot time, and a LinkedHashMap keeps that order.
     */
    public Map<String, List<BoardCard>> getBoard(LocalDate date) {
        Map<String, List<BoardCard>> board = new LinkedHashMap<>();
        for (Order order : orderRepository.findBySlotDate(date)) {
            if (order.getStatus() == OrderStatus.CANCELLED) {
                continue;
            }
            String slotLabel = order.getSlotLabel();
            if (!board.containsKey(slotLabel)) {
                board.put(slotLabel, new ArrayList<>());
            }
            board.get(slotLabel).add(new BoardCard(order, kitchenButtons(order.getStatus())));
        }
        return board;
    }

    /**
     * A kitchen button was pressed. The kitchen may never cancel: that is the student's
     * job (and cancelling must give the stock back). Everything else is checked by
     * OrderStatusService.
     *
     * @throws InvalidStatusTransitionException if the move is not allowed
     */
    public void updateStatus(Long orderId, OrderStatus next, Long staffUserId) {
        if (next == OrderStatus.CANCELLED) {
            throw new InvalidStatusTransitionException("Only the student can cancel an order.");
        }
        orderStatusService.changeStatus(orderId, next, staffUserId);
    }

    /**
     * One order for the staff detail page.
     *
     * @throws NotFoundException if the order does not exist
     */
    public Order getOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order " + orderId + " was not found."));
    }

    /** The next statuses the KITCHEN may choose. Cancelling is only for the student. */
    private List<OrderStatus> kitchenButtons(OrderStatus current) {
        List<OrderStatus> buttons = new ArrayList<>();
        for (OrderStatus next : StatusFlow.nextStatuses(current)) {
            if (next != OrderStatus.CANCELLED) {
                buttons.add(next);
            }
        }
        return buttons;
    }
}
