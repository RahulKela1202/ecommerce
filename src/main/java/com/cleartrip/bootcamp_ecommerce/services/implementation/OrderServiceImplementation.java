package com.cleartrip.bootcamp_ecommerce.services.implementation;

import com.cleartrip.bootcamp_ecommerce.dto.OrderItemRequest;
import com.cleartrip.bootcamp_ecommerce.dto.OrderRequest;
import com.cleartrip.bootcamp_ecommerce.models.*;
import com.cleartrip.bootcamp_ecommerce.repository.*;
import com.cleartrip.bootcamp_ecommerce.services.CartService;
import com.cleartrip.bootcamp_ecommerce.services.OrderService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class OrderServiceImplementation implements OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderItemsRepository orderItemsRepository;
    private final InventoryRepository inventoryRepository;
    private final CartRepository cartRepository;
    private final CartService cartService;

    @Autowired
    public OrderServiceImplementation(OrderRepository orderRepository,
                                      UserRepository userRepository,
                                      ProductRepository productRepository,
                                      OrderItemsRepository orderItemsRepository,
                                      InventoryRepository inventoryRepository,
                                      CartRepository cartRepository,
                                      CartService cartService){
        this.orderRepository  = orderRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.orderItemsRepository = orderItemsRepository;
        this.inventoryRepository = inventoryRepository;
        this.cartRepository = cartRepository;
        this.cartService = cartService;
    }

    @Override
    @Transactional
    public Order createOrder(OrderRequest orderRequest) {
        User user = userRepository.findById(orderRequest.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = new Order(user,orderRequest.getTotalAmount(),OrderStatus.PENDING,orderRequest.getShippingAddress());
        // Save the order first to get its ID
        order = orderRepository.save(order);

        List<OrderItems> orderItems = new ArrayList<>();

        // update the inventory and add the orderItem to orderItems array
        for (OrderItemRequest itemRequest : orderRequest.getOrderItems()) {
            inventoryUpdate(itemRequest.getProductId(),itemRequest.getQuantity());
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            OrderItems orderItem = new OrderItems(order,product,itemRequest.getQuantity());
            orderItems.add(orderItem);
        }

        orderItemsRepository.saveAll(orderItems);

        order.setOrderItems(orderItems);

        return order;
    }

    @Transactional
    public Order checkout(Long userId, String shippingAddress) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = cartService.getCartByUser(user);
        if (cart.getCartItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        Order order = new Order(user,OrderStatus.PENDING,shippingAddress);

        List<OrderItems> orderItems = new ArrayList<>();

        // update the inventory and add the cartItem to orderItems array
         for(CartItem cartItem : cart.getCartItems()){
            inventoryUpdate(cartItem.getProduct().getId(),cartItem.getQuantity());
            OrderItems orderItem = new OrderItems(order,cartItem.getProduct(),cartItem.getQuantity());
            orderItems.add(orderItem);
        }

        order.setOrderItems(orderItems);
        order.setTotalAmount(orderItems.stream()
                .mapToDouble(item -> item.getProduct().getPrice().doubleValue() * item.getQuantity())
                .sum());

        cart.getCartItems().clear();
        cartRepository.save(cart);
        orderRepository.save(order);
        return order;
    }

    @Override
    public List<Order> getAllOrder(){
        return orderRepository.findAll();
    }

    @Override
    public Optional<Order> getOrderById(Long id){
        return orderRepository.findById(id);
    }

    @Override
    public List<Order> getOrderByUserId(Long id){
        return orderRepository.findAllByUserId(id);
    }

    @Transactional
    public void updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(status);
        orderRepository.save(order);
    }

    public void inventoryUpdate(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Inventory inventory = inventoryRepository.findByProductId(product.getId())
                .orElseThrow(() -> new RuntimeException("Inventory not found for product " + product.getId()));

        if (inventory.getStockQuantity() < quantity) {
            throw new RuntimeException("Not enough stock for product: " + product.getName());
        }

        inventory.setStockQuantity(inventory.getStockQuantity() - quantity);
        inventoryRepository.save(inventory);
    }
}
