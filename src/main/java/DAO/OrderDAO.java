/**
 *
 * Author : Nguyen Trung Hieu & Nguyen Thi Phuong Linh
 * Date : 14/03/2026
 * Description : Xu ly tao don hang, chi tiet don hang va cap nhat ton kho.
 */
package DAO;

import model.*;
import dal.DBContext;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.ArrayList;
import model.OrderSummaryDTO;

public class OrderDAO extends DBContext {

    public OrderResult placeOrder(int userId, String shippingName,
            String shippingPhone, String street, String district, String city,
            String paymentMethod, List<CartItemDTO> items) {
    /* thuc hien dat hang: kiem tra ton kho, tao order va chi tiet, cap nhat tong tien */
        if (items == null || items.isEmpty()) {
            return new OrderResult(false, "Gio hang trong.", -1);
        }

        try {
            connection.setAutoCommit(false);

            for (CartItemDTO item : items) {
                if (!hasStock(item.getVariantId(), item.getQuantity())) {
                    connection.rollback();
                    return new OrderResult(false,
                            "Khong du ton kho cho san pham " + item.getProductName() + ".",
                            -1);
                }
            }

                int orderId = insertOrder(userId, shippingName, shippingPhone,
                    street, district, city, paymentMethod);
            if (orderId <= 0) {
                connection.rollback();
                return new OrderResult(false, "Khong the tao don hang.", -1);
            }

            for (CartItemDTO item : items) {
                boolean inserted = insertOrderItem(orderId, item.getVariantId(),
                        item.getQuantity(), item.getPrice());
                if (!inserted) {
                    connection.rollback();
                    return new OrderResult(false, "Khong the tao chi tiet don hang.", -1);
                }
                // Trừ kho
                boolean stockUpdated = updateStock(item.getVariantId(), item.getQuantity());
                if (!stockUpdated) {
                    connection.rollback();
                    return new OrderResult(false, "Lỗi khi trừ kho.", -1);
                }
            }

            long totalAmount = calculateTotal(items);
            if (!updateOrderTotal(orderId, totalAmount)) {
                connection.rollback();
                return new OrderResult(false, "Khong the cap nhat tong tien don hang.", -1);
            }
            // Ghi log
            SystemLogDAO logDAO = new SystemLogDAO();
            logDAO.insertLog(userId, "New Order #" + orderId + " created");

            connection.commit();
            return new OrderResult(true, "OK", orderId);
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                System.out.println(ex);
            }
            System.out.println(e);
            return new OrderResult(false, "Loi he thong khi dat hang.", -1);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                System.out.println(e);
            }
        }
    }

    // Trừ kho sau khi đặt hàng thành công từ ProductVariants.stock
    private boolean updateStock(int variantId, int quantity) throws SQLException {
    /* tru so luong ton kho cho mot variant */
        String sql = "UPDATE ProductVariants SET stock = stock - ? WHERE id = ? AND stock >= ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, quantity);
            ps.setInt(2, variantId);
            ps.setInt(3, quantity);
            return ps.executeUpdate() > 0;
        }
    }

    private boolean hasStock(int variantId, int quantity) throws SQLException {
    /* kiem tra so luong ton kho cua variant >= yeu cau */
        String sql = "SELECT stock FROM ProductVariants WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, variantId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("stock") >= quantity;
            }
        }
        return false;
    }

    private int insertOrder(int userId, String shippingName, String shippingPhone,
            String street, String district, String city, String paymentMethod) throws SQLException {
    /* chen ban ghi order va tra ve orderId duoc sinh */
        String sql = "INSERT INTO Orders (user_id, shipping_name, shipping_phone, "
                + "shipping_street, shipping_district, shipping_city, total_amount, payment_method, shipping_status) "
                + "VALUES (?, ?, ?, ?, ?, ?, 0, ?, 'PENDING')";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, userId);
            ps.setString(2, shippingName);
            ps.setString(3, shippingPhone);
            ps.setString(4, street);
            ps.setString(5, district);
            ps.setString(6, city);
            ps.setString(7, paymentMethod);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    private boolean insertOrderItem(int orderId, int variantId, int quantity, long price) throws SQLException {
    /* chen chi tiet don hang cho order */
        String sql = "INSERT INTO OrderItems (order_id, variant_id, quantity, price_at_purchase) "
                + "VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.setInt(2, variantId);
            ps.setInt(3, quantity);
            ps.setLong(4, price);
            return ps.executeUpdate() > 0;
        }
    }

    private boolean updateOrderTotal(int orderId, long totalAmount) throws SQLException {
    /* cap nhat tong tien cho order */
        String sql = "UPDATE Orders SET total_amount = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, totalAmount);
            ps.setInt(2, orderId);
            return ps.executeUpdate() > 0;
        }
    }

    private long calculateTotal(List<CartItemDTO> items) {
    /* tinh tong tien tam tinh tu danh sach item */
        long total = 0;
        for (CartItemDTO item : items) {
            total += item.getPrice() * item.getQuantity();
        }
        return total;
    }

    /**
     * List shop orders with optional status filter and sort order.
     *
     * @param shopId shop id
     * @param status optional shipping status to filter (exact match)
     * @param sortOrder "asc" or "desc" (by order_date) or null for default
     * @param offset pagination offset
     * @param limit pagination size
     */
    public List<OrderSummaryDTO> listOrdersByShop(int shopId, String status, String sortOrder, int offset, int limit) {
        return listOrdersForDirector(status, sortOrder, offset, limit);
    }

    // keep original signature for backwards compatibility
    public List<OrderSummaryDTO> listOrdersByShop(int shopId, int offset, int limit) {
        return listOrdersByShop(shopId, null, null, offset, limit);
    }

    public int countOrdersByShop(int shopId) {
        return countOrdersByShop(shopId, null);
    }

    public int countOrdersByShop(int shopId, String status) {
        return countOrdersForDirector(status);
    }

    public List<OrderSummaryDTO> listOrdersByUser(int userId, int offset, int limit) {
        return listOrdersByUser(userId, null, offset, limit);
    }

    public List<OrderSummaryDTO> listOrdersForDirector(String status, String sortOrder, int offset, int limit) {
    /* lay danh sach don hang cho admin/manager voi loc trang thai va phan trang */
        List<OrderSummaryDTO> orders = new ArrayList<>();
        String normalizedStatus = status == null ? null : status.trim().toUpperCase();
        boolean hasExplicitStatus = normalizedStatus != null && !normalizedStatus.isEmpty();
        StringBuilder sql = new StringBuilder("SELECT o.id, o.user_id, 0 AS shop_id, "
            + "CAST(NULL AS NVARCHAR(100)) AS shop_branch_name, "
                + "o.shipping_name, o.shipping_phone, o.shipping_street, o.shipping_district, o.shipping_city, "
                + "o.order_date, o.total_amount, o.payment_method, o.shipping_status "
            + "FROM Orders o WHERE 1 = 1 ");
        if (hasExplicitStatus) {
            sql.append("AND o.shipping_status = ? ");
        } else {
            sql.append("AND UPPER(o.shipping_status) IN ('PENDING', 'PROCESSING') ");
        }
        sql.append("ORDER BY CASE WHEN UPPER(o.shipping_status) IN ('PENDING', 'PROCESSING') THEN 0 ELSE 1 END, ");
        if ("asc".equalsIgnoreCase(sortOrder)) {
            sql.append("o.order_date ASC, o.id ASC ");
        } else {
            sql.append("o.order_date DESC, o.id DESC ");
        }
        sql.append("OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            int idx = 1;
            if (hasExplicitStatus) {
                ps.setString(idx++, normalizedStatus);
            }
            ps.setInt(idx++, offset);
            ps.setInt(idx++, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                orders.add(new OrderSummaryDTO(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getInt("shop_id"),
                        rs.getString("shop_branch_name"),
                        rs.getString("shipping_name"),
                        rs.getString("shipping_phone"),
                        rs.getString("shipping_street"),
                        rs.getString("shipping_district"),
                        rs.getString("shipping_city"),
                        rs.getTimestamp("order_date"),
                        rs.getLong("total_amount"),
                        rs.getString("payment_method"),
                        rs.getString("shipping_status")
                ));
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return orders;
    }

    public int countOrdersForDirector(String status) {
    /* dem so don hang phu hop voi trang thai (danh cho admin) */
        String normalizedStatus = status == null ? null : status.trim().toUpperCase();
        boolean hasExplicitStatus = normalizedStatus != null && !normalizedStatus.isEmpty();
        StringBuilder sql = new StringBuilder("SELECT COUNT(1) AS total FROM Orders WHERE 1 = 1 ");
        if (hasExplicitStatus) {
            sql.append("AND shipping_status = ?");
        } else {
            sql.append("AND UPPER(shipping_status) IN ('PENDING', 'PROCESSING')");
        }
        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            if (hasExplicitStatus) {
                ps.setString(1, normalizedStatus);
            }
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return 0;
    }

    public List<OrderSummaryDTO> listOrdersByUser(int userId, String status, int offset, int limit) {
    /* lay danh sach don hang cua mot nguoi dung voi phan trang */
        List<OrderSummaryDTO> orders = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT o.id, o.user_id, 0 AS shop_id, "
            + "CAST(NULL AS NVARCHAR(100)) AS shop_branch_name, "
                + "o.shipping_name, o.shipping_phone, o.shipping_street, o.shipping_district, o.shipping_city, "
                + "o.order_date, o.total_amount, o.payment_method, o.shipping_status "
            + "FROM Orders o "
                + "WHERE o.user_id = ? ");
        if (status != null && !status.trim().isEmpty()) {
            sql.append("AND o.shipping_status = ? ");
        }
        sql.append("ORDER BY o.order_date DESC, o.id DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            int idx = 1;
            ps.setInt(idx++, userId);
            if (status != null && !status.trim().isEmpty()) {
                ps.setString(idx++, status.trim());
            }
            ps.setInt(idx++, offset);
            ps.setInt(idx++, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                orders.add(new OrderSummaryDTO(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getInt("shop_id"),
                        rs.getString("shop_branch_name"),
                        rs.getString("shipping_name"),
                        rs.getString("shipping_phone"),
                        rs.getString("shipping_street"),
                        rs.getString("shipping_district"),
                        rs.getString("shipping_city"),
                        rs.getTimestamp("order_date"),
                        rs.getLong("total_amount"),
                        rs.getString("payment_method"),
                        rs.getString("shipping_status")
                ));
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return orders;
    }

    public int countOrdersByUser(int userId) {
        return countOrdersByUser(userId, null);
    }

    public int countOrdersByUser(int userId, String status) {
    /* dem so don hang cua nguoi dung voi tuy chon loc theo trang thai */
        StringBuilder sql = new StringBuilder("SELECT COUNT(1) AS total FROM Orders WHERE user_id = ?");
        if (status != null && !status.trim().isEmpty()) {
            sql.append(" AND shipping_status = ?");
        }
        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            ps.setInt(1, userId);
            if (status != null && !status.trim().isEmpty()) {
                ps.setString(2, status.trim());
            }
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return 0;
    }

    public List<OrderItemSummaryDTO> listOrderItemsByOrderId(int orderId) {
    /* lay danh sach chi tiet don hang cho orderId */
        List<OrderItemSummaryDTO> items = new ArrayList<>();
        String sql = "SELECT oi.order_id, oi.variant_id, v.product_id, oi.quantity, oi.price_at_purchase, "
                + "p.name AS product_name, "
                + "COALESCE(NULLIF(p.thumbnail, ''), NULLIF(v.image_url, '')) AS image_url, "
                + "s.size_name AS variant_size, c.color_name AS variant_color, p.base_price "
                + "FROM OrderItems oi "
                + "JOIN ProductVariants v ON oi.variant_id = v.id "
                + "JOIN Products p ON v.product_id = p.id "
                + "LEFT JOIN Sizes s ON s.id = v.size_id "
                + "LEFT JOIN Colors c ON c.id = v.color_id "
                + "WHERE oi.order_id = ? "
                + "ORDER BY oi.id";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                items.add(new OrderItemSummaryDTO(
                        rs.getInt("order_id"),
                        rs.getInt("variant_id"),
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        rs.getString("image_url"),
                        rs.getString("variant_size"),
                        rs.getString("variant_color"),
                        rs.getInt("quantity"),
                        rs.getLong("price_at_purchase"),
                        rs.getLong("base_price")
                ));
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return items;
    }

    /**
     * Atomically update status only if current status matches expected
     * fromStatus. Returns true if updated.
     */
    public boolean updateOrderStatusConditional(int orderId, String fromStatus, String toStatus) {
    /* cap nhat trang thai don neu trang thai hien tai trung voi fromStatus */
        String normalizedFrom = fromStatus == null ? null : fromStatus.trim().toUpperCase();
        String normalizedTo = toStatus == null ? null : toStatus.trim().toUpperCase();
        String sql = "UPDATE Orders SET shipping_status = ? WHERE id = ? AND UPPER(shipping_status) = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, normalizedTo);
            ps.setInt(2, orderId);
            ps.setString(3, normalizedFrom);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println(e);
        }
        return false;
    }

    public String getOrderShippingStatus(int orderId) {
    /* tra ve trang thai giao hang hien tai cua order */
        String sql = "SELECT shipping_status FROM Orders WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String status = rs.getString("shipping_status");
                return status == null ? null : status.trim().toUpperCase();
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return null;
    }

    public Integer getOrderUserId(int orderId) {
    /* tra ve user_id cua order */
        String sql = "SELECT user_id FROM Orders WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("user_id");
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return null;
    }

    public Integer getOrderShopId(int orderId) {
    /* tra ve shop_id cua order (chua implement) */
        return null;
    }

}

