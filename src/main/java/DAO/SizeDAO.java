/**
 *
 * Author : Nguyen Trung Hieu & Nguyen Thi Phuong Linh
 * Date : 18/03/2026
 * Description : CRUD cho thong tin kich co (sizes) cua bien the.
 */
package DAO;

import model.*;
import dal.DBContext;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SizeDAO extends DBContext {

    public List<SizeDTO> getAllSizes() {
    /* lay tat ca kich co */
        List<SizeDTO> sizes = new ArrayList<>();
        String sql = "SELECT id, size_name FROM Sizes ORDER BY id";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                sizes.add(new SizeDTO(
                        rs.getInt("id"),
                        rs.getString("size_name")
                ));
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return sizes;
    }

    public Integer getSizeIdByName(String sizeName) {
    /* lay id kich co theo ten */
        if (sizeName == null || sizeName.trim().isEmpty()) {
            return null;
        }
        String sql = "SELECT id FROM Sizes WHERE size_name = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, sizeName.trim());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return null;
    }

    public SizeDTO getSizeById(int sizeId) {
    /* lay thong tin kich co theo id */
        String sql = "SELECT id, size_name FROM Sizes WHERE id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, sizeId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new SizeDTO(
                        rs.getInt("id"),
                        rs.getString("size_name")
                );
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return null;
    }

    public Integer createSize(String sizeName) {
    /* tao kich co moi neu chua ton tai va tra ve id */
        if (sizeName == null || sizeName.trim().isEmpty()) {
            return null;
        }
        Integer existing = getSizeIdByName(sizeName.trim());
        if (existing != null) {
            return existing;
        }
        String sql = "INSERT INTO Sizes (size_name) VALUES (?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, sizeName.trim());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error creating size: " + e.getMessage());
        }
        return null;
    }
}

