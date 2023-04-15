package fr.dopolytech.polyshop.cart.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import fr.dopolytech.polyshop.cart.dtos.AddToCartDto;
import fr.dopolytech.polyshop.cart.models.Product;
import fr.dopolytech.polyshop.cart.services.ProductService;

@RestController
@RequestMapping("/cart")
public class CartController {
	private final ProductService productService;

	public CartController(ProductService cartService) {
		this.productService = cartService;
	}

	@GetMapping
	public Flux<Product> findAll() {
		return productService.getProducts();
	}

	@PostMapping("/add")
	public Mono<Product> addToCart(@RequestBody AddToCartDto dto) {
		return productService.addToCart(dto);
	}

	@PostMapping("/remove")
	public Mono<Product> removeFromCart(@RequestBody AddToCartDto dto) {
		return productService.removeFromCart(dto);
	}

	@PostMapping("/clear")
	public Mono<Void> clear() {
		return productService.clearProducts();
	}

	@PostMapping("/checkout")
	public boolean checkout() throws Exception {
		return productService.checkout();
	}
}