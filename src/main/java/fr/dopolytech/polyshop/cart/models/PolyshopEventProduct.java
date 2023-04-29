package fr.dopolytech.polyshop.cart.models;

public class PolyshopEventProduct {
    public String id;
    public String name;
    public Double price;
    public Integer quantity;

    public PolyshopEventProduct() {
    }

    public PolyshopEventProduct(String id, String name, Double price, Integer quantity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }
}
