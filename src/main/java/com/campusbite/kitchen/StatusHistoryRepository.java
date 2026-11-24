package com.campusbite.kitchen;

import java.util.List;

/**
 * M7 Kitchen Order Board (owner: Fatemeh).
 * Stores the history of status changes. Implemented by InMemoryStatusHistoryRepository
 * (Phase 1) and JdbcStatusHistoryRepository (MySQL, Week 08).
 */
public interface StatusHistoryRepository {

    /** Saves one status change and sets its id. */
    void add(StatusChange change);

    /** All changes of one order, oldest first. */
    List<StatusChange> findByOrder(Long orderId);
}
