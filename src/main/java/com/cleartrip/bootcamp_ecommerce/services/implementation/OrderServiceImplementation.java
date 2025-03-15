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
import java.util.stream.Collectors;

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

        Order order = new Order();
        order.setUser(user);
        order.setTotalAmount(orderRequest.getTotalAmount());
        order.setStatus(OrderStatus.PENDING);
        order.setShippingAddress(orderRequest.getShippingAddress());

        // Save the order first to get its ID
        order = orderRepository.save(order);

        List<OrderItems> orderItems = new ArrayList<>();

        for (OrderItemRequest itemRequest : orderRequest.getOrderItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            Inventory inventory = inventoryRepository.findByProductId(product.getId())
                    .orElseThrow(() -> new RuntimeException("Inventory not found for product " + product.getId()));

            if (inventory.getStockQuantity() < itemRequest.getQuantity()) {
                throw new RuntimeException("Not enough stock for product: " + product.getName());
            }

            // Deduct stock
            inventory.setStockQuantity(inventory.getStockQuantity() - itemRequest.getQuantity());
            inventoryRepository.save(inventory);

            OrderItems orderItem = new OrderItems();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(itemRequest.getQuantity());

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

        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setShippingAddress(shippingAddress);

        List<OrderItems> orderItems = new ArrayList<>();

         for(CartItem cartItem : cart.getCartItems()){

            Product product = productRepository.findById(cartItem.getProduct().getId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            Inventory inventory = inventoryRepository.findByProductId(product.getId())
                    .orElseThrow(() -> new RuntimeException("Inventory not found for product " + product.getId()));

            if (inventory.getStockQuantity() < cartItem.getQuantity()) {
                throw new RuntimeException("Not enough stock for product: " + product.getName());
            }

            // Deduct stock
            inventory.setStockQuantity(inventory.getStockQuantity() - cartItem.getQuantity());
            inventoryRepository.save(inventory);


            OrderItems orderItem = new OrderItems();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());

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
}
