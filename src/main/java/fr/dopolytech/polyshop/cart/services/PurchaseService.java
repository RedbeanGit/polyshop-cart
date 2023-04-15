package fr.dopolytech.polyshop.cart.services;

import java.util.List;

import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Service;

import fr.dopolytech.polyshop.cart.dtos.AddToCartDto;
import fr.dopolytech.polyshop.cart.models.Purchase;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class PurchaseService {
    private final ReactiveRedisOperations<String, Long> purchaseOperations;

    public PurchaseService(ReactiveRedisOperations<String, Long> purchaseOperations) {
        this.purchaseOperations = purchaseOperations;
    }

    public Mono<Purchase> addToCart(AddToCartDto dto) {
        return purchaseOperations.opsForValue().increment(dto.productId, dto.quantity)
                .map(count -> new Purchase(dto.productId, count));
    }

    public Mono<Purchase> removeFromCart(AddToCartDto dto) {
        if (!purchaseOperations.hasKey(dto.productId).block()) {
            return Mono.just(new Purchase(dto.productId, 0L));
        }
        if (purchaseOperations.opsForValue().get(dto.productId).block() <= dto.quantity) {
            return purchaseOperations.delete(dto.productId).map(count -> new Purchase(dto.productId, 0L));
        }
        return purchaseOperations.opsForValue().decrement(dto.productId, dto.quantity)
                .map(count -> new Purchase(dto.productId, count));
    }

    public List<Purchase> getAllBlocking() {
        return purchaseOperations.keys("*")
                .flatMap(key -> purchaseOperations.opsForValue().get(key).map(count -> new Purchase(key, count)))
                .collectList()
                .block();
    }

    public Flux<Purchase> findAll() {
        return purchaseOperations
                .keys("*")
                .flatMap(key -> purchaseOperations.opsForValue().get(key).map(purchase -> new Purchase(key, purchase)));
    }

    public Mono<Void> deleteAll() {
        return purchaseOperations.keys("*").flatMap(key -> purchaseOperations.delete(key)).then();
    }
}
