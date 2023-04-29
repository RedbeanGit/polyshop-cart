package fr.dopolytech.polyshop.cart.services;

import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import fr.dopolytech.polyshop.cart.dtos.AddToCartDto;
import fr.dopolytech.polyshop.cart.models.CatalogProduct;
import fr.dopolytech.polyshop.cart.models.PolyshopEvent;
import fr.dopolytech.polyshop.cart.models.PolyshopEventProduct;
import fr.dopolytech.polyshop.cart.models.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProductService {
    private final ReactiveRedisOperations<String, Long> purchaseOperations;
    private final QueueService queueService;
    private final WebClient.Builder webClientBuilder;

    public ProductService(ReactiveRedisOperations<String, Long> purchaseOperations, QueueService queueService,
            WebClient.Builder webClientBuilder) {
        this.purchaseOperations = purchaseOperations;
        this.queueService = queueService;
        this.webClientBuilder = webClientBuilder;
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

    public Mono<Boolean> checkout() {
        WebClient client = webClientBuilder.build();

        return getProducts().collectList()
                .flatMap(products -> Flux.merge(products.stream().map(product -> client.get()
                        .uri("lb://catalog-service/products/" + product.productId)
                        .retrieve()
                        .bodyToMono(CatalogProduct.class)
                        .map(catalogProduct -> new PolyshopEventProduct(product.productId, catalogProduct.name,
                                catalogProduct.price, product.quantity)))
                        .toList()).collectList())
                .map(eventProducts -> {
                    PolyshopEvent event = new PolyshopEvent(
                            eventProducts.toArray(new PolyshopEventProduct[eventProducts.size()]));
                    try {
                        queueService.sendCartCheckout(event);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                    return true;
                });
    }

}
