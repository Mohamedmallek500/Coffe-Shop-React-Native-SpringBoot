package com.bezkoder.spring.security.jwt.service;

import com.bezkoder.spring.security.jwt.models.*;
import com.bezkoder.spring.security.jwt.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartItemRepository cartItemRepository;
    private final DiscountRepository discountRepository;
    private final UtilisateurRepository utilisateurRepository;

    public OrderService(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            CartItemRepository cartItemRepository,
            DiscountRepository discountRepository,
            UtilisateurRepository utilisateurRepository) {

        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartItemRepository = cartItemRepository;
        this.discountRepository = discountRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    private Utilisateur getCurrentUser(String email) {
        return utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // =========================
    // CREATE ORDER (CHECKOUT)
    // =========================
    @Transactional
    public Order createOrder(String email, String paymentMethod, String discountCode) {

        Utilisateur user = getCurrentUser(email);

        List<CartItem> cartItems = cartItemRepository.findByUser(user);
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        BigDecimal subtotal = BigDecimal.ZERO;

        Order order = new Order();
        order.setUser(user);
        order.setPaymentMethod(paymentMethod);
        order.setStatus("PENDING");

        order = orderRepository.save(order);

        for (CartItem cartItem : cartItems) {
            BigDecimal itemTotal =
                    cartItem.getUnitPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            subtotal = subtotal.add(itemTotal);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getUnitPrice());

            orderItemRepository.save(orderItem);
        }

        BigDecimal discountAmount = BigDecimal.ZERO;

        if (discountCode != null && !discountCode.isBlank()) {
            Discount discount = discountRepository
                    .findByCodeAndIsActiveTrue(discountCode)
                    .orElseThrow(() -> new RuntimeException("Invalid discount code"));

            discountAmount = subtotal
                    .multiply(discount.getPercentage())
                    .divide(BigDecimal.valueOf(100));

            order.setDiscountCode(discount);
        }

        order.setSubtotal(subtotal);
        order.setDiscount(discountAmount);
        order.setTotal(subtotal.subtract(discountAmount));

        order.setStatus("PAID"); // 💳 paiement simulé

        cartItemRepository.deleteByUser(user); // vider panier

        return orderRepository.save(order);
    }

    // =========================
    // GET MY ORDERS
    // =========================
    public List<Order> getMyOrders(String email) {
        Utilisateur user = getCurrentUser(email);
        return orderRepository.findByUser(user);
    }
}
