/**
 *
 * Author : Nguyen Trung Hieu & Nguyen Thi Phuong Linh
 * Date : 17/03/2026
 * Description : DTO tom tat thong tin don hang (order) de hien thi tren danh sach.
 */
package model;

import java.sql.Timestamp;

public class OrderSummaryDTO {
    private int id;
    private int userId;
    private int shopId;
    private String shopBranchName;
    private String shippingName;
    private String shippingPhone;
    private String shippingStreet;
    private String shippingDistrict;
    private String shippingCity;
    private Timestamp orderDate;
    private long totalAmount;
    private String paymentMethod;
    private String shippingStatus;

    public OrderSummaryDTO(int id, int userId, int shopId, String shippingName,
            String shippingPhone, String shippingStreet, String shippingDistrict, String shippingCity, Timestamp orderDate,
            long totalAmount, String paymentMethod, String shippingStatus) {
        this.id = id;
        this.userId = userId;
        this.shopId = shopId;
        this.shopBranchName = null;
        this.shippingName = shippingName;
        this.shippingPhone = shippingPhone;
        this.shippingStreet = shippingStreet;
        this.shippingDistrict = shippingDistrict;
        this.shippingCity = shippingCity;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
        this.shippingStatus = shippingStatus;
    }

    public OrderSummaryDTO(int id, int userId, int shopId, String shopBranchName,
            String shippingName, String shippingPhone, String shippingStreet, String shippingDistrict, String shippingCity, Timestamp orderDate,
            long totalAmount, String paymentMethod, String shippingStatus) {
        this.id = id;
        this.userId = userId;
        this.shopId = shopId;
        this.shopBranchName = shopBranchName;
        this.shippingName = shippingName;
        this.shippingPhone = shippingPhone;
        this.shippingStreet = shippingStreet;
        this.shippingDistrict = shippingDistrict;
        this.shippingCity = shippingCity;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
        this.shippingStatus = shippingStatus;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public int getShopId() {
        return shopId;
    }

    public String getShopBranchName() {
        return shopBranchName;
    }

    public String getShippingName() {
        return shippingName;
    }

    public String getShippingPhone() {
        return shippingPhone;
    }

    public String getShippingStreet() {
        return shippingStreet;
    }

    public String getShippingDistrict() {
        return shippingDistrict;
    }

    public String getShippingCity() {
        return shippingCity;
    }

    public Timestamp getOrderDate() {
        return orderDate;
    }

    public long getTotalAmount() {
        return totalAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getShippingStatus() {
        return shippingStatus;
    }
}
