package fr.dopolytech.polyshop.cart.models;

public class PolyshopEvent {
    public String orderId; // can be null
    public PolyshopEventProduct[] products;

    public PolyshopEvent() {
    }

    public PolyshopEvent(PolyshopEventProduct[] products) {
        this.products = products;
    }

    public PolyshopEvent(String orderId, PolyshopEventProduct[] products) {
        this.orderId = orderId;
        this.products = products;
    }
}
