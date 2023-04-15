package fr.dopolytech.polyshop.cart.models;

public class Product {
    public String productId;
    public int quantity;

    public Product(String productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }
}
