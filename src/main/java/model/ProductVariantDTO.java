/**
 *
 * Author : Nguyen Trung Hieu & Nguyen Thi Phuong Linh
 * Date : 19/03/2026
 * Description : DTO cho bien the san pham (variant) gom size, color, stock, image.
 */
package model;

public class ProductVariantDTO {

    private int id;
    private int productId;
    private int sizeId;
    private int colorId;
    private String sizeName;
    private String colorName;
    private long price;
    private String imageUrl;

    public ProductVariantDTO(int id, int productId, int sizeId, int colorId, String sizeName,
            String colorName, long price, String imageUrl) {
        this.id = id;
        this.productId = productId;
        this.sizeId = sizeId;
        this.colorId = colorId;
        this.sizeName = sizeName;
        this.colorName = colorName;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public int getId() {
        return id;
    }

    public int getProductId() {
        return productId;
    }

    public int getSizeId() {
        return sizeId;
    }

    public int getColorId() {
        return colorId;
    }

    public String getSizeName() {
        return sizeName;
    }

    public String getColorName() {
        return colorName;
    }

    public long getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
