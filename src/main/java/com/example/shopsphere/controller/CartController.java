package com.example.shopsphere.controller;

import com.example.shopsphere.dto.CartRequest;
import com.example.shopsphere.dto.CartResponse;
import com.example.shopsphere.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    // ✅ Add product to cart
    @PostMapping("/add")
    public CartResponse addToCart(@AuthenticationPrincipal UserDetails userDetails,
                                  @RequestBody CartRequest request) {
        Long userId = Long.parseLong(userDetails.getUsername()); // username stores userId in JWT
        return cartService.addToCart(userId, request);
    }

    // ✅ Remove product from cart
    @DeleteMapping("/remove/{productId}")
    public CartResponse removeFromCart(@AuthenticationPrincipal UserDetails userDetails,
                                       @PathVariable Long productId) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return cartService.removeFromCart(userId, productId);
    }

    // ✅ View user’s cart
    @GetMapping
    public CartResponse getCart(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return cartService.getCart(userId);
    }
}
