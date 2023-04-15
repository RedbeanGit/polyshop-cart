package fr.dopolytech.polyshop.cart.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import fr.dopolytech.polyshop.cart.dtos.AddToCartDto;
import fr.dopolytech.polyshop.cart.models.Product;
import fr.dopolytech.polyshop.cart.services.PurchaseService;
import fr.dopolytech.polyshop.cart.services.QueueService;

@RestController
@RequestMapping("/cart")
public class CartController {
	private final PurchaseService purchaseService;
	private final QueueService queueService;

	public CartController(PurchaseService cartService, QueueService queueService) {
		this.purchaseService = cartService;
		this.queueService = queueService;
	}

	@PostMapping("/add")
	public Mono<Product> addToCart(@RequestBody AddToCartDto dto) {
		return purchaseService.addToCart(dto);
	}

	@PostMapping("/remove")
	public Mono<Product> removeFromCart(@RequestBody AddToCartDto dto) {
		return purchaseService.removeFromCart(dto);
	}

	@PostMapping("/checkout")
	public Mono<Void> checkout() throws Exception {
		List<Product> purchases = purchaseService.getAllBlocking();
		String message = queueService.createMessage(purchases);

		queueService.sendCheckout(message);
		return purchaseService.deleteAll();
	}

	@GetMapping
	public Flux<Product> findAll() {
		return purchaseService.findAll();
	}
}