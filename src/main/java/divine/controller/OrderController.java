package divine.controller;

import divine.dto.OrderDTO;
import divine.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Create an order for the authenticated user.
     */
    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(Authentication authentication) {
        divine.model.User user = (divine.model.User) authentication.getPrincipal();
        int userId = user.getId();
        OrderDTO createdOrder = orderService.createOrder(userId);
        return ResponseEntity.ok(createdOrder);
    }

    /**
     * Retrieve an order by its ID (requires authentication).
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Integer id, Authentication authentication) {
        divine.model.User user = (divine.model.User) authentication.getPrincipal();
        int userId = user.getId();
        OrderDTO order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    /**
     * Retrieve all orders for the authenticated user.
     */
    @GetMapping
    public ResponseEntity<List<OrderDTO>> getAllOrders(Authentication authentication) {
        divine.model.User user = (divine.model.User) authentication.getPrincipal();
        int userId = user.getId();
        List<OrderDTO> orders = orderService.getAllOrders(userId);
        return ResponseEntity.ok(orders);
    }

    /**
     * Delete an order by its ID (requires authentication).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Integer id, Authentication authentication) {
        divine.model.User user = (divine.model.User) authentication.getPrincipal();
        int userId = user.getId();
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}