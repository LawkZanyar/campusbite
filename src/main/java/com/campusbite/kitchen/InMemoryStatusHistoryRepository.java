package com.campusbite.kitchen;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

/**
 * M7 Kitchen Order Board (owner: Fatemeh).
 * Phase 1 storage for the status history: a plain list in memory ("memory" profile only).
 */
@Repository
@Profile("memory")
public class InMemoryStatusHistoryRepository implements StatusHistoryRepository {

    private final List<StatusChange> changes = new ArrayList<>();
    private long nextId = 1;

    @Override
    public synchronized void add(StatusChange change) {
        change.setId(nextId);
        nextId++;
        changes.add(change);
    }

    @Override
    public synchronized List<StatusChange> findByOrder(Long orderId) {
        List<StatusChange> result = new ArrayList<>();
        for (StatusChange change : changes) {
            if (change.getOrderId().equals(orderId)) {
                result.add(change);       // the list is already in the order they were added
            }
        }
        return result;
    }
}
