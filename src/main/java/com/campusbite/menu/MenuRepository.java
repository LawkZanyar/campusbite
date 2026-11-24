package com.campusbite.menu;

import java.util.List;
import java.util.Optional;

/**
 * M3/M4 (owner: Pasha) - CONTRACT.
 * Everything the app needs to read and change categories and menu items.
 * Implemented by InMemoryMenuRepository (Phase 1) and JdbcMenuRepository (MySQL, Week 08).
 */
public interface MenuRepository {

    // ---------- categories ----------

    /** All categories, sorted by sortOrder then name. */
    List<Category> findAllCategories();

    /** One category by id, empty if it does not exist. */
    Optional<Category> findCategoryById(Long id);

    /** Inserts a category, sets its id and returns it. */
    Category saveCategory(Category category);

    /** Changes the name and sort order of a category. */
    void updateCategory(Category category);

    /** Deletes a category (the service first checks that no item uses it). */
    void deleteCategory(Long id);

    /** How many menu items belong to this category. */
    int countItemsInCategory(Long categoryId);

    // ---------- menu items ----------

    /**
     * Items for the public menu: only available ones, optionally filtered.
     *
     * @param categoryId only this category, or null for all
     * @param vegOnly    true = only vegetarian items
     * @param search     text that must appear in the name or description, or null
     */
    List<MenuItem> findItems(Long categoryId, boolean vegOnly, String search);

    /** Every item, also unavailable ones (for the admin list). */
    List<MenuItem> findAllItems();

    /** One item by id, empty if it does not exist. */
    Optional<MenuItem> findById(Long id);

    /** Inserts an item, sets its id and returns it. */
    MenuItem saveItem(MenuItem item);

    /** Saves all editable fields of an existing item. */
    void updateItem(MenuItem item);

    /** Deletes an item (the service first checks that it was never ordered). */
    void deleteItem(Long id);

    /** How many order lines (order_items rows) use this item. */
    int countOrderLines(Long itemId);

    // ---------- today's stock ----------

    /**
     * Takes qty units from stock_left, but only if enough is left (or stock is unlimited).
     *
     * @return the number of rows changed: 1 = done, 0 = not enough stock
     */
    int decrementStock(Long itemId, int qty);

    /** Gives qty units back (used when an order is cancelled). Unlimited items are not changed. */
    void restoreStock(Long itemId, int qty);

    /** Sets today's stock_left for one item (null = unlimited). */
    void updateStockLeft(Long itemId, Integer stockLeft);

    /** Start of a new day: stock_left = daily_stock for every item. */
    void resetAllStock();
}
