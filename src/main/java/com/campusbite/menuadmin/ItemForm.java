package com.campusbite.menuadmin;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * M4 Menu & Stock Management (owner: Pasha).
 * The "new item" / "edit item" form. Integer (not int) for numbers, so an empty
 * box becomes null and @NotNull can report "Please enter a price".
 */
public class ItemForm {

    @NotBlank(message = "Please enter a name")
    @Size(max = 100, message = "Name can be at most 100 characters")
    private String name;

    @Size(max = 255, message = "Description can be at most 255 characters")
    private String description;

    @NotNull(message = "Please enter a price")
    @Min(value = 250, message = "Price must be at least 250 IQD")
    @Max(value = 100000, message = "Price can be at most 100,000 IQD")
    private Integer priceIqd;

    @NotNull(message = "Please choose a category")
    private Long categoryId;

    private boolean vegetarian;

    private boolean available = true;

    /** Empty = unlimited. */
    @Min(value = 0, message = "Daily stock cannot be negative")
    @Max(value = 1000, message = "Daily stock can be at most 1000")
    private Integer dailyStock;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getPriceIqd() { return priceIqd; }
    public void setPriceIqd(Integer priceIqd) { this.priceIqd = priceIqd; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public boolean isVegetarian() { return vegetarian; }
    public void setVegetarian(boolean vegetarian) { this.vegetarian = vegetarian; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public Integer getDailyStock() { return dailyStock; }
    public void setDailyStock(Integer dailyStock) { this.dailyStock = dailyStock; }
}
