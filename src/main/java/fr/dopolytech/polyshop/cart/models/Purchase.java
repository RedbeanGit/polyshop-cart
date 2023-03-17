package fr.dopolytech.polyshop.cart.models;

public class Purchase {
    public String productId;
    public Long count;

    public Purchase(String productId, Long count) {
        this.productId = productId;
        this.count = count;
    }
}
