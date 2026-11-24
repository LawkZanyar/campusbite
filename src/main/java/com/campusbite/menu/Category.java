package com.campusbite.menu;

/**
 * M3 Menu Catalogue (owner: Pasha) - CONTRACT.
 * A menu category such as "Hot meals" or "Drinks". sortOrder decides the
 * order in which categories are shown on the menu page (smallest first).
 */
public class Category {

    private Long id;
    private String name;
    private int sortOrder;

    /** Empty constructor for the repositories. */
    public Category() {
    }

    /** Creates a category with all fields. */
    public Category(Long id, String name, int sortOrder) {
        this.id = id;
        this.name = name;
        this.sortOrder = sortOrder;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }
}
