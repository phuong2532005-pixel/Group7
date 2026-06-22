/**
 *
 * Author : Nguyen Trung Hieu & Nguyen Thi Phuong Linh
 * Date : 22/03/2026
 * Description : DTO chua cac chi so/metric cua cua hang de hien thi tren dashboard.
 */
package model;

public class ShopMetricDTO {
    private int shopId;
    private String branchName;
    private double value;

    public ShopMetricDTO(int shopId, String branchName, double value) {
        this.shopId = shopId;
        this.branchName = branchName;
        this.value = value;
    }

    public int getShopId() {
        return shopId;
    }

    public String getBranchName() {
        return branchName;
    }

    public double getValue() {
        return value;
    }
}
