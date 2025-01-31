package divine.service;

import divine.dto.OrderDTO;
import divine.dto.OrderItemDTO;
import divine.model.*;
import divine.repository.CartRepository;
import divine.repository.OrderRepository;
import divine.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository, CartRepository cartRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
    }

    /**
     * Creates an order for the given user by moving cart items to an order.
     */
    public OrderDTO createOrder(Integer userId) {
        Cart cart = cartRepository.findById(userId.longValue())
                .orElseThrow(() -> new RuntimeException("Cart not found for user ID: " + userId));

        if (cart.getCartItems().isEmpty()) {
            throw new RuntimeException("Cannot place an order with an empty cart.");
        }

        Order order = new Order();
        User user = new User();
        user.setId(userId);
        order.setUser(user);
        order.setStatus("PENDING");
        order.setOrderDate(Instant.now());

        List<OrderItem> orderItems = cart.getCartItems().stream().map(cartItem -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getPrice());
            return orderItem;
        }).collect(Collectors.toList());

        order.setOrderItems(orderItems);
        orderRepository.save(order);

        // Clear cart after placing order
        cart.getCartItems().clear();
        cartRepository.save(cart);

        return convertToDTO(order);
    }

    /**
     * Retrieves a specific order by ID.
     */
    public OrderDTO getOrderById(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found for ID: " + orderId));
        return convertToDTO(order);
    }

    /**
     * Retrieves all orders for a given user.
     */
    public List<OrderDTO> getAllOrders(Integer userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    /**
     * Deletes an order.
     */
    public void deleteOrder(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found for ID: " + orderId));
        orderRepository.delete(order);
    }

    /**
     * Converts Order entity to DTO.
     */
    private OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setStatus(order.getStatus());
        dto.setOrderDate(order.getOrderDate());
        dto.setUserId(order.getUser().getId());
        dto.setOrderItems(order.getOrderItems().stream()
                .map(this::convertToOrderItemDTO)
                .collect(Collectors.toList()));
        return dto;
    }

    /**
     * Converts OrderItem entity to DTO.
     */
    private OrderItemDTO convertToOrderItemDTO(OrderItem orderItem) {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setId(orderItem.getId());
        dto.setOrderId(orderItem.getOrder().getId());
        dto.setProductId(orderItem.getProduct().getId());
        dto.setQuantity(orderItem.getQuantity());
        dto.setPrice(orderItem.getPrice());
        return dto;
    }
}
