package divine.controller;

import divine.model.User;
import divine.dto.WishDTO;
import divine.dto.WishItemDTO;
import divine.service.WishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/wish")
public class WishController {

    private final WishService wishService;

    @Autowired
    public WishController(WishService wishService) {
        this.wishService = wishService;
    }

    // Get the wish for the authenticated user
    @GetMapping
    public ResponseEntity<WishDTO> getWish(Authentication authentication) {
        divine.model.User user = (divine.model.User) authentication.getPrincipal();
        int userId = user.getId();
        WishDTO wish = wishService.getWishByUserId(userId);
        return ResponseEntity.ok(wish);
    }


    // Add an item to the wish for the authenticated user
    @PostMapping("/items")
    public ResponseEntity<WishDTO> addItemToWish(Authentication authentication, @RequestBody WishItemDTO wishItemDTO) {
        divine.model.User user = (divine.model.User) authentication.getPrincipal();
        int userId = user.getId();
        WishDTO updatedWish = wishService.addItemToWish(userId, wishItemDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(updatedWish);
    }

    // Update the quantity or details of an existing wish item
    @PutMapping("/items")
    public ResponseEntity<WishDTO> updateWishItem(Authentication authentication, @RequestBody WishItemDTO wishItemDTO) {
        divine.model.User user = (divine.model.User) authentication.getPrincipal();
        int userId = user.getId();
        WishDTO updatedWish = wishService.updateWishItem(userId, wishItemDTO);
        return ResponseEntity.ok(updatedWish);
    }

    // Remove an item from the wish for the authenticated user
    @DeleteMapping("/items/{wishItemId}")
    public ResponseEntity<Void> removeItemFromWish(Authentication authentication, @PathVariable Integer wishItemId) {
        divine.model.User user = (divine.model.User) authentication.getPrincipal();
        int userId = user.getId();
        wishService.removeItemFromWish(userId, wishItemId);
        return ResponseEntity.noContent().build();
    }

    // Clear all items from the wish for the authenticated user
    @DeleteMapping
    public ResponseEntity<Void> clearWish(Authentication authentication) {
        int userId = Math.toIntExact(Long.parseLong(authentication.getName()));
        wishService.clearWish(userId);
        return ResponseEntity.noContent().build();
    }
}
