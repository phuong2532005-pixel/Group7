/**
 *
 * Author : Nguyen Trung Hieu & Nguyen Thi Phuong Linh
 * Date : 11/03/2026
 * Description : Xu ly cac thao tac CRUD tren bang dia chi nguoi dung.
 */
package DAO;

import model.*;
import dal.DBContext;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.AddressDTO;

public class AddressDAO extends DBContext {

    public List<AddressDTO> listByUser(int userId) {
    /* lay danh sach dia chi cua nguoi dung theo userId */
        List<AddressDTO> addresses = new ArrayList<>();
        String sql = "SELECT id, user_id, street, district, city, is_default "
                + "FROM Addresses WHERE user_id = ? "
                + "ORDER BY is_default DESC, id DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                addresses.add(new AddressDTO(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("street"),
                        rs.getString("district"),
                        rs.getString("city"),
                        rs.getBoolean("is_default")
                ));
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return addresses;
    }

    public AddressDTO getByIdAndUser(int addressId, int userId) {
    /* lay dia chi theo id va userId neu co */
        String sql = "SELECT id, user_id, street, district, city, is_default "
                + "FROM Addresses WHERE id = ? AND user_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, addressId);
            ps.setInt(2, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new AddressDTO(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("street"),
                        rs.getString("district"),
                        rs.getString("city"),
                        rs.getBoolean("is_default")
                );
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return null;
    }

    public boolean insertAddress(int userId, String street, String district, String city, boolean isDefault) {
    /* them dia chi moi va cap nhat dia chi mac dinh neu can */
        String countSql = "SELECT COUNT(1) AS total FROM Addresses WHERE user_id = ?";
        String clearDefaultSql = "UPDATE Addresses SET is_default = 0 WHERE user_id = ?";
        String insertSql = "INSERT INTO Addresses (user_id, street, district, city, is_default) VALUES (?, ?, ?, ?, ?)";

        try {
            boolean oldAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try {
                int total;
                try (PreparedStatement psCount = connection.prepareStatement(countSql)) {
                    psCount.setInt(1, userId);
                    ResultSet rs = psCount.executeQuery();
                    total = rs.next() ? rs.getInt("total") : 0;
                }

                boolean shouldDefault = isDefault || total == 0;
                if (shouldDefault) {
                    try (PreparedStatement psClear = connection.prepareStatement(clearDefaultSql)) {
                        psClear.setInt(1, userId);
                        psClear.executeUpdate();
                    }
                }

                try (PreparedStatement psInsert = connection.prepareStatement(insertSql)) {
                    psInsert.setInt(1, userId);
                    psInsert.setString(2, street);
                    psInsert.setString(3, district);
                    psInsert.setString(4, city);
                    psInsert.setBoolean(5, shouldDefault);
                    psInsert.executeUpdate();
                }

                connection.commit();
                connection.setAutoCommit(oldAutoCommit);
                return true;
            } catch (Exception ex) {
                connection.rollback();
                connection.setAutoCommit(oldAutoCommit);
                System.out.println(ex);
                return false;
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return false;
    }

    public boolean updateAddress(int addressId, int userId, String street, String district, String city, boolean isDefault) {
    /* cap nhat thong tin dia chi va dieu chinh dia chi mac dinh */
        String clearDefaultSql = "UPDATE Addresses SET is_default = 0 WHERE user_id = ?";
        String updateSql = "UPDATE Addresses SET street = ?, district = ?, city = ?, is_default = ? WHERE id = ? AND user_id = ?";

        try {
            boolean oldAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try {
                if (isDefault) {
                    try (PreparedStatement psClear = connection.prepareStatement(clearDefaultSql)) {
                        psClear.setInt(1, userId);
                        psClear.executeUpdate();
                    }
                }

                int rows;
                try (PreparedStatement ps = connection.prepareStatement(updateSql)) {
                    ps.setString(1, street);
                    ps.setString(2, district);
                    ps.setString(3, city);
                    ps.setBoolean(4, isDefault);
                    ps.setInt(5, addressId);
                    ps.setInt(6, userId);
                    rows = ps.executeUpdate();
                }

                if (rows > 0) {
                    ensureAnyDefault(userId);
                }

                connection.commit();
                connection.setAutoCommit(oldAutoCommit);
                return rows > 0;
            } catch (Exception ex) {
                connection.rollback();
                connection.setAutoCommit(oldAutoCommit);
                System.out.println(ex);
                return false;
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return false;
    }

    public boolean deleteAddress(int addressId, int userId) {
    /* xoa dia chi va dam bao con mot dia chi mac dinh neu co */
        String deleteSql = "DELETE FROM Addresses WHERE id = ? AND user_id = ?";
        try {
            boolean oldAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try {
                int rows;
                try (PreparedStatement ps = connection.prepareStatement(deleteSql)) {
                    ps.setInt(1, addressId);
                    ps.setInt(2, userId);
                    rows = ps.executeUpdate();
                }

                if (rows > 0) {
                    ensureAnyDefault(userId);
                }

                connection.commit();
                connection.setAutoCommit(oldAutoCommit);
                return rows > 0;
            } catch (Exception ex) {
                connection.rollback();
                connection.setAutoCommit(oldAutoCommit);
                System.out.println(ex);
                return false;
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return false;
    }

    private void ensureAnyDefault(int userId) throws SQLException {
    /* dam bao co it nhat 1 dia chi duoc dat la mac dinh neu ton tai */
        String hasDefaultSql = "SELECT TOP 1 id FROM Addresses WHERE user_id = ? AND is_default = 1";
        String anySql = "SELECT TOP 1 id FROM Addresses WHERE user_id = ? ORDER BY id DESC";
        String setDefaultSql = "UPDATE Addresses SET is_default = 1 WHERE id = ? AND user_id = ?";

        Integer defaultId = null;
        try (PreparedStatement ps = connection.prepareStatement(hasDefaultSql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                defaultId = rs.getInt("id");
            }
        }
        if (defaultId != null) {
            return;
        }

        Integer candidateId = null;
        try (PreparedStatement ps = connection.prepareStatement(anySql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                candidateId = rs.getInt("id");
            }
        }
        if (candidateId == null) {
            return;
        }

        try (PreparedStatement ps = connection.prepareStatement(setDefaultSql)) {
            ps.setInt(1, candidateId);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }
}

