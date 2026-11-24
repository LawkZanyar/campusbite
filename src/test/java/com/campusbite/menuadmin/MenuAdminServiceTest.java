package com.campusbite.menuadmin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.campusbite.common.BusinessException;
import com.campusbite.common.NotFoundException;
import com.campusbite.menu.InMemoryMenuRepository;
import com.campusbite.menu.MenuItem;

/**
 * M4 Menu & Stock Management (owner: Pasha).
 * Unit tests for creating, editing and deleting items (in-memory, no Spring).
 */
class MenuAdminServiceTest {

    private InMemoryMenuRepository repository;
    private MenuAdminService service;

    @BeforeEach
    void setUp() {
        repository = new InMemoryMenuRepository();
        service = new MenuAdminService(repository);
    }

    private ItemForm form(String name, int price, long categoryId, Integer dailyStock) {
        ItemForm form = new ItemForm();
        form.setName(name);
        form.setDescription("Test item");
        form.setPriceIqd(price);
        form.setCategoryId(categoryId);
        form.setAvailable(true);
        form.setDailyStock(dailyStock);
        return form;
    }

    @Test
    void createItemStartsWithFullStock() {
        MenuItem item = service.createItem(form("Tea with milk", 750, 3L, 40));

        assertThat(item.getId()).isEqualTo(17L);
        assertThat(item.getStockLeft()).isEqualTo(40);
        assertThat(service.listItems()).hasSize(17);
    }

    @Test
    void createItemWithUnknownCategoryFails() {
        assertThatThrownBy(() -> service.createItem(form("Ghost", 1000, 99L, null)))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("valid category");
    }

    @Test
    void editFormIsFilledFromTheItem() {
        ItemForm edit = service.getItemForm(1L);

        assertThat(edit.getName()).isEqualTo("Chicken biryani");
        assertThat(edit.getPriceIqd()).isEqualTo(5000);
        assertThat(edit.getDailyStock()).isEqualTo(30);
    }

    @Test
    void updateResetsTodaysStockOnlyWhenDailyStockChanges() {
        repository.decrementStock(1L, 5);                    // 30 -> 25 left

        ItemForm sameStock = service.getItemForm(1L);
        sameStock.setPriceIqd(5500);
        service.updateItem(1L, sameStock);
        assertThat(repository.findById(1L).get().getStockLeft()).isEqualTo(25);

        ItemForm newStock = service.getItemForm(1L);
        newStock.setDailyStock(50);
        service.updateItem(1L, newStock);
        assertThat(repository.findById(1L).get().getStockLeft()).isEqualTo(50);
        assertThat(repository.findById(1L).get().getPriceIqd()).isEqualTo(5500);
    }

    @Test
    void deleteRemovesTheItem() {
        service.deleteItem(13L);

        assertThatThrownBy(() -> service.getItemForm(13L)).isInstanceOf(NotFoundException.class);
    }
}
