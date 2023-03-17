package fr.dopolytech.polyshop.cart.controllers;

import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import fr.dopolytech.polyshop.cart.dtos.AddToCartDto;
import fr.dopolytech.polyshop.cart.models.Purchase;

@RestController
@RequestMapping("/cart")
public class CartController {
	private final ReactiveRedisOperations<String, Long> purchaseOperations;

	CartController(ReactiveRedisOperations<String, Long> purchaseOperations) {
		this.purchaseOperations = purchaseOperations;
	}

	@PostMapping
	public Mono<Purchase> addToCart(@RequestBody AddToCartDto dto) {
		return purchaseOperations.opsForValue().increment(dto.getProductId()).map(count -> new Purchase(dto.getProductId(), count));
	}

	@GetMapping
	public Flux<Purchase> findAll() {
		return purchaseOperations
			.keys("*")
			.flatMap(key -> purchaseOperations.opsForValue().get(key).map(purchase -> new Purchase(key, purchase)));
	}
}