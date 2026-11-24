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

/**
 * M7 Kitchen Order Board (owner: Fatemeh).
 * Builds the kitchen board: the orders of one day grouped by pickup slot.
 */
@Service
public class KitchenBoardService {

    private final OrderRepository orderRepository;

    /** Spring passes Hananeh's OrderRepository (contract). */
    public KitchenBoardService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
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
