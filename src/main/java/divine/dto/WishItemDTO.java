package divine.dto;

import java.math.BigDecimal;

public class WishItemDTO {
    private Integer id;
    //    private Integer wishId;
    private Integer productId;
//    private BigDecimal price;

    // Getters and setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }
}
