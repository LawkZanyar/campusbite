package com.campusbite.menu;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.campusbite.common.NotFoundException;

/**
 * M3 Menu Catalogue (owner: Pasha) - CONTRACT: findById is used by checkout (Hananeh).
 * Reads the menu for the student pages.
 */
@Service
public class MenuService {

    private final MenuRepository menuRepository;

    /** Spring passes the MenuRepository of the active profile. */
    public MenuService(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    /**
     * The menu grouped by category, in the categories' sort order.
     * Categories with no matching items are left out.
     * A LinkedHashMap keeps the order in which we put the keys in.
     */
    public Map<Category, List<MenuItem>> getMenuGrouped(Long categoryId, boolean vegOnly, String search) {
        List<MenuItem> items = menuRepository.findItems(categoryId, vegOnly, search);
        Map<Category, List<MenuItem>> grouped = new LinkedHashMap<>();
        for (Category category : menuRepository.findAllCategories()) {
            List<MenuItem> inCategory = new ArrayList<>();
            for (MenuItem item : items) {
                if (item.getCategoryId().equals(category.getId())) {
                    inCategory.add(item);
                }
            }
            if (!inCategory.isEmpty()) {
                grouped.put(category, inCategory);
            }
        }
        return grouped;
    }

    /** All categories (for the category tabs). */
    public List<Category> findAllCategories() {
        return menuRepository.findAllCategories();
    }

    /**
     * One menu item.
     *
     * @throws NotFoundException if the id does not exist (shown as our 404 page)
     */
    public MenuItem findById(Long id) {
        return menuRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Menu item " + id + " was not found."));
    }
}
