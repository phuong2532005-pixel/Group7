/**
 *
 * Author : Nguyen Trung Hieu & Nguyen Thi Phuong Linh
 * Date : 12/03/2026
 * Description : Truy van va quan ly thong tin danh muc san pham.
 */
package DAO;

import model.*;
import dal.DBContext;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO extends DBContext {

    public List<CategoryDTO> getAllCategories() {
    /* lay tat ca danh muc san pham */
        List<CategoryDTO> categories = new ArrayList<>();
        String sql = "SELECT id, name, parent_id FROM Categories ORDER BY name";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                categories.add(new CategoryDTO(
                        rs.getInt("id"),
                        rs.getString("name"),
                        (Integer) rs.getObject("parent_id")
                ));
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return categories;
    }

    public Integer getCategoryIdByNameInsensitive(String name) {
    /* lay id danh muc theo ten (khong phan biet phu am) */
        if (name == null || name.trim().isEmpty()) {
            return null;
        }
        String sql = "SELECT TOP 1 id FROM Categories WHERE name COLLATE Latin1_General_CI_AI = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, name.trim());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return null;
    }

    public Integer getCategoryIdByNameAndParentInsensitive(String name, Integer parentId) {
    /* lay id danh muc theo ten va parent (khong phan biet phu am) */
        if (name == null || name.trim().isEmpty()) {
            return null;
        }
        String sql;
        if (parentId == null) {
            sql = "SELECT TOP 1 id FROM Categories WHERE name COLLATE Latin1_General_CI_AI = ? AND parent_id IS NULL";
        } else {
            sql = "SELECT TOP 1 id FROM Categories WHERE name COLLATE Latin1_General_CI_AI = ? AND parent_id = ?";
        }
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, name.trim());
            if (parentId != null) {
                ps.setInt(2, parentId);
            }
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return null;
    }

    public Integer createCategory(String name, Integer parentId) {
    /* tao danh muc moi va tra ve id neu thanh cong */
        if (name == null || name.trim().isEmpty()) {
            return null;
        }
        String sql = "INSERT INTO Categories (name, parent_id) VALUES (?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name.trim());
            if (parentId != null) {
                ps.setInt(2, parentId);
            } else {
                ps.setNull(2, java.sql.Types.INTEGER);
            }
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error creating category: " + e.getMessage());
        }
        return null;
    }

    public CategoryDTO getCategoryById(int id) {
    /* lay thong tin danh muc theo id */
        String sql = "SELECT id, name, parent_id FROM Categories WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new CategoryDTO(
                        rs.getInt("id"),
                        rs.getString("name"),
                        (Integer) rs.getObject("parent_id")
                );
            }
        } catch (SQLException e) {
            System.out.println("Error fetching category: " + e.getMessage());
        }
        return null;
    }

    public boolean updateCategory(int id, String name, Integer parentId) {
    /* cap nhat ten va parent cua danh muc */
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        String sql = "UPDATE Categories SET name = ?, parent_id = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, name.trim());
            if (parentId != null) {
                ps.setInt(2, parentId);
            } else {
                ps.setNull(2, java.sql.Types.INTEGER);
            }
            ps.setInt(3, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error updating category: " + e.getMessage());
        }
        return false;
    }

    public boolean deleteCategory(int id) {
    /* xoa danh muc theo id */
        String sql = "DELETE FROM Categories WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting category: " + e.getMessage());
        }
        return false;
    }

    public boolean hasChildCategories(int categoryId) {
    /* kiem tra neu danh muc con danh muc con */
        String sql = "SELECT TOP 1 1 FROM Categories WHERE parent_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println("Error checking child categories: " + e.getMessage());
        }
        return false;
    }

    public boolean hasProducts(int categoryId) {
    /* kiem tra neu danh muc co san pham */
        String sql = "SELECT TOP 1 1 FROM Products WHERE category_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println("Error checking products by category: " + e.getMessage());
        }
        return false;
    }
}

