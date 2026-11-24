package com.campusbite.menu;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.campusbite.common.NotFoundException;

/**
 * M3 Menu Catalogue (owner: Pasha).
 * Unit tests for MenuService using the in-memory repository (no Spring needed).
 */
class MenuServiceTest {

    private InMemoryMenuRepository repository;
    private MenuService menuService;

    @BeforeEach
    void setUp() {
        repository = new InMemoryMenuRepository();
        menuService = new MenuService(repository);
    }

    @Test
    void groupsItemsByCategoryInSortOrder() {
        Map<Category, List<MenuItem>> menu = menuService.getMenuGrouped(null, false, null);

        List<String> names = new ArrayList<>();
        for (Category category : menu.keySet()) {
            names.add(category.getName());
        }
        assertThat(names).containsExactly("Hot meals", "Sandwiches", "Drinks", "Desserts");
    }

    @Test
    void categoryFilterKeepsOnlyThatCategory() {
        Map<Category, List<MenuItem>> menu = menuService.getMenuGrouped(3L, false, null);

        assertThat(menu).hasSize(1);
        assertThat(menu.keySet().iterator().next().getName()).isEqualTo("Drinks");
    }

    @Test
    void vegFilterReturnsOnlyVegetarianItemsAndSkipsEmptyCategories() {
        Map<Category, List<MenuItem>> menu = menuService.getMenuGrouped(null, true, null);

        for (List<MenuItem> items : menu.values()) {
            assertThat(items).isNotEmpty();
            for (MenuItem item : items) {
                assertThat(item.isVegetarian()).isTrue();
            }
        }
    }

    @Test
    void unavailableItemsAreHiddenFromTheMenu() {
        MenuItem biryani = menuService.findById(1L);
        biryani.setAvailable(false);

        Map<Category, List<MenuItem>> menu = menuService.getMenuGrouped(1L, false, null);

        assertThat(menu.values().iterator().next()).noneMatch(item -> item.getId().equals(1L));
    }

    @Test
    void findByIdThrowsNotFoundForUnknownId() {
        assertThatThrownBy(() -> menuService.findById(999L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void priceLabelAndSoldOut() {
        MenuItem kebab = menuService.findById(8L);          // demo item with stock 0
        assertThat(kebab.isSoldOut()).isTrue();
        assertThat(menuService.findById(1L).getPriceLabel()).isEqualTo("5,000 IQD");
    }
}
