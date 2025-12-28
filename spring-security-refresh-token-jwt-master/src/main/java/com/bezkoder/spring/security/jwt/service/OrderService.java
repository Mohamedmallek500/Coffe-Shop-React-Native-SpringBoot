package com.bezkoder.spring.security.jwt.service;

import com.bezkoder.spring.security.jwt.models.*;
import com.bezkoder.spring.security.jwt.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
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

        // Récupérer les items du panier
        List<CartItem> cartItems = cartItemRepository.findByUser(user);
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        // Valider que tous les produits sont disponibles
        for (CartItem cartItem : cartItems) {
            if (!cartItem.getProduct().isAvailable()) {
                throw new RuntimeException("Product " + cartItem.getProduct().getName() + " is no longer available");
            }
        }

        BigDecimal subtotal = BigDecimal.ZERO;

        // Créer la commande
        Order order = new Order();
        order.setUser(user);
        order.setPaymentMethod(paymentMethod);
        order.setStatus("PENDING");

        order = orderRepository.save(order);

        List<OrderItem> orderItems = new ArrayList<>();

        // Convertir les CartItems en OrderItems
        for (CartItem cartItem : cartItems) {

            // Calculer le total de cet item (prix unitaire * quantité)
            BigDecimal itemTotal = cartItem.getUnitPrice()
                    .multiply(BigDecimal.valueOf(cartItem.getQuantity()));

            subtotal = subtotal.add(itemTotal);

            // Créer l'OrderItem
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getUnitPrice()); // Prix avec options déjà calculé

            // 🔥 IMPORTANT : Copier les options sélectionnées
            if (cartItem.getSelectedOptions() != null && !cartItem.getSelectedOptions().isEmpty()) {
                orderItem.setSelectedOptions(new ArrayList<>(cartItem.getSelectedOptions()));
            }

            orderItems.add(orderItem);
            orderItemRepository.save(orderItem);
        }

        // Appliquer le code de réduction si fourni
        BigDecimal discountAmount = BigDecimal.ZERO;
        Discount appliedDiscount = null;

        if (discountCode != null && !discountCode.isBlank()) {
            appliedDiscount = discountRepository
                    .findByCodeAndIsActiveTrue(discountCode)
                    .orElseThrow(() -> new RuntimeException("Invalid or inactive discount code"));

            // Calculer le montant de la réduction
            discountAmount = subtotal
                    .multiply(appliedDiscount.getPercentage())
                    .divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);

            order.setDiscountCode(appliedDiscount);
        }

        // Calculer le total final
        BigDecimal total = subtotal.subtract(discountAmount);

        // S'assurer que le total n'est pas négatif
        if (total.compareTo(BigDecimal.ZERO) < 0) {
            total = BigDecimal.ZERO;
        }

        order.setSubtotal(subtotal);
        order.setDiscount(discountAmount);
        order.setTotal(total);
        order.setItems(orderItems);

        // Simuler le paiement (dans un vrai système, appeler une API de paiement)
        order.setStatus("PAID");

        // Sauvegarder la commande avec tous les détails
        order = orderRepository.save(order);

        // Vider le panier après la commande réussie
        cartItemRepository.deleteByUser(user);

        return order;
    }

    // =========================
    // GET MY ORDERS
    // =========================
    public List<Order> getMyOrders(String email) {
        Utilisateur user = getCurrentUser(email);
        return orderRepository.findByUser(user);
    }

    // =========================
    // GET ORDER BY ID
    // =========================
    public Order getOrderById(String email, Long orderId) {
        Utilisateur user = getCurrentUser(email);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Vérifier que la commande appartient bien à l'utilisateur
        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to order");
        }

        return order;
    }

    // =========================
    // CANCEL ORDER (si PENDING)
    // =========================
    @Transactional
    public Order cancelOrder(String email, Long orderId) {
        Order order = getOrderById(email, orderId);

        // On ne peut annuler que les commandes en attente
        if (!"PENDING".equals(order.getStatus())) {
            throw new RuntimeException("Cannot cancel order with status: " + order.getStatus());
        }

        order.setStatus("CANCELLED");
        return orderRepository.save(order);
    }

    // =========================
    // GET ORDER STATISTICS (utilitaire)
    // =========================
    public OrderStatistics getOrderStatistics(String email) {
        Utilisateur user = getCurrentUser(email);
        List<Order> orders = orderRepository.findByUser(user);

        OrderStatistics stats = new OrderStatistics();
        stats.setTotalOrders(orders.size());

        BigDecimal totalSpent = BigDecimal.ZERO;
        int completedOrders = 0;
        int pendingOrders = 0;
        int cancelledOrders = 0;

        for (Order order : orders) {
            totalSpent = totalSpent.add(order.getTotal());

            switch (order.getStatus()) {
                case "PAID", "COMPLETED" -> completedOrders++;
                case "PENDING" -> pendingOrders++;
                case "CANCELLED" -> cancelledOrders++;
            }
        }

        stats.setTotalSpent(totalSpent);
        stats.setCompletedOrders(completedOrders);
        stats.setPendingOrders(pendingOrders);
        stats.setCancelledOrders(cancelledOrders);

        return stats;
    }

    // =========================
    // CLASSE INTERNE : STATISTIQUES
    // =========================
    public static class OrderStatistics {
        private int totalOrders;
        private BigDecimal totalSpent;
        private int completedOrders;
        private int pendingOrders;
        private int cancelledOrders;

        // Getters et Setters
        public int getTotalOrders() { return totalOrders; }
        public void setTotalOrders(int totalOrders) { this.totalOrders = totalOrders; }

        public BigDecimal getTotalSpent() { return totalSpent; }
        public void setTotalSpent(BigDecimal totalSpent) { this.totalSpent = totalSpent; }

        public int getCompletedOrders() { return completedOrders; }
        public void setCompletedOrders(int completedOrders) { this.completedOrders = completedOrders; }

        public int getPendingOrders() { return pendingOrders; }
        public void setPendingOrders(int pendingOrders) { this.pendingOrders = pendingOrders; }

        public int getCancelledOrders() { return cancelledOrders; }
        public void setCancelledOrders(int cancelledOrders) { this.cancelledOrders = cancelledOrders; }
    }

    // =========================
    // ADMIN : GET ALL ORDERS
    // =========================
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // =========================
    // ADMIN : UPDATE ORDER STATUS
    // =========================
    @Transactional
    public Order updateOrderStatus(Long orderId, String newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Valider le statut
        List<String> validStatuses = List.of("PENDING", "PAID", "PROCESSING", "SHIPPED", "DELIVERED", "CANCELLED");
        if (!validStatuses.contains(newStatus)) {
            throw new RuntimeException("Invalid status: " + newStatus);
        }

        order.setStatus(newStatus);
        return orderRepository.save(order);
    }

    // =========================
    // VALIDATE DISCOUNT CODE (avant checkout)
    // =========================
    public Discount validateDiscountCode(String code) {
        return discountRepository.findByCodeAndIsActiveTrue(code)
                .orElseThrow(() -> new RuntimeException("Invalid or inactive discount code"));
    }

    // =========================
    // CALCULATE ORDER PREVIEW (avant checkout)
    // =========================
    public OrderPreview calculateOrderPreview(String email, String discountCode) {
        Utilisateur user = getCurrentUser(email);
        List<CartItem> cartItems = cartItemRepository.findByUser(user);

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        BigDecimal subtotal = BigDecimal.ZERO;

        for (CartItem item : cartItems) {
            BigDecimal itemTotal = item.getUnitPrice()
                    .multiply(BigDecimal.valueOf(item.getQuantity()));
            subtotal = subtotal.add(itemTotal);
        }

        BigDecimal discountAmount = BigDecimal.ZERO;
        Discount discount = null;

        if (discountCode != null && !discountCode.isBlank()) {
            discount = discountRepository.findByCodeAndIsActiveTrue(discountCode)
                    .orElse(null);

            if (discount != null) {
                discountAmount = subtotal
                        .multiply(discount.getPercentage())
                        .divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);
            }
        }

        BigDecimal total = subtotal.subtract(discountAmount);
        if (total.compareTo(BigDecimal.ZERO) < 0) {
            total = BigDecimal.ZERO;
        }

        return new OrderPreview(subtotal, discountAmount, total, discount);
    }

    // =========================
    // CLASSE INTERNE : PREVIEW
    // =========================
    public static class OrderPreview {
        private BigDecimal subtotal;
        private BigDecimal discount;
        private BigDecimal total;
        private Discount discountCode;

        public OrderPreview(BigDecimal subtotal, BigDecimal discount, BigDecimal total, Discount discountCode) {
            this.subtotal = subtotal;
            this.discount = discount;
            this.total = total;
            this.discountCode = discountCode;
        }

        // Getters
        public BigDecimal getSubtotal() { return subtotal; }
        public BigDecimal getDiscount() { return discount; }
        public BigDecimal getTotal() { return total; }
        public Discount getDiscountCode() { return discountCode; }
    }
}