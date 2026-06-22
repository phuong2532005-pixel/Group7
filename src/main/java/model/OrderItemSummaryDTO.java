/**
 *
 * Author : Nguyen Trung Hieu & Nguyen Thi Phuong Linh
 * Date : 15/03/2026
 * Description : DTO tom tat thong tin mot muc trong don hang (order item) de hien thi.
 */
package model;

public class OrderItemSummaryDTO {
    private int orderId;
    private int variantId;
    private int productId;
    private String productName;
    private String imageUrl;
    private String size;
    private String color;
    private int quantity;
    private long priceAtPurchase;
    private long basePrice;

    public OrderItemSummaryDTO(int orderId, int variantId, int productId, String productName, String imageUrl,
            String size, String color, int quantity, long priceAtPurchase, long basePrice) {
        this.orderId = orderId;
        this.variantId = variantId;
        this.productId = productId;
        this.productName = productName;
        this.imageUrl = imageUrl;
        this.size = size;
        this.color = color;
        this.quantity = quantity;
        this.priceAtPurchase = priceAtPurchase;
        this.basePrice = basePrice;
    }

    public int getOrderId() {
        return orderId;
    }

    public int getVariantId() {
        return variantId;
    }

    public int getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getSize() {
        return size;
    }

    public String getColor() {
        return color;
    }

    public int getQuantity() {
        return quantity;
    }

    public long getPriceAtPurchase() {
        return priceAtPurchase;
    }

    public long getBasePrice() {
        return basePrice;
    }
}
