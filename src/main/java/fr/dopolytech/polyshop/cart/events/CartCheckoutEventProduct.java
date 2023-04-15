package fr.dopolytech.polyshop.cart.events;

public class CartCheckoutEventProduct {
    public String productId;
    public int quantity;

    public CartCheckoutEventProduct(String productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }
}
