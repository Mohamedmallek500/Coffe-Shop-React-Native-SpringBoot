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

    // Obtenir une commande spécifique
    @GetMapping("/{orderId}")
    public Order getOrder(@PathVariable Long orderId,
                          Authentication authentication) {
        return orderService.getOrderById(authentication.getName(), orderId);
    }

    // Annuler une commande
    @PutMapping("/{orderId}/cancel")
    public Order cancelOrder(@PathVariable Long orderId,
                             Authentication authentication) {
        return orderService.cancelOrder(authentication.getName(), orderId);
    }

    // Statistiques
    @GetMapping("/statistics")
    public OrderService.OrderStatistics getStatistics(Authentication authentication) {
        return orderService.getOrderStatistics(authentication.getName());
    }

    // Preview du total avant checkout
    @PostMapping("/preview")
    public OrderService.OrderPreview previewOrder(
            @RequestParam(required = false) String discountCode,
            Authentication authentication) {
        return orderService.calculateOrderPreview(authentication.getName(), discountCode);
    }

    // ADMIN : Toutes les commandes
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/admin/all")
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    // ADMIN : Changer le statut
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/admin/{orderId}/status")
    public Order updateStatus(@PathVariable Long orderId,
                              @RequestParam String status) {
        return orderService.updateOrderStatus(orderId, status);
    }
}
