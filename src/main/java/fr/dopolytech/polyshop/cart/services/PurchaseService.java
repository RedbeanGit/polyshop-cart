package fr.dopolytech.polyshop.cart.services;

import java.util.List;

import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Service;

import fr.dopolytech.polyshop.cart.dtos.AddToCartDto;
import fr.dopolytech.polyshop.cart.models.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class PurchaseService {
    private final ReactiveRedisOperations<String, Long> purchaseOperations;

    public PurchaseService(ReactiveRedisOperations<String, Long> purchaseOperations) {
        this.purchaseOperations = purchaseOperations;
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

    public List<Product> getAllBlocking() {
        return purchaseOperations.keys("*")
                .flatMap(key -> purchaseOperations.opsForValue().get(key)
                        .map(quantity -> new Product(key, quantity.intValue())))
                .collectList()
                .block();
    }

    public Flux<Product> findAll() {
        return purchaseOperations
                .keys("*")
                .flatMap(key -> purchaseOperations.opsForValue().get(key)
                        .map(quantity -> new Product(key, quantity.intValue())));
    }

    public Mono<Void> deleteAll() {
        return purchaseOperations.keys("*").flatMap(key -> purchaseOperations.delete(key)).then();
    }
}
