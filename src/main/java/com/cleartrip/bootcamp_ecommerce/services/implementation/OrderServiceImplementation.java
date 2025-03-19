package com.cleartrip.bootcamp_ecommerce.services.implementation;

import com.cleartrip.bootcamp_ecommerce.dto.OrderItemRequest;
import com.cleartrip.bootcamp_ecommerce.dto.OrderRequest;
import com.cleartrip.bootcamp_ecommerce.exception.InvalidRequestException;
import com.cleartrip.bootcamp_ecommerce.exception.NotFoundException;
import com.cleartrip.bootcamp_ecommerce.models.*;
import com.cleartrip.bootcamp_ecommerce.repository.*;
import com.cleartrip.bootcamp_ecommerce.services.CartService;
import com.cleartrip.bootcamp_ecommerce.services.OrderService;
import com.cleartrip.bootcamp_ecommerce.services.ProductService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class OrderServiceImplementation implements OrderService {
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ProductService productService;
    private final CartService cartService;
    @Autowired
    public OrderServiceImplementation(OrderRepository orderRepository,
                                      CartRepository cartRepository,
                                      ProductService productService,
                                      CartService cartService){
        this.orderRepository  = orderRepository;
        this.cartRepository = cartRepository;
        this.productService = productService;
        this.cartService = cartService;
    }

    @Override
    @Transactional
    public Order checkout(Long userId, String shippingAddress) {

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Cart not found for user: " + userId));

        List<CartItem> cartItems = cart.getCartItems();

        List<OrderItems> orderItems = new ArrayList<>();

        // update the inventory and add the cartItem to orderItems array
         for(CartItem cartItem : cart.getCartItems()){
            OrderItems orderItem = inventoryUpdate(cartItem.getProductId(),cartItem.getQuantity());
            orderItems.add(orderItem);
        }
        
        Order order = new Order(userId,orderItems,cart.getTotalAmount(),shippingAddress,OrderStatus.PENDING);
        order.setOrderItems(orderItems);
        cartService.clear(userId);
        orderRepository.save(order);
        return order;
    }

    @Override
    public List<Order> getAll(){
        return orderRepository.findAll();
    }

    @Override
    public Order getById(Long id){
        return orderRepository.findById(id)
                .orElseThrow(()->new NotFoundException("Order not found"));
    }

    @Override
    public List<Order> getByUserId(Long id){
        return orderRepository.findAllByUserId(id);
    }

    @Override
    @Transactional
    public Order updateStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Order not found"));
        
        // If order is being cancelled, restore inventory
        if (status == OrderStatus.CANCELLED && order.getStatus() != OrderStatus.CANCELLED) {
            order.getOrderItems().forEach(item -> 
                productService.incStock(item.getProductId(), item.getQuantity())
            );
        }
        
        order.setStatus(status);
        return orderRepository.save(order);
    }

    public OrderItems inventoryUpdate(Long productId, int quantity) {
        Product product = productService.getById(productId);

            
            if (product.getQuantity() < quantity) {
                throw new InvalidRequestException("Insufficient stock for product: " + product.getName());
            }
            
            // Create OrderItems from CartItems
            OrderItems orderItem = new OrderItems(product.getId(),product.getName(),quantity,product.getPrice());
            
            
            // Update inventory
            productService.decStock(product.getId(), quantity);

            return orderItem;
    }

    @Override
    @Transactional
    public Order create(OrderRequest orderRequest) {
        if (orderRequest.getOrderItems() == null || orderRequest.getOrderItems().isEmpty()) {
            throw new InvalidRequestException("Order items cannot be empty");
        }

        double totalAmount = 0;

        List<OrderItems> orderItems = new ArrayList<>();

        // Validate stock and update inventory
        for (OrderItemRequest item : orderRequest.getOrderItems()) {
            Product product = productService.getById(item.getProductId());

            
           OrderItems orderItem = inventoryUpdate(item.getProductId(),item.getQuantity());
           orderItems.add(orderItem);

            // Calculate total
            totalAmount += product.getPrice().doubleValue() * item.getQuantity();
        }

        // Create order
        Order order = new Order(orderRequest.getUserId(),orderItems,totalAmount,orderRequest.getShippingAddress(),OrderStatus.PENDING);
        order.setOrderItems(orderItems);
        return orderRepository.save(order);
    }
}
