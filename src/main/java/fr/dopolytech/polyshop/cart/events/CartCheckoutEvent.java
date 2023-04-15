package fr.dopolytech.polyshop.cart.events;

public class CartCheckoutEvent {
    public CartCheckoutEventProduct[] products;

    public CartCheckoutEvent(CartCheckoutEventProduct[] products) {
        this.products = products;
    }
}
