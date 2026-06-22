/**
 *
 * Author : Nguyen Trung Hieu & Nguyen Thi Phuong Linh
 * Date : 13/03/2026
 * Description : CRUD cho mau sac/thuoc tinh mau cua bien the san pham.
 */
package DAO;

import model.*;
import dal.DBContext;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ColorDAO extends DBContext {

    public List<ColorDTO> getAllColors() {
    /* lay tat ca mau sac */
        List<ColorDTO> colors = new ArrayList<>();
        String sql = "SELECT id, color_name FROM Colors ORDER BY id";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                colors.add(new ColorDTO(
                        rs.getInt("id"),
                        rs.getString("color_name")
                ));
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return colors;
    }

    public Integer getColorIdByName(String colorName) {
    /* lay id mau sac theo ten */
        if (colorName == null || colorName.trim().isEmpty()) {
            return null;
        }
        String sql = "SELECT id FROM Colors WHERE color_name = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, colorName.trim());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return null;
    }

    public ColorDTO getColorById(int colorId) {
    /* lay thong tin mau sac theo id */
        String sql = "SELECT id, color_name FROM Colors WHERE id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, colorId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new ColorDTO(
                        rs.getInt("id"),
                        rs.getString("color_name")
                );
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return null;
    }

    public Integer createColor(String colorName) {
    /* tao mau moi neu chua ton tai va tra ve id */
        if (colorName == null || colorName.trim().isEmpty()) {
            return null;
        }
        Integer existing = getColorIdByName(colorName.trim());
        if (existing != null) {
            return existing;
        }
        String sql = "INSERT INTO Colors (color_name) VALUES (?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, colorName.trim());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error creating color: " + e.getMessage());
        }
        return null;
    }
}

