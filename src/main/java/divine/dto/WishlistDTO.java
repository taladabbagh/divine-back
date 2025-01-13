package divine.dto;

import java.util.List;

public class WishlistDTO {
    private Integer id;
    private Integer userId;
    private List<WishlistItemDTO> wishlistItems;

    // Getters and setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public List<WishlistItemDTO> getWishlistItems() {
        return wishlistItems;
    }

    public void setWishlistItems(List<WishlistItemDTO> wishlistItems) {
        this.wishlistItems = wishlistItems;
    }
}
