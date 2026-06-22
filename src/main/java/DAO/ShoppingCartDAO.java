/**
 *
 * Author : Nguyen Trung Hieu & Nguyen Thi Phuong Linh
 * Date : 17/03/2026
 * Description : Quan ly gio hang luu tren DB (them, cap nhat so luong, xoa, lay thong tin).
 */
package DAO;

import model.*;
import dal.DBContext;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ShoppingCartDAO extends DBContext {

    /**
     * Returns existing cart id for user, or creates one.
     */
    public int getOrCreateCartId(int userId) {
    /* lay hoac tao id gio hang cho nguoi dung */
        Integer existing = findCartIdByUserId(userId);
        if (existing != null) {
            return existing;
        }

        String insert = "INSERT INTO ShoppingCarts (user_id, created_at) VALUES (?, GETDATE())";
        try {
            PreparedStatement ps = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, userId);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.out.println(e);
        }

        existing = findCartIdByUserId(userId);
        return existing == null ? -1 : existing;
    }

    public List<CartItemDTO> getCartItems(int userId) {
    /* lay tat ca item trong gio hang cua nguoi dung */
        List<CartItemDTO> items = new ArrayList<>();
        String sql = "SELECT ci.variant_id, p.id AS product_id, v.size_id, v.color_id, p.name AS product_name, c.color_name, s.size_name, v.price, ci.quantity, "
                + "COALESCE(NULLIF(LTRIM(RTRIM(v.image_url)), ''), p.thumbnail) AS image_url "
                + "FROM ShoppingCarts sc "
                + "JOIN CartItems ci ON ci.cart_id = sc.id "
                + "JOIN ProductVariants v ON v.id = ci.variant_id "
                + "JOIN Sizes s ON s.id = v.size_id "
                + "JOIN Colors c ON c.id = v.color_id "
                + "JOIN Products p ON p.id = v.product_id "
                + "WHERE sc.user_id = ? "
                + "ORDER BY p.name, ci.id";        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                items.add(new CartItemDTO(
                        rs.getInt("variant_id"),
                        rs.getInt("product_id"),
                    rs.getInt("size_id"),
                    rs.getInt("color_id"),
                        rs.getString("product_name"),
                        rs.getString("color_name"),
                        rs.getString("size_name"),
                        rs.getLong("price"),
                        rs.getInt("quantity"),
                        rs.getString("image_url")
                ));
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return items;
    }

    public boolean changeItemVariant(int userId, int oldVariantId, int newVariantId) {
    /* doi variant cho mot dong trong gio hang (hop nhat so luong neu can) */
        if (oldVariantId == newVariantId) {
            return true;
        }

        Integer cartId = findCartIdByUserId(userId);
        if (cartId == null) {
            return false;
        }

        String currentQtySql = "SELECT quantity FROM CartItems WHERE cart_id = ? AND variant_id = ?";
        String targetQtySql = "SELECT quantity FROM CartItems WHERE cart_id = ? AND variant_id = ?";
        String updateTargetSql = "UPDATE CartItems SET quantity = quantity + ? WHERE cart_id = ? AND variant_id = ?";
        String updateRowSql = "UPDATE CartItems SET variant_id = ? WHERE cart_id = ? AND variant_id = ?";
        String deleteOldSql = "DELETE FROM CartItems WHERE cart_id = ? AND variant_id = ?";

        try {
            connection.setAutoCommit(false);

            int sourceQty = 0;
            try (PreparedStatement ps = connection.prepareStatement(currentQtySql)) {
                ps.setInt(1, cartId);
                ps.setInt(2, oldVariantId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    sourceQty = rs.getInt("quantity");
                }
            }

            if (sourceQty <= 0) {
                connection.rollback();
                return false;
            }

            boolean targetExists = false;
            try (PreparedStatement ps = connection.prepareStatement(targetQtySql)) {
                ps.setInt(1, cartId);
                ps.setInt(2, newVariantId);
                ResultSet rs = ps.executeQuery();
                targetExists = rs.next();
            }

            if (targetExists) {
                try (PreparedStatement ps = connection.prepareStatement(updateTargetSql)) {
                    ps.setInt(1, sourceQty);
                    ps.setInt(2, cartId);
                    ps.setInt(3, newVariantId);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = connection.prepareStatement(deleteOldSql)) {
                    ps.setInt(1, cartId);
                    ps.setInt(2, oldVariantId);
                    ps.executeUpdate();
                }
            } else {
                try (PreparedStatement ps = connection.prepareStatement(updateRowSql)) {
                    ps.setInt(1, newVariantId);
                    ps.setInt(2, cartId);
                    ps.setInt(3, oldVariantId);
                    ps.executeUpdate();
                }
            }

            connection.commit();
            return true;
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                System.out.println(ex);
            }
            System.out.println(e);
            return false;
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                System.out.println(e);
            }
        }
    }

    public int getCartQuantitySum(int userId) {
    /* tinh tong so luong item trong gio hang cua nguoi dung */
        String sql = "SELECT ISNULL(SUM(ci.quantity), 0) AS qty "
                + "FROM ShoppingCarts sc "
                + "LEFT JOIN CartItems ci ON ci.cart_id = sc.id "
                + "WHERE sc.user_id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("qty");
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return 0;
    }

    public boolean addItem(int userId, int variantId, int quantity) {
    /* them mot item vao gio hang hoac cong so luong neu da ton tai */
        if (quantity <= 0) {
            quantity = 1;
        }
        int cartId = getOrCreateCartId(userId);
        if (cartId <= 0) {
            return false;
        }

        try {
            // First try to update existing item
            String update = "UPDATE CartItems SET quantity = quantity + ? WHERE cart_id = ? AND variant_id = ?";
            PreparedStatement psUpdate = connection.prepareStatement(update);
            psUpdate.setInt(1, quantity);
            psUpdate.setInt(2, cartId);
            psUpdate.setInt(3, variantId);
            int updated = psUpdate.executeUpdate();
            psUpdate.close();

            // If not updated, insert new item
            if (updated == 0) {
                String insert = "INSERT INTO CartItems (cart_id, variant_id, quantity) VALUES (?, ?, ?)";
                PreparedStatement psInsert = connection.prepareStatement(insert);
                psInsert.setInt(1, cartId);
                psInsert.setInt(2, variantId);
                psInsert.setInt(3, quantity);
                psInsert.executeUpdate();
                psInsert.close();
            }

            return true;
        } catch (SQLException e) {
            System.out.println("Error adding item to cart: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean setItemQuantity(int userId, int variantId, int quantity) {
    /* dat so luong cho mot item trong gio hang (neu 0 se xoa) */
        int cartId = getOrCreateCartId(userId);
        if (cartId <= 0) {
            return false;
        }
        if (quantity <= 0) {
            return removeItem(userId, variantId);
        }
        String sql = "UPDATE CartItems SET quantity = ? WHERE cart_id = ? AND variant_id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, quantity);
            ps.setInt(2, cartId);
            ps.setInt(3, variantId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println(e);
        }
        return false;
    }

    public boolean removeItem(int userId, int variantId) {
    /* xoa mot item khoi gio hang */
        Integer cartId = findCartIdByUserId(userId);
        if (cartId == null) {
            return true;
        }
        String sql = "DELETE FROM CartItems WHERE cart_id = ? AND variant_id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, cartId);
            ps.setInt(2, variantId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println(e);
        }
        return false;
    }

    public boolean removeItems(int userId, Collection<Integer> variantIds) {
    /* xoa nhieu item trong gio hang theo danh sach variantId */
        if (variantIds == null || variantIds.isEmpty()) {
            return true;
        }

        Integer cartId = findCartIdByUserId(userId);
        if (cartId == null) {
            return true;
        }

        StringBuilder sql = new StringBuilder("DELETE FROM CartItems WHERE cart_id = ? AND variant_id IN (");
        int size = 0;
        for (Integer ignored : variantIds) {
            if (size > 0) {
                sql.append(',');
            }
            sql.append('?');
            size++;
        }
        sql.append(')');

        try {
            PreparedStatement ps = connection.prepareStatement(sql.toString());
            ps.setInt(1, cartId);
            int idx = 2;
            for (Integer variantId : variantIds) {
                ps.setInt(idx++, variantId);
            }
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println(e);
        }
        return false;
    }

    public boolean clearCart(int userId) {
    /* xoa tat ca item trong gio hang cua nguoi dung */
        Integer cartId = findCartIdByUserId(userId);
        if (cartId == null) {
            return true;
        }
        String sql = "DELETE FROM CartItems WHERE cart_id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, cartId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println(e);
        }
        return false;
    }

    private Integer findCartIdByUserId(int userId) {
    /* tim id gio hang gan nhat cua nguoi dung neu co */
        String select = "SELECT TOP 1 id FROM ShoppingCarts WHERE user_id = ? ORDER BY id DESC";
        try {
            PreparedStatement ps = connection.prepareStatement(select);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return null;
    }
}

