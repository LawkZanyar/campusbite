package com.campusbite.menuadmin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.campusbite.common.BusinessException;
import com.campusbite.menu.InMemoryMenuRepository;

/**
 * M4 Menu & Stock Management (owner: Pasha).
 * Unit tests for StockService. Demo items: 1 = Chicken biryani (30),
 * 4 = Lentil soup (unlimited), 8 = Kebab sandwich (0), 16 = Zalabia (10).
 */
class StockServiceTest {

    private InMemoryMenuRepository repository;
    private StockService stockService;

    @BeforeEach
    void setUp() {
        repository = new InMemoryMenuRepository();
        stockService = new StockService(repository);
    }

    private Integer left(long itemId) {
        return repository.findById(itemId).get().getStockLeft();
    }

    @Test
    void decrementWithinStock() {
        stockService.decrement(16L, 4);

        assertThat(left(16L)).isEqualTo(6);
    }

    @Test
    void decrementBeyondStockThrowsAndChangesNothing() {
        assertThatThrownBy(() -> stockService.decrement(16L, 11))
                .isInstanceOf(OutOfStockException.class)
                .hasMessageContaining("Zalabia");

        assertThat(left(16L)).isEqualTo(10);
    }

    @Test
    void unlimitedItemNeverRunsOut() {
        stockService.decrement(4L, 500);

        assertThat(left(4L)).isNull();
    }

    @Test
    void restoreGivesStockBack() {
        stockService.decrement(1L, 3);
        stockService.restore(1L, 3);

        assertThat(left(1L)).isEqualTo(30);
    }

    @Test
    void saveStockSetsNumbersSoldOutAndUnlimited() {
        Map<String, String> form = new HashMap<>();
        form.put("left_1", "12");
        form.put("left_16", "5");
        form.put("soldOut_16", "on");          // sold out wins over the number
        form.put("left_8", "");                 // empty = unlimited

        stockService.saveStock(form);

        assertThat(left(1L)).isEqualTo(12);
        assertThat(left(16L)).isZero();
        assertThat(left(8L)).isNull();
        assertThat(left(4L)).isNull();          // not on the form: unchanged
    }

    @Test
    void saveStockWithBadValueSavesNothing() {
        Map<String, String> form = new HashMap<>();
        form.put("left_1", "12");
        form.put("left_2", "abc");

        assertThatThrownBy(() -> stockService.saveStock(form))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("whole number");
        assertThat(left(1L)).isEqualTo(30);
    }

    @Test
    void resetAllRestoresDailyStock() {
        stockService.decrement(1L, 10);
        stockService.resetAll();

        assertThat(left(1L)).isEqualTo(30);
        assertThat(left(8L)).isZero();         // its daily stock is 0
    }
}
