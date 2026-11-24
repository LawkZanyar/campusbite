package com.campusbite.kitchen;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campusbite.checkout.Order;
import com.campusbite.checkout.OrderRepository;
import com.campusbite.checkout.OrderStatus;
import com.campusbite.common.NotFoundException;

/**
 * M7 Kitchen Order Board (owner: Fatemeh) - CONTRACT used by checkout (Hananeh)
 * and order history (Fatemeh's M8).
 * The only way to change an order's status: it checks the rule, saves the new
 * status and writes a history row.
 */
@Service
public class OrderStatusService {

    private final OrderRepository orderRepository;
    private final StatusHistoryRepository historyRepository;
    private final Clock clock;

    /** Spring passes Hananeh's OrderRepository, my history repository and the shared Clock. */
    public OrderStatusService(OrderRepository orderRepository,
                              StatusHistoryRepository historyRepository,
                              Clock clock) {
        this.orderRepository = orderRepository;
        this.historyRepository = historyRepository;
        this.clock = clock;
    }

    /**
     * Moves an order to a new status.
     * 1. load the order (locked in MySQL so two clicks can't both change it)
     * 2. check the move is allowed by StatusFlow
     * 3. save the new status and a history row - both or neither (@Transactional)
     *
     * @throws NotFoundException                if the order does not exist
     * @throws InvalidStatusTransitionException if the move is not allowed
     */
    @Transactional
    public void changeStatus(Long orderId, OrderStatus next, Long actorId) {
        Order order = orderRepository.findByIdForUpdate(orderId)
                .orElseThrow(() -> new NotFoundException("Order " + orderId + " was not found."));
        OrderStatus current = order.getStatus();
        if (!StatusFlow.canMove(current, next)) {
            throw new InvalidStatusTransitionException(
                    "Can't move order #" + orderId + " from " + current + " to " + next + ".");
        }
        orderRepository.updateStatus(orderId, next);
        historyRepository.add(new StatusChange(orderId, current, next, actorId, LocalDateTime.now(clock)));
    }

    /** Writes the first history row of a new order (nothing -> PLACED). Called by checkout. */
    public void recordNewOrder(Long orderId, Long studentId) {
        historyRepository.add(new StatusChange(orderId, null, OrderStatus.PLACED, studentId,
                LocalDateTime.now(clock)));
    }

    /** The status history of one order, oldest first. */
    public List<StatusChange> getHistory(Long orderId) {
        return historyRepository.findByOrder(orderId);
    }
}
