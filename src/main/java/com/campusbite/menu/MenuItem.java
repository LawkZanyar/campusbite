package com.campusbite.menu;

import java.util.Locale;

/**
 * M3 Menu Catalogue (owner: Pasha) - CONTRACT used by checkout (Hananeh).
 * One dish or drink on the menu. Prices are whole Iraqi dinars (int).
 * dailyStock / stockLeft are Integer (not int) because null means "unlimited".
 */
public class MenuItem {

    private Long id;
    private Long categoryId;
    private String categoryName;
    private String name;
    private String description;
    private int priceIqd;
    private String imageUrl;
    private boolean vegetarian;
    private boolean available = true;
    private Integer dailyStock;
    private Integer stockLeft;

    /** Empty constructor for the repositories. */
    public MenuItem() {
    }

    /**
     * True when the item cannot be ordered right now: it was taken off the menu
     * (available = false) or today's stock is used up (stockLeft is 0).
     */
    public boolean isSoldOut() {
        if (!available) {
            return true;
        }
        return stockLeft != null && stockLeft <= 0;
    }

    /** The price as text for the pages, e.g. 5000 becomes "5,000 IQD". */
    public String getPriceLabel() {
        return String.format(Locale.US, "%,d IQD", priceIqd);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getPriceIqd() { return priceIqd; }
    public void setPriceIqd(int priceIqd) { this.priceIqd = priceIqd; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public boolean isVegetarian() { return vegetarian; }
    public void setVegetarian(boolean vegetarian) { this.vegetarian = vegetarian; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public Integer getDailyStock() { return dailyStock; }
    public void setDailyStock(Integer dailyStock) { this.dailyStock = dailyStock; }

    public Integer getStockLeft() { return stockLeft; }
    public void setStockLeft(Integer stockLeft) { this.stockLeft = stockLeft; }
}
