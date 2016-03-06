package com.wishlist.wishlist;

/**
 * Created by jacek on 06/03/16.
 */
public class DiscountedProduct extends Product{

    String expiry;
    double rate;

    public DiscountedProduct(Product product, String expiry, double rate) {
        super(product);
        this.expiry = expiry;
        this.rate = rate;
    }

    public String getExpiry(){
        return expiry;
    }

    public double getRate(){
        return rate;
    }
}
