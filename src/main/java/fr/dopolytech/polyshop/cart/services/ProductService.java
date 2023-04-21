package fr.dopolytech.polyshop.cart.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Service;

import fr.dopolytech.polyshop.cart.dtos.AddToCartDto;
import fr.dopolytech.polyshop.cart.events.CartCheckoutEvent;
import fr.dopolytech.polyshop.cart.events.CartCheckoutEventProduct;
import fr.dopolytech.polyshop.cart.models.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProductService {
    private final ReactiveRedisOperations<String, Long> purchaseOperations;
    private final QueueService queueService;

    public ProductService(ReactiveRedisOperations<String, Long> purchaseOperations, QueueService queueService) {
        this.purchaseOperations = purchaseOperations;
        this.queueService = queueService;
    }

    public Mono<Product> addToCart(AddToCartDto dto) {
        return purchaseOperations.opsForValue().increment(dto.productId, dto.quantity)
                .map(quantity -> new Product(dto.productId, quantity.intValue()));
    }

    public Mono<Product> removeFromCart(AddToCartDto dto) {
        if (!purchaseOperations.hasKey(dto.productId).block()) {
            return Mono.just(new Product(dto.productId, 0));
        }
        if (purchaseOperations.opsForValue().get(dto.productId).block() <= dto.quantity) {
            return purchaseOperations.delete(dto.productId).map(count -> new Product(dto.productId, 0));
        }
        return purchaseOperations.opsForValue().decrement(dto.productId, dto.quantity)
                .map(quantity -> new Product(dto.productId, quantity.intValue()));
    }

    public Flux<Product> getProducts() {
        return purchaseOperations
                .keys("*")
                .flatMap(key -> purchaseOperations.opsForValue().get(key)
                        .map(quantity -> new Product(key, quantity.intValue())));
    }

    public Mono<Void> clearProducts() {
        return purchaseOperations.keys("*").flatMap(key -> purchaseOperations.delete(key)).then();
    }

    public boolean checkout() {
        List<Product> products = getProducts().collectList().block();
        List<CartCheckoutEventProduct> checkoutProducts = new ArrayList<CartCheckoutEventProduct>();

        for (Product product : products) {
            checkoutProducts.add(new CartCheckoutEventProduct(product.productId, product.quantity));
        }

        CartCheckoutEvent event = new CartCheckoutEvent(checkoutProducts.toArray(new CartCheckoutEventProduct[0]));

        try {
            queueService.sendCheckout(event);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        this.clearProducts();

        return true;
    }

}
