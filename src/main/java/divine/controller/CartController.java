package divine.controller;

import divine.model.User;
import divine.dto.CartDTO;
import divine.dto.CartItemDTO;
import divine.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // Get the cart for the authenticated user
    @GetMapping
    public ResponseEntity<CartDTO> getCart(Authentication authentication) {
        divine.model.User user = (divine.model.User) authentication.getPrincipal();
        int userId = user.getId();
        CartDTO cart = cartService.getCartByUserId(userId);
        return ResponseEntity.ok(cart);
    }


    // Add an item to the cart for the authenticated user
    @PostMapping("/items")
    public ResponseEntity<CartDTO> addItemToCart(Authentication authentication, @RequestBody CartItemDTO cartItemDTO) {
        divine.model.User user = (divine.model.User) authentication.getPrincipal();
        int userId = user.getId();
        CartDTO updatedCart = cartService.addItemToCart(userId, cartItemDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(updatedCart);
    }

    // Update the quantity or details of an existing cart item
    @PutMapping("/items")
    public ResponseEntity<CartDTO> updateCartItem(Authentication authentication, @RequestBody CartItemDTO cartItemDTO) {
        divine.model.User user = (divine.model.User) authentication.getPrincipal();
        int userId = user.getId();
        CartDTO updatedCart = cartService.updateCartItem(userId, cartItemDTO);
        return ResponseEntity.ok(updatedCart);
    }

    // Remove an item from the cart for the authenticated user
    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<Void> removeItemFromCart(Authentication authentication, @PathVariable Integer cartItemId) {
        divine.model.User user = (divine.model.User) authentication.getPrincipal();
        int userId = user.getId();
        cartService.removeItemFromCart(userId, cartItemId);
        return ResponseEntity.noContent().build();
    }

    // Clear all items from the cart for the authenticated user
    @DeleteMapping
    public ResponseEntity<Void> clearCart(Authentication authentication) {
        int userId = Math.toIntExact(Long.parseLong(authentication.getName()));
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }
}
