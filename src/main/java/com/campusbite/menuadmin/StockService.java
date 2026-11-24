package com.campusbite.menuadmin;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.campusbite.common.BusinessException;
import com.campusbite.menu.MenuItem;
import com.campusbite.menu.MenuRepository;

/**
 * M4 Menu & Stock Management (owner: Pasha) - CONTRACT used by checkout (Hananeh)
 * and order cancel (Fatemeh).
 * Today's stock: take stock away when an order is placed, give it back on cancel,
 * and let the kitchen set the numbers by hand.
 *
 * decrement/restore have NO @Transactional on purpose: they are always called from
 * the caller's @Transactional method (placeOrder, cancel), so they join that
 * transaction and are saved or rolled back together with the order.
 */
@Service
public class StockService {

    private final MenuRepository menuRepository;

    /** Constructor injection of my MenuRepository. */
    public StockService(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    /**
     * Takes qty units of an item from today's stock.
     *
     * @throws OutOfStockException if not enough is left (nothing is changed then)
     */
    public void decrement(Long itemId, int qty) {
        int changed = menuRepository.decrementStock(itemId, qty);
        if (changed == 0) {
            String name = menuRepository.findById(itemId).map(MenuItem::getName).orElse("This item");
            throw new OutOfStockException(itemId, name);
        }
    }

    /** Gives qty units back (an order was cancelled). Unlimited items stay unlimited. */
    public void restore(Long itemId, int qty) {
        menuRepository.restoreStock(itemId, qty);
    }

    /** Every item with its stock, for the stock page. */
    public List<MenuItem> listStock() {
        return menuRepository.findAllItems();
    }

    /**
     * Saves the stock page. For each item the form has "left_ID" (a number, or empty for
     * unlimited) and, when ticked, "soldOut_ID" (sold out = 0 left).
     * All values are checked FIRST; only if every value is valid do we save them,
     * so one typo never saves half of the page.
     *
     * @throws BusinessException if a value is not a whole number from 0 to 1000
     */
    public void saveStock(Map<String, String> form) {
        Map<Long, Integer> newValues = new LinkedHashMap<>();
        for (MenuItem item : menuRepository.findAllItems()) {
            String key = "left_" + item.getId();
            if (!form.containsKey(key)) {
                continue;                               // item was not on the page
            }
            if (form.containsKey("soldOut_" + item.getId())) {
                newValues.put(item.getId(), 0);
            } else {
                newValues.put(item.getId(), parseStock(item.getName(), form.get(key)));
            }
        }
        for (Map.Entry<Long, Integer> entry : newValues.entrySet()) {
            menuRepository.updateStockLeft(entry.getKey(), entry.getValue());
        }
    }

    /** Start of a new day: every item gets its daily stock again. */
    public void resetAll() {
        menuRepository.resetAllStock();
    }

    /** Helper: "" means unlimited (null); otherwise a whole number from 0 to 1000. */
    private Integer parseStock(String itemName, String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        try {
            int value = Integer.parseInt(text.trim());
            if (value < 0 || value > 1000) {
                throw new BusinessException("Stock for \"" + itemName + "\" must be from 0 to 1000.");
            }
            return value;
        } catch (NumberFormatException ex) {
            throw new BusinessException("Stock for \"" + itemName + "\" must be a whole number.");
        }
    }
}
