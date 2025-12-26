package com.bezkoder.spring.security.jwt.controllers;

import com.bezkoder.spring.security.jwt.models.Order;
import com.bezkoder.spring.security.jwt.service.OrderService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
@PreAuthorize("hasAuthority('CLIENT')")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // =========================
    // CHECKOUT
    // =========================
    @PostMapping
    public Order createOrder(
            @RequestParam String paymentMethod,
            @RequestParam(required = false) String discountCode,
            Authentication authentication) {

        return orderService.createOrder(
                authentication.getName(),
                paymentMethod,
                discountCode
        );
    }

    // =========================
    // MY ORDERS
    // =========================
    @GetMapping
    public List<Order> getMyOrders(Authentication authentication) {
        return orderService.getMyOrders(authentication.getName());
    }
}
