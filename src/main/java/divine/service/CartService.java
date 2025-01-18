package divine.service;

import divine.dto.CartDTO;
import divine.dto.CartItemDTO;
import divine.model.Cart;
import divine.model.CartItem;
import divine.model.Product;
import divine.model.User;
import divine.repository.CartRepository;
import divine.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
//import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    @Autowired
    public CartService(CartRepository cartRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
    }

    public CartDTO getCartByUserId(Integer userId) {
        Cart cart = cartRepository.findById(userId.longValue())
                .orElseThrow(() -> new RuntimeException("Cart not found for user id: " + userId));
        return convertToDTO(cart);
    }

    public CartDTO addItemToCart(Integer userId, CartItemDTO cartItemDTO) {
        Cart cart = cartRepository.findById(userId.longValue())
                .orElseGet(() -> createNewCart(userId));

        if (cart.getCartItems() == null) {
            cart.setCartItems(new ArrayList<>());
        }

        Product product = productRepository.findById(cartItemDTO.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        CartItem cartItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(cartItemDTO.getProductId()))
                .findFirst()
                .orElseGet(() -> createNewCartItem(cart, product));

        cartItem.setQuantity(cartItem.getQuantity() + cartItemDTO.getQuantity());
        cartItem.setPrice(product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));

        cartRepository.save(cart);
        return convertToDTO(cart);
    }


    public CartDTO updateCartItem(Integer userId, CartItemDTO cartItemDTO) {
        Cart cart = cartRepository.findById(userId.longValue())
                .orElseThrow(() -> new RuntimeException("Cart not found for user id: " + userId));

        CartItem cartItem = cart.getCartItems().stream()
                .filter(item -> item.getId().equals(cartItemDTO.getId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        cartItem.setQuantity(cartItemDTO.getQuantity());
        cartItem.setPrice(cartItem.getProduct().getPrice().multiply(BigDecimal.valueOf(cartItemDTO.getQuantity())));

        cartRepository.save(cart);
        return convertToDTO(cart);
    }

    public void removeItemFromCart(Integer userId, Integer cartItemId) {
        Cart cart = cartRepository.findById(userId.longValue())
                .orElseThrow(() -> new RuntimeException("Cart not found for user id: " + userId));

        cart.getCartItems().removeIf(item -> item.getId().equals(cartItemId));
        cartRepository.save(cart);
    }

    public void clearCart(Integer userId) {
        Cart cart = cartRepository.findById(userId.longValue())
                .orElseThrow(() -> new RuntimeException("Cart not found for user id: " + userId));

        cart.getCartItems().clear();
        cartRepository.save(cart);
    }

    private Cart createNewCart(Integer userId) {
        Cart cart = new Cart();
        User user = new User();
        user.setId(userId);
        cart.setUser(user);
        return cart;
    }


    private CartItem createNewCartItem(Cart cart, Product product) {
        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setQuantity(0); // Initial quantity set by caller
        cartItem.setPrice(BigDecimal.ZERO);
        cart.getCartItems().add(cartItem);
        return cartItem;
    }

    private CartDTO convertToDTO(Cart cart) {
        CartDTO dto = new CartDTO();
        dto.setId(cart.getId());
        dto.setUserId(cart.getUser().getId());
        dto.setCartItems(cart.getCartItems().stream()
                .map(this::convertToCartItemDTO)
                .collect(Collectors.toList()));
        return dto;
    }

    private CartItemDTO convertToCartItemDTO(CartItem cartItem) {
        CartItemDTO dto = new CartItemDTO();
        dto.setId(cartItem.getId());
        dto.setProductId(cartItem.getProduct().getId());
        dto.setQuantity(cartItem.getQuantity());
        dto.setPrice(cartItem.getPrice());
        return dto;
    }
}
