/**
 *
 * Author : Nguyen Trung Hieu & Nguyen Thi Phuong Linh
 * Date : 24/03/2026
 * Description : DTO cho bao cao cua hang (doanh thu, so don, cac thong so khac).
 */
package model;

public class ShopReportDTO {
    private int shopId;
    private String branchName;
    private int totalOrders;

    public ShopReportDTO(int shopId, String branchName, int totalOrders) {
        this.shopId = shopId;
        this.branchName = branchName;
        this.totalOrders = totalOrders;
    }

    public int getShopId() {
        return shopId;
    }

    public String getBranchName() {
        return branchName;
    }

    public int getTotalOrders() {
        return totalOrders;
    }
}
