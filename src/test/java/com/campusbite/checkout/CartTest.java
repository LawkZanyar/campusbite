package com.campusbite.checkout;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.campusbite.common.BusinessException;
import com.campusbite.menu.MenuItem;

/**
 * M5 Cart & Checkout (owner: Hananeh).
 * Unit tests for the Cart rules (business rule 5).
 */
class CartTest {

    private Cart cart;

    @BeforeEach
    void setUp() {
        cart = new Cart();
    }

    private MenuItem item(long id, String name, int price, Integer stockLeft) {
        MenuItem item = new MenuItem();
        item.setId(id);
        item.setName(name);
        item.setPriceIqd(price);
        item.setAvailable(true);
        item.setStockLeft(stockLeft);
        return item;
    }

    @Test
    void addingTheSameItemTwiceMergesTheLine() {
        MenuItem wrap = item(6, "Falafel wrap", 2500, null);
        cart.add(wrap, 2);
        cart.add(wrap, 1);

        assertThat(cart.getLines()).hasSize(1);
        assertThat(cart.getLines().get(0).getQuantity()).isEqualTo(3);
        assertThat(cart.getTotalIqd()).isEqualTo(7500);
        assertThat(cart.getTotalLabel()).isEqualTo("7,500 IQD");
    }

    @Test
    void moreThanTenOfOneItemIsRejected() {
        MenuItem tea = item(11, "Black tea", 500, null);
        cart.add(tea, 8);

        assertThatThrownBy(() -> cart.add(tea, 3))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("at most 10");
        assertThat(cart.getItemCount()).isEqualTo(8);
    }

    @Test
    void moreThanThirtyItemsInTotalIsRejected() {
        cart.add(item(1, "A", 1000, null), 10);
        cart.add(item(2, "B", 1000, null), 10);
        cart.add(item(3, "C", 1000, null), 10);

        assertThatThrownBy(() -> cart.add(item(4, "D", 1000, null), 1))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("at most 30");
    }

    @Test
    void soldOutItemCannotBeAdded() {
        assertThatThrownBy(() -> cart.add(item(8, "Kebab sandwich", 4000, 0), 1))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("sold out");
    }

    @Test
    void zeroOrNegativeQuantityIsRejected() {
        assertThatThrownBy(() -> cart.add(item(1, "A", 1000, null), 0))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void updateToZeroRemovesTheLine() {
        cart.add(item(1, "A", 1000, null), 2);
        cart.update(1L, 5);
        assertThat(cart.getItemCount()).isEqualTo(5);

        cart.update(1L, 0);
        assertThat(cart.isEmpty()).isTrue();
    }

    @Test
    void removeAndClear() {
        cart.add(item(1, "A", 1000, null), 1);
        cart.add(item(2, "B", 1000, null), 1);
        cart.remove(1L);
        assertThat(cart.getLines()).hasSize(1);

        cart.clear();
        assertThat(cart.isEmpty()).isTrue();
    }
}
