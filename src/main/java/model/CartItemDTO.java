/**
 *
 * Author : Nguyen Trung Hieu & Nguyen Thi Phuong Linh
 * Date : 12/03/2026
 * Description : DTO cho muc trong gio hang (variantId, quantity, price, subtotal).
 */
package model;

public class CartItemDTO {

    private int variantId;
    private int productId;
    private int sizeId;
    private int colorId;
    private String productName;
    private String color;
    private String size;
    private long price;
    private int quantity;
    private String imageUrl;

    public CartItemDTO(int variantId, String productName, String color, String size,
            long price, int quantity, String imageUrl) {
        this(variantId, 0, 0, 0, productName, color, size, price, quantity, imageUrl);
    }

    public CartItemDTO(int variantId, int productId, int sizeId, int colorId, String productName, String color, String size,
            long price, int quantity, String imageUrl) {
        this.variantId = variantId;
        this.productId = productId;
        this.sizeId = sizeId;
        this.colorId = colorId;
        this.productName = productName;
        this.color = color;
        this.size = size;
        this.price = price;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
    }

    public int getVariantId() {
        return variantId;
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

    public String getProductName() {
        return productName;
    }

    public String getColor() {
        return color;
    }

    public String getSize() {
        return size;
    }

    public long getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public long getSubtotal() {
        return price * quantity;
    }
}
