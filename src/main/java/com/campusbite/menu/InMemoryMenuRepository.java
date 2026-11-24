package com.campusbite.menu;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

/**
 * M3/M4 (owner: Pasha).
 * Phase 1 storage: categories and items live in Maps in memory ("memory" profile only).
 * Starts with 4 categories and 16 campus dishes priced in IQD. The same data goes
 * into db/seed/02_menu.sql in Week 08.
 */
@Repository
@Profile("memory")
public class InMemoryMenuRepository implements MenuRepository {

    private final Map<Long, Category> categories = new LinkedHashMap<>();
    private final Map<Long, MenuItem> items = new LinkedHashMap<>();
    private long nextCategoryId = 1;
    private long nextItemId = 1;

    /** Fills the store with the demo menu. */
    public InMemoryMenuRepository() {
        Category hot = saveCategory(new Category(null, "Hot meals", 1));
        Category sandwiches = saveCategory(new Category(null, "Sandwiches", 2));
        Category drinks = saveCategory(new Category(null, "Drinks", 3));
        Category desserts = saveCategory(new Category(null, "Desserts", 4));

        addDemo(hot, "Chicken biryani", "Spiced rice with chicken, almonds and raisins", 5000, false, 30);
        addDemo(hot, "Quzi lamb and rice", "Slow-cooked lamb over spiced rice", 7000, false, 15);
        addDemo(hot, "Kubba (2 pieces)", "Fried bulgur shells filled with minced meat", 3000, false, null);
        addDemo(hot, "Lentil soup", "Red lentil soup with lemon", 1500, true, null);
        addDemo(hot, "Vegetable dolma", "Vine leaves and vegetables stuffed with rice", 4000, true, 20);
        addDemo(sandwiches, "Falafel wrap", "Falafel, salad and tahini in flat bread", 2500, true, null);
        addDemo(sandwiches, "Chicken shawarma", "Chicken shawarma with garlic sauce and pickles", 3500, false, 40);
        addDemo(sandwiches, "Kebab sandwich", "Grilled minced lamb kebab in samoon bread", 4000, false, 0);
        addDemo(sandwiches, "Cheese manakish", "Flat bread baked with white cheese", 2000, true, null);
        addDemo(drinks, "Ayran", "Cold salted yoghurt drink", 750, true, null);
        addDemo(drinks, "Black tea", "Iraqi tea in an istikan glass", 500, true, null);
        addDemo(drinks, "Fresh orange juice", "Freshly squeezed orange juice", 2000, true, 25);
        addDemo(drinks, "Bottled water", "500 ml bottle", 500, true, null);
        addDemo(desserts, "Baklava (3 pieces)", "Filo pastry with pistachio and syrup", 2000, true, 20);
        addDemo(desserts, "Kleicha date cookies", "Traditional cookies filled with dates", 1500, true, null);
        addDemo(desserts, "Zalabia", "Crispy fried dough in syrup", 1500, true, 10);
    }

    /** Helper for the constructor: creates one demo item. */
    private void addDemo(Category category, String name, String description, int price,
                         boolean vegetarian, Integer dailyStock) {
        MenuItem item = new MenuItem();
        item.setCategoryId(category.getId());
        item.setName(name);
        item.setDescription(description);
        item.setPriceIqd(price);
        item.setVegetarian(vegetarian);
        item.setAvailable(true);
        item.setDailyStock(dailyStock);
        item.setStockLeft(dailyStock);
        saveItem(item);
    }

    // ---------- categories ----------

    @Override
    public synchronized List<Category> findAllCategories() {
        List<Category> result = new ArrayList<>(categories.values());
        result.sort(Comparator.comparingInt(Category::getSortOrder).thenComparing(Category::getName));
        return result;
    }

    @Override
    public synchronized Optional<Category> findCategoryById(Long id) {
        return Optional.ofNullable(categories.get(id));
    }

    @Override
    public synchronized Category saveCategory(Category category) {
        category.setId(nextCategoryId);
        nextCategoryId++;
        categories.put(category.getId(), category);
        return category;
    }

    @Override
    public synchronized void updateCategory(Category category) {
        categories.put(category.getId(), category);
    }

    @Override
    public synchronized void deleteCategory(Long id) {
        categories.remove(id);
    }

    @Override
    public synchronized int countItemsInCategory(Long categoryId) {
        int count = 0;
        for (MenuItem item : items.values()) {
            if (item.getCategoryId().equals(categoryId)) {
                count++;
            }
        }
        return count;
    }

    // ---------- menu items ----------

    @Override
    public synchronized List<MenuItem> findItems(Long categoryId, boolean vegOnly, String search) {
        String text = (search == null) ? "" : search.trim().toLowerCase();
        List<MenuItem> result = new ArrayList<>();
        for (MenuItem item : findAllItems()) {
            if (!item.isAvailable()) {
                continue;
            }
            if (categoryId != null && !categoryId.equals(item.getCategoryId())) {
                continue;
            }
            if (vegOnly && !item.isVegetarian()) {
                continue;
            }
            String haystack = (item.getName() + " " + item.getDescription()).toLowerCase();
            if (!text.isEmpty() && !haystack.contains(text)) {
                continue;
            }
            result.add(item);
        }
        return result;
    }

    @Override
    public synchronized List<MenuItem> findAllItems() {
        List<MenuItem> result = new ArrayList<>();
        // walk the categories in display order so items come out grouped and sorted
        for (Category category : findAllCategories()) {
            List<MenuItem> inCategory = new ArrayList<>();
            for (MenuItem item : items.values()) {
                if (item.getCategoryId().equals(category.getId())) {
                    item.setCategoryName(category.getName());
                    inCategory.add(item);
                }
            }
            inCategory.sort(Comparator.comparing(MenuItem::getName));
            result.addAll(inCategory);
        }
        return result;
    }

    @Override
    public synchronized Optional<MenuItem> findById(Long id) {
        MenuItem item = items.get(id);
        if (item != null) {
            Category category = categories.get(item.getCategoryId());
            item.setCategoryName(category == null ? "" : category.getName());
        }
        return Optional.ofNullable(item);
    }

    @Override
    public synchronized MenuItem saveItem(MenuItem item) {
        item.setId(nextItemId);
        nextItemId++;
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public synchronized void updateItem(MenuItem item) {
        items.put(item.getId(), item);
    }

    @Override
    public synchronized void deleteItem(Long id) {
        items.remove(id);
    }

    @Override
    public synchronized int countOrderLines(Long itemId) {
        // Phase 1 has no order table to look at, so we report "never ordered".
        return 0;
    }

    // ---------- today's stock ----------

    @Override
    public synchronized int decrementStock(Long itemId, int qty) {
        MenuItem item = items.get(itemId);
        if (item == null) {
            return 0;
        }
        if (item.getStockLeft() == null) {
            return 1;                       // unlimited stock: always OK
        }
        if (item.getStockLeft() < qty) {
            return 0;                       // not enough left: nothing changes
        }
        item.setStockLeft(item.getStockLeft() - qty);
        return 1;
    }

    @Override
    public synchronized void restoreStock(Long itemId, int qty) {
        MenuItem item = items.get(itemId);
        if (item != null && item.getStockLeft() != null) {
            item.setStockLeft(item.getStockLeft() + qty);
        }
    }

    @Override
    public synchronized void updateStockLeft(Long itemId, Integer stockLeft) {
        MenuItem item = items.get(itemId);
        if (item != null) {
            item.setStockLeft(stockLeft);
        }
    }

    @Override
    public synchronized void resetAllStock() {
        for (MenuItem item : items.values()) {
            item.setStockLeft(item.getDailyStock());
        }
    }
}
