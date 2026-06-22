/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * Author : Nguyen Trung Hieu & Nguyen Thi Phuong Linh
 * Date : 21/03/2026
 * Description : Quan ly truy van nguoi dung (dang nhap, dang ky, tim kiem, cap nhat).
 */
package DAO;

import model.*;
import dal.DBContext;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UsersDAO extends DBContext {

    private UsersDTO mapUser(ResultSet rs) throws SQLException {
    /* chuyen ResultSet sang UsersDTO */
        UsersDTO u = new UsersDTO(
                rs.getInt("id"),
                rs.getInt("status"),
                rs.getInt("role_id"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("full_name"),
                rs.getString("phone"),
                rs.getString("email")
        );
        return u;
    }

    // Kiểm tra username có tồn tại không
    public boolean isUsernameExist(String username) {
    /* kiem tra neu username da ton tai */
        String sql = "SELECT 1 FROM Users WHERE LOWER(username) = LOWER(?)";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println(e);
        }
        return false;
    }

    public boolean isUsernameExistExceptId(String username, int userId) {
    /* kiem tra username da ton tai ngoai tru mot user id */
        String sql = "SELECT 1 FROM Users WHERE LOWER(username) = LOWER(?) AND id <> ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, username);
            ps.setInt(2, userId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println(e);
        }
        return false;
    }

    // Kiểm tra email có tồn tại không
    public boolean isEmailExist(String email) {
    /* kiem tra neu email da ton tai */
        String sql = "SELECT 1 FROM Users WHERE LOWER(email) = LOWER(?)";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println(e);
        }
        return false;
    }

    // Kiểm tra số điện thoại có tồn tại không
    public boolean isPhoneExist(String phone) {
    /* kiem tra neu so dien thoai da ton tai */
        String sql = "SELECT 1 FROM Users WHERE LTRIM(RTRIM(phone)) = LTRIM(RTRIM(?))";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, phone);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println(e);
        }
        return false;
    }

    public boolean isEmailExistExceptId(String email, int userId) {
    /* kiem tra email da ton tai ngoai tru mot user id */
        String sql = "SELECT 1 FROM Users WHERE LOWER(email) = LOWER(?) AND id <> ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, email);
            ps.setInt(2, userId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println(e);
        }
        return false;
    }

    // Kiểm tra role có tồn tại không
    public boolean isRoleExist(int roleId) {
    /* kiem tra neu role_id ton tai trong bang Roles */
        String sql = "SELECT 1 FROM Roles WHERE id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, roleId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println(e);
        }
        return false;
    }

    // Lấy user theo username
    public UsersDTO login(String username, String password) {
    /* dang nhap bang username va password da ma hoa */
        String sql = "SELECT * FROM Users WHERE username = ? AND password = ? AND status = 1";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                UsersDTO u = new UsersDTO(
                        rs.getInt("id"),
                        rs.getInt("status"),
                        rs.getInt("role_id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("full_name"),
                        rs.getString("phone"),
                        rs.getString("email")
                );
                return u;
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return null;
    }

    public UsersDTO loginWithRawPassword(String username, String rawPassword) {
    /* dang nhap voi mat khau raw (ma hoa MD5 trong query) */
        String sql = "SELECT * FROM Users WHERE username = ? AND password = LOWER(CONVERT(VARCHAR(32), HASHBYTES('MD5', ?), 2)) AND status = 1";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, rawPassword);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                UsersDTO u = new UsersDTO(
                        rs.getInt("id"),
                        rs.getInt("status"),
                        rs.getInt("role_id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("full_name"),
                        rs.getString("phone"),
                        rs.getString("email")
                );
                return u;
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return null;
    }

    public boolean isLoginKeyExist(String loginKey) {
    /* kiem tra neu loginKey la username/email/phone ton tai */
        if (loginKey == null || loginKey.trim().isEmpty()) {
            return false;
        }
        String sql = "SELECT TOP 1 1 FROM Users "
                + "WHERE LOWER(LTRIM(RTRIM(username))) = LOWER(?) "
                + "OR LOWER(LTRIM(RTRIM(email))) = LOWER(?) "
                + "OR LTRIM(RTRIM(phone)) = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            String key = loginKey.trim();
            ps.setString(1, key);
            ps.setString(2, key);
            ps.setString(3, key);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw new IllegalStateException("DB query failed while checking login key existence", e);
        }
    }

    public Integer getUserStatusByLoginKey(String loginKey) {
    /* tra ve status cua user dua vao loginKey (username/email/phone) */
        if (loginKey == null || loginKey.trim().isEmpty()) {
            return null;
        }
        String sql = "SELECT TOP 1 status FROM Users "
                + "WHERE username = ? OR email = ? OR phone = ? "
                + "ORDER BY CASE "
                + "WHEN username = ? THEN 1 "
                + "WHEN email = ? THEN 2 "
                + "WHEN phone = ? THEN 3 "
                + "ELSE 4 END";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            String key = loginKey.trim();
            ps.setString(1, key);
            ps.setString(2, key);
            ps.setString(3, key);
            ps.setString(4, key);
            ps.setString(5, key);
            ps.setString(6, key);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("status");
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return null;
    }

    public UsersDTO loginByLoginKey(String loginKey, String passwordHash) {
    /* dang nhap bang loginKey va passwordHash (da ma) */
        if (loginKey == null || loginKey.trim().isEmpty()) {
            return null;
        }
        String sql = "SELECT TOP 1 * FROM Users "
                + "WHERE (username = ? OR email = ? OR phone = ?) AND password = ? AND status = 1 "
                + "ORDER BY CASE "
                + "WHEN username = ? THEN 1 "
                + "WHEN email = ? THEN 2 "
                + "WHEN phone = ? THEN 3 "
                + "ELSE 4 END";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            String key = loginKey.trim();
            ps.setString(1, key);
            ps.setString(2, key);
            ps.setString(3, key);
            ps.setString(4, passwordHash);
            ps.setString(5, key);
            ps.setString(6, key);
            ps.setString(7, key);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapUser(rs);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return null;
    }

    public UsersDTO loginWithRawPasswordByLoginKey(String loginKey, String rawPassword) {
    /* dang nhap bang loginKey va mat khau raw (ma hoa MD5 trong query) */
        if (loginKey == null || loginKey.trim().isEmpty()) {
            return null;
        }
        String sql = "SELECT TOP 1 * FROM Users "
                + "WHERE (username = ? OR email = ? OR phone = ?) "
                + "AND password = LOWER(CONVERT(VARCHAR(32), HASHBYTES('MD5', ?), 2)) AND status = 1 "
                + "ORDER BY CASE "
                + "WHEN username = ? THEN 1 "
                + "WHEN email = ? THEN 2 "
                + "WHEN phone = ? THEN 3 "
                + "ELSE 4 END";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            String key = loginKey.trim();
            ps.setString(1, key);
            ps.setString(2, key);
            ps.setString(3, key);
            ps.setString(4, rawPassword);
            ps.setString(5, key);
            ps.setString(6, key);
            ps.setString(7, key);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapUser(rs);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return null;
    }

    public UsersDTO getUserById(int userId) {
    /* lay thong tin user theo id */
        String sql = "SELECT * FROM Users WHERE id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                UsersDTO u = new UsersDTO(
                        rs.getInt("id"),
                        rs.getInt("status"),
                        rs.getInt("role_id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("full_name"),
                        rs.getString("phone"),
                        rs.getString("email")
                );
                return u;
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return null;
    }

    public int createUser(UsersDTO user) {
    /* tao user moi va tra ve id neu thanh cong */
        String sql = "INSERT INTO Users (username, password, full_name, phone, email, status, role_id) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try {
            try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, user.getUsername());
                ps.setString(2, user.getPassword());
                ps.setString(3, user.getFull_name());
                ps.setString(4, user.getPhone());
                ps.setString(5, user.getEmail());
                ps.setInt(6, user.getStatus());
                ps.setInt(7, user.getRole_id());

                int rows = ps.executeUpdate();
                if (rows > 0) {
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            return rs.getInt(1);
                        }
                    }

                    // Fallback for SQL Server when generated keys are not returned by driver settings.
                    try (PreparedStatement idPs = connection.prepareStatement("SELECT CAST(SCOPE_IDENTITY() AS INT)")) {
                        try (ResultSet idRs = idPs.executeQuery()) {
                            if (idRs.next()) {
                                int id = idRs.getInt(1);
                                if (id > 0) {
                                    return id;
                                }
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("[UsersDAO.createUser] SQLState=" + e.getSQLState() + ", ErrorCode=" + e.getErrorCode() + ", Message=" + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    public boolean deleteUserHard(int userId) {
    /* xoa user khoi he thong (hard delete) */
        String sql = "DELETE FROM Users WHERE id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println(e);
        }
        return false;
    }

    public Integer getUserStatusByUsername(String username) {
    /* lay status cua user theo username */
        String sql = "SELECT TOP 1 status FROM Users WHERE username = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("status");
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return null;
    }

    public boolean updateProfileBasic(int id, String username, String fullName, String phone) {
    /* cap nhat thong tin co ban cua profile (username, fullName, phone) */
        String sql = "UPDATE Users SET username = ?, full_name = ?, phone = ? WHERE id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, fullName);
            ps.setString(3, phone);
            ps.setInt(4, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println(e);
        }
        return false;
    }

    public boolean updateEmail(int id, String email) {
    /* cap nhat email cua user */
        String sql = "UPDATE Users SET email = ? WHERE id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, email);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println(e);
        }
        return false;
    }

    public boolean updatePassword(int id, String hashedPassword) {
    /* cap nhat mat khau (da ma hoa) cho user */
        String sql = "UPDATE Users SET password = ? WHERE id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, hashedPassword);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println(e);
        }
        return false;
    }

    public Integer getUserIdByEmail(String email) {
    /* lay id user theo email */
        String sql = "SELECT TOP 1 id FROM Users WHERE email = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return null;
    }

    public boolean updateUserAdmin(int id, String fullName, String phone, String email, int roleId, int status, String hashedPassword) {
    /* cap nhat thong tin user tu trang admin (voi hoac khong doi mat khau) */
        if (hashedPassword == null || hashedPassword.trim().isEmpty()) {
            String sql = "UPDATE Users SET full_name = ?, phone = ?, email = ?, role_id = ?, status = ? WHERE id = ?";
            try {
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setString(1, fullName);
                ps.setString(2, phone);
                ps.setString(3, email);
                ps.setInt(4, roleId);
                ps.setInt(5, status);
                ps.setInt(6, id);
                return ps.executeUpdate() > 0;
            } catch (SQLException e) {
                System.out.println(e);
            }
        } else {
            String sql = "UPDATE Users SET full_name = ?, phone = ?, email = ?, role_id = ?, status = ?, password = ? WHERE id = ?";
            try {
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setString(1, fullName);
                ps.setString(2, phone);
                ps.setString(3, email);
                ps.setInt(4, roleId);
                ps.setInt(5, status);
                ps.setString(6, hashedPassword);
                ps.setInt(7, id);
                return ps.executeUpdate() > 0;
            } catch (SQLException e) {
                System.out.println(e);
            }
        }
        return false;
    }

    public List<UsersDTO> listUsersByRole(Integer roleId, Integer status, int offset, int limit) {
    /* lay danh sach user theo role va status voi phan trang */
        List<UsersDTO> users = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM Users");
        boolean hasWhere = false;
        if (roleId != null) {
            sql.append(" WHERE role_id = ?");
            hasWhere = true;
        }
        if (status != null) {
            sql.append(hasWhere ? " AND status = ?" : " WHERE status = ?");
        }
        sql.append(" ORDER BY id OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        try {
            PreparedStatement ps = connection.prepareStatement(sql.toString());
            int index = 1;
            if (roleId != null) {
                ps.setInt(index++, roleId);
            }
            if (status != null) {
                ps.setInt(index++, status);
            }
            ps.setInt(index++, offset);
            ps.setInt(index, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                UsersDTO user = new UsersDTO(
                        rs.getInt("id"),
                        rs.getInt("status"),
                        rs.getInt("role_id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("full_name"),
                        rs.getString("phone"),
                        rs.getString("email")
                );
                users.add(user);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return users;
    }

    public int countUsersByRole(Integer roleId, Integer status) {
    /* dem so user theo role va status */
        StringBuilder sql = new StringBuilder("SELECT COUNT(1) FROM Users");
        boolean hasWhere = false;
        if (roleId != null) {
            sql.append(" WHERE role_id = ?");
            hasWhere = true;
        }
        if (status != null) {
            sql.append(hasWhere ? " AND status = ?" : " WHERE status = ?");
        }
        try {
            PreparedStatement ps = connection.prepareStatement(sql.toString());
            int index = 1;
            if (roleId != null) {
                ps.setInt(index++, roleId);
            }
            if (status != null) {
                ps.setInt(index, status);
            }
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return 0;
    }

    public boolean softDeleteUser(int id) {
    /* dat status = 0 de xoa mem user */
        String sql = "UPDATE Users SET status = 0 WHERE id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println(e);
        }
        return false;
    }

    public boolean restoreUser(int id) {
    /* phuc hoi user bang cach dat status = 1 */
        String sql = "UPDATE Users SET status = 1 WHERE id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println(e);
        }
        return false;
    }
}


