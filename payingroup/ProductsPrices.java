package com.example.andre.payingroup;

/**
 * Created by Andre on 24/04/18.
 */

public class ProductsPrices {

    private String products;
    private String prices;

    public ProductsPrices(String products, String prices) {

        this.products = products;
        this.prices = prices;
    }

    public String getProducts() {
        return products;
    }

    public String getPrices() {
        return prices;
    }

    public void setProducts() {
        this.products = products;
    }

    public void setPrices() {
        this.prices = prices;
    }
}
