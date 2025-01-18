package divine.controller;

import divine.dto.CartDTO;
import divine.dto.CartItemDTO;
import divine.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // Get the cart for a specific user
    @GetMapping("/{userId}")
    public ResponseEntity<CartDTO> getCartByUserId(@PathVariable Integer userId) {
        CartDTO cart = cartService.getCartByUserId(userId);
        return ResponseEntity.ok(cart);
    }

    // Add an item to the cart
    @PostMapping("/{userId}/items")
    public ResponseEntity<CartDTO> addItemToCart(@PathVariable Integer userId, @RequestBody CartItemDTO cartItemDTO) {
        CartDTO updatedCart = cartService.addItemToCart(userId, cartItemDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(updatedCart);
    }

    // Update the quantity or details of an existing cart item
    @PutMapping("/{userId}/items")
    public ResponseEntity<CartDTO> updateCartItem(@PathVariable Integer userId, @RequestBody CartItemDTO cartItemDTO) {
        CartDTO updatedCart = cartService.updateCartItem(userId, cartItemDTO);
        return ResponseEntity.ok(updatedCart);
    }

    // Remove an item from the cart
    @DeleteMapping("/{userId}/items/{cartItemId}")
    public ResponseEntity<Void> removeItemFromCart(@PathVariable Integer userId, @PathVariable Integer cartItemId) {
        cartService.removeItemFromCart(userId, cartItemId);
        return ResponseEntity.noContent().build();
    }

    // Clear all items from the cart
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> clearCart(@PathVariable Integer userId) {
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }
}
