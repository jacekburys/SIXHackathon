package com.wishlist.wishlist;

/**
 * Created by jacek on 05/03/16.
 */
public class Product {

    private String id, name;
    private boolean selected;

    public Product(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }
}
