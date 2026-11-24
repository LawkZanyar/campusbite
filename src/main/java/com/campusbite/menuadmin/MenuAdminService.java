package com.campusbite.menuadmin;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.campusbite.common.BusinessException;
import com.campusbite.common.NotFoundException;
import com.campusbite.menu.Category;
import com.campusbite.menu.MenuItem;
import com.campusbite.menu.MenuRepository;

/**
 * M4 Menu & Stock Management (owner: Pasha).
 * Business rules for adding, editing and deleting menu items.
 */
@Service
public class MenuAdminService {

    private final MenuRepository menuRepository;

    /** Constructor injection of my MenuRepository (M3 contract). */
    public MenuAdminService(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    /** Every item, also unavailable ones, grouped by category. */
    public List<MenuItem> listItems() {
        return menuRepository.findAllItems();
    }

    /** All categories (for the category drop-down). */
    public List<Category> listCategories() {
        return menuRepository.findAllCategories();
    }

    /**
     * An ItemForm filled with the values of an existing item (for the edit page).
     *
     * @throws NotFoundException if the item does not exist
     */
    public ItemForm getItemForm(Long itemId) {
        MenuItem item = findItem(itemId);
        ItemForm form = new ItemForm();
        form.setName(item.getName());
        form.setDescription(item.getDescription());
        form.setPriceIqd(item.getPriceIqd());
        form.setCategoryId(item.getCategoryId());
        form.setVegetarian(item.isVegetarian());
        form.setAvailable(item.isAvailable());
        form.setDailyStock(item.getDailyStock());
        return form;
    }

    /** Creates a new item. Today's stock starts at the daily stock. */
    public MenuItem createItem(ItemForm form) {
        checkCategoryExists(form.getCategoryId());
        MenuItem item = new MenuItem();
        copyFormToItem(form, item);
        item.setStockLeft(form.getDailyStock());
        return menuRepository.saveItem(item);
    }

    /** Saves the edited fields. If the daily stock changed, today's stock is reset to it. */
    public void updateItem(Long itemId, ItemForm form) {
        checkCategoryExists(form.getCategoryId());
        MenuItem item = findItem(itemId);
        boolean stockChanged = !Objects.equals(item.getDailyStock(), form.getDailyStock());
        copyFormToItem(form, item);
        if (stockChanged) {
            item.setStockLeft(form.getDailyStock());
        }
        menuRepository.updateItem(item);
    }

    /**
     * Deletes an item that was never ordered. Ordered items must stay, because old
     * orders point to them; the admin can untick "available" instead.
     *
     * @throws BusinessException if the item appears in any order
     */
    public void deleteItem(Long itemId) {
        MenuItem item = findItem(itemId);
        if (menuRepository.countOrderLines(itemId) > 0) {
            throw new BusinessException("\"" + item.getName()
                    + "\" has orders, so it can't be deleted. Untick \"available\" instead.");
        }
        menuRepository.deleteItem(itemId);
    }

    /** Helper: loads an item or throws NotFoundException. */
    private MenuItem findItem(Long itemId) {
        return menuRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Menu item " + itemId + " was not found."));
    }

    /** Helper: a tampered form could send a category id that does not exist. */
    private void checkCategoryExists(Long categoryId) {
        if (menuRepository.findCategoryById(categoryId).isEmpty()) {
            throw new BusinessException("Please choose a valid category.");
        }
    }

    /** Helper: copies the form fields into the item (not the id and not today's stock). */
    private void copyFormToItem(ItemForm form, MenuItem item) {
        item.setName(form.getName().trim());
        item.setDescription(form.getDescription() == null ? null : form.getDescription().trim());
        item.setPriceIqd(form.getPriceIqd());
        item.setCategoryId(form.getCategoryId());
        item.setVegetarian(form.isVegetarian());
        item.setAvailable(form.isAvailable());
        item.setDailyStock(form.getDailyStock());
    }
}
