/**
 *
 * Author : Nguyen Trung Hieu & Nguyen Thi Phuong Linh
 * Date : 25/03/2026
 * Description : DTO thong tin ton kho cua tung bien the trong cua hang.
 */
package model;

public class ShopVariantStockDTO {
    private int variantId;
    private int productId;
    private String commonProductName;
    private String productName;
    private String variantImageUrl;
    private String sizeName;
    private String colorName;
    private long price;
    private int currentStock;

    public ShopVariantStockDTO(int variantId, int productId, String commonProductName, String productName,
            String variantImageUrl, String sizeName, String colorName, long price, int currentStock) {
        this.variantId = variantId;
        this.productId = productId;
        this.commonProductName = commonProductName;
        this.productName = productName;
        this.variantImageUrl = variantImageUrl;
        this.sizeName = sizeName;
        this.colorName = colorName;
        this.price = price;
        this.currentStock = currentStock;
    }

    public int getVariantId() {
        return variantId;
    }

    public int getProductId() {
        return productId;
    }

    public String getCommonProductName() {
        return commonProductName;
    }

    public String getProductName() {
        return productName;
    }

    public String getVariantImageUrl() {
        return variantImageUrl;
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

    public int getCurrentStock() {
        return currentStock;
    }
}
