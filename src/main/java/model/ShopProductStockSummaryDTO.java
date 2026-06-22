/**
 *
 * Author : Nguyen Trung Hieu & Nguyen Thi Phuong Linh
 * Date : 23/03/2026
 * Description : DTO tom tat ton kho tung san pham theo cua hang (de hien thi trong admin).
 */
package model;

import java.util.List;

public class ShopProductStockSummaryDTO {
    private int productId;
    private String productName;
    private int totalStock;
    private List<ShopVariantStockDTO> variants;

    public ShopProductStockSummaryDTO(int productId, String productName, int totalStock,
            List<ShopVariantStockDTO> variants) {
        this.productId = productId;
        this.productName = productName;
        this.totalStock = totalStock;
        this.variants = variants;
    }

    public int getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public int getTotalStock() {
        return totalStock;
    }

    public List<ShopVariantStockDTO> getVariants() {
        return variants;
    }
}
