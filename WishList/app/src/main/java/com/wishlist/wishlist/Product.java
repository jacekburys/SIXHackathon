package com.wishlist.wishlist;

/**
 * Created by jacek on 05/03/16.
 */
public class Product {

    private String id, name, url, asin;

    public Product(Product p) {
        this.id = p.getId();
        this.name = p.getName();
        this.url = p.getUrl();
    }

    public Product(String id, String name, String url, String asin) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.asin = asin;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getAsin() {
        return asin;
    }

}
