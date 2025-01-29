package divine.dto;

import java.util.List;

public class WishDTO {
    private Integer id;
    private Integer userId;
    private List<WishItemDTO> wishItems;

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

    public List<WishItemDTO> getWishItems() {
        return wishItems;
    }

    public void setWishItems(List<WishItemDTO> wishItems) {
        this.wishItems = wishItems;
    }
}
