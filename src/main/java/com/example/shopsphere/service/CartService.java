package com.example.shopsphere.service;

import com.example.shopsphere.dto.CartRequest;
import com.example.shopsphere.dto.CartResponse;
import com.example.shopsphere.entity.*;
import com.example.shopsphere.repository.CartRepository;
import com.example.shopsphere.repository.ProductRepository;
import com.example.shopsphere.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    public CartResponse addToCart(Long userId, CartRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Cart cart = cartRepository.findByUser(user).orElse(new Cart());
        cart.setUser(user);

        cart.addProduct(product, request.getQuantity());
        cart.calculateTotalPrice();
        cartRepository.save(cart);

        return mapToResponse(cart);
    }

    public CartResponse removeFromCart(Long userId, Long productId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        cart.getItems().removeIf(item -> item.getProduct().getId().equals(productId));
        cart.calculateTotalPrice();
        cartRepository.save(cart);

        return mapToResponse(cart);
    }

    public CartResponse getCart(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = cartRepository.findByUser(user).orElse(new Cart());
        return mapToResponse(cart);
    }

    private CartResponse mapToResponse(Cart cart) {
        List<CartResponse.CartItem> items = cart.getItems().stream()
                .map(ci -> new CartResponse.CartItem(
                        ci.getProduct().getId(),
                        ci.getProduct().getName(),
                        ci.getQuantity(),
                        ci.getProduct().getPrice()
                )).collect(Collectors.toList());

        return new CartResponse(items, cart.getTotalPrice());
    }
}
