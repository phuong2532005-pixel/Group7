/**
 *
 * Author : Nguyen Trung Hieu & Nguyen Thi Phuong Linh
 * Date : 16/03/2026
 * Description : DTO tra ve ket qua khi thuc hien dat hang (success, message, orderId).
 */
package model;

public class OrderResult {

    private boolean success;
    private String message;
    private int orderId;

    public OrderResult(boolean success, String message, int orderId) {
        this.success = success;
        this.message = message;
        this.orderId = orderId;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public int getOrderId() {
        return orderId;
    }
}
