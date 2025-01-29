package divine.service;

import divine.dto.WishDTO;
import divine.dto.WishItemDTO;
import divine.dto.WishDTO;
import divine.dto.WishItemDTO;
import divine.model.*;
import divine.repository.ProductRepository;
import divine.repository.WishRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
public class WishService {

    private final WishRepository wishRepository;
    private final ProductRepository productRepository;

    @Autowired
    public WishService(WishRepository wishRepository, ProductRepository productRepository) {
        this.wishRepository = wishRepository;
        this.productRepository = productRepository;
    }

    public WishDTO getWishByUserId(Integer userId) {
        Wish wish = wishRepository.findById(userId.longValue())
                .orElseThrow(() -> new RuntimeException("Wish not found for user id: " + userId));
        return convertToDTO(wish);
    }

    public WishDTO addItemToWish(Integer userId, WishItemDTO wishItemDTO) {
        Wish wish = wishRepository.findById(userId.longValue()).orElseGet(() -> createNewWish(userId));
        if (wish.getWishItems() == null) {
            wish.setWishItems(new ArrayList<>());
        }

        Product product = productRepository.findById(wishItemDTO.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        WishItem wishItem = wish.getWishItems().stream()
                .filter(wi -> wi.getProduct().getId()
                        .equals(wishItemDTO.getProductId())).findFirst()
                .orElseGet(() -> createNewWishItem(wish, product));

        wishRepository.save(wish);
        return convertToDTO(wish);
    }

    public WishDTO updateWishItem(Integer userId, WishItemDTO wishItemDTO) {
        Wish wish = wishRepository.findById(userId.longValue())
                .orElseThrow(() -> new RuntimeException("Wish not found for user id: " + userId));

        WishItem wishItem = wish.getWishItems().stream()
                .filter(item -> item.getId().equals(wishItemDTO.getId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Wish item not found"));

        wishRepository.save(wish);
        return convertToDTO(wish);
    }

    public void removeItemFromWish(Integer userId, Integer wishItemId) {
        Wish wish = wishRepository.findById(userId.longValue())
                .orElseThrow(() -> new RuntimeException("Wish not found for user id: " + userId));

        wish.getWishItems().removeIf(item -> item.getId().equals(wishItemId));
        wishRepository.save(wish);
    }

    public void clearWish(Integer userId) {
        Wish wish = wishRepository.findById(userId.longValue())
                .orElseThrow(() -> new RuntimeException("Wish not found for user id: " + userId));

        wish.getWishItems().clear();
        wishRepository.save(wish);
    }

    private Wish createNewWish(Integer userId) {
        Wish wish = new Wish();
        User user = new User();
        user.setId(userId);
        wish.setUser(user);
        return wish;
    }


    private WishItem createNewWishItem(Wish wish, Product product) {
        WishItem wishItem = new WishItem();
        wishItem.setWish(wish);
        wishItem.setProduct(product);
        wish.getWishItems().add(wishItem);
        return wishItem;
    }

    private WishDTO convertToDTO(Wish wish) {
        WishDTO dto = new WishDTO();
        dto.setId(wish.getId());
        dto.setUserId(wish.getUser().getId());
        dto.setWishItems(wish.getWishItems().stream()
                .map(this::convertToWishItemDTO)
                .collect(Collectors.toList()));
        return dto;
    }

    private WishItemDTO convertToWishItemDTO(WishItem wishItem) {
        WishItemDTO dto = new WishItemDTO();
        dto.setId(wishItem.getId());
        dto.setProductId(wishItem.getProduct().getId());
        return dto;
    }
}
