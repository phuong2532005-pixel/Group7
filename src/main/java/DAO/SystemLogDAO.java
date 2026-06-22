/**
 *
 * Author : Nguyen Trung Hieu & Nguyen Thi Phuong Linh
 * Date : 20/03/2026
 * Description : Ghi nhan va truy van log he thong (system logs).
 */
package DAO;

import model.*;
import dal.DBContext;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SystemLogDAO extends DBContext {

    public List<SystemLogDTO> getAllLogs(int limit) {
    /* lay tat ca log he thong voi gioi han so ban ghi */
        List<SystemLogDTO> logs = new ArrayList<>();
        String sql = "SELECT TOP " + limit + " sl.id, sl.user_id, u.full_name, sl.action, "
                + "CONVERT(VARCHAR(19), sl.created_at, 120) AS log_time "
                + "FROM dbo.SystemLogs sl "
                + "LEFT JOIN dbo.Users u ON sl.user_id = u.id "
                + "ORDER BY sl.created_at DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            System.out.println("[SystemLogDAO] Executing query: " + sql);

            while (rs.next()) {
                logs.add(new SystemLogDTO(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("full_name"),
                        rs.getString("action"),
                        rs.getString("log_time")
                ));
            }

            System.out.println("[SystemLogDAO] Total logs found: " + logs.size());
        } catch (SQLException e) {
            System.out.println("[SystemLogDAO] SQL ERROR: " + e.getMessage());
            e.printStackTrace();
        }

        return logs;
    }

    public List<SystemLogDTO> getLogsByUser(int userId, int limit) {
    /* lay log cua mot nguoi dung voi gioi han so ban ghi */
        List<SystemLogDTO> logs = new ArrayList<>();
        String sql = "SELECT TOP " + limit + " sl.id, sl.user_id, u.full_name, sl.action, "
                + "CONVERT(VARCHAR(19), sl.created_at, 120) AS log_time "
                + "FROM dbo.SystemLogs sl "
                + "LEFT JOIN dbo.Users u ON sl.user_id = u.id "
                + "WHERE sl.user_id = ? "
                + "ORDER BY sl.created_at DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    logs.add(new SystemLogDTO(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getString("full_name"),
                            rs.getString("action"),
                            rs.getString("log_time")
                    ));
                }
            }
        } catch (SQLException e) {
            System.out.println("[SystemLogDAO] SQL ERROR: " + e.getMessage());
            e.printStackTrace();
        }

        return logs;
    }

    public List<SystemLogDTO> getLogsByDateRange(String startDate, String endDate, int limit) {
    /* lay log trong khoang thoi gian duoc chi dinh */
        List<SystemLogDTO> logs = new ArrayList<>();
        String sql = "SELECT TOP " + limit + " sl.id, sl.user_id, u.full_name, sl.action, "
                + "CONVERT(VARCHAR(19), sl.created_at, 120) AS log_time "
                + "FROM dbo.SystemLogs sl "
                + "LEFT JOIN dbo.Users u ON sl.user_id = u.id "
                + "WHERE sl.created_at BETWEEN ? AND ? "
                + "ORDER BY sl.created_at DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, startDate);
            stmt.setString(2, endDate);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    logs.add(new SystemLogDTO(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getString("full_name"),
                            rs.getString("action"),
                            rs.getString("log_time")
                    ));
                }
            }
        } catch (SQLException e) {
            System.out.println("[SystemLogDAO] SQL ERROR: " + e.getMessage());
            e.printStackTrace();
        }

        return logs;
    }

    public int countLogs() {
    /* dem tong so ban ghi log */
        String sql = "SELECT COUNT(*) AS total FROM dbo.SystemLogs";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.out.println("[SystemLogDAO] countLogs ERROR: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    public String getCurrentDatabaseName() {
    /* tra ve ten database hien tai */
        String sql = "SELECT DB_NAME() AS db_name";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getString("db_name");
            }
        } catch (SQLException e) {
            System.out.println("[SystemLogDAO] getCurrentDatabaseName ERROR: " + e.getMessage());
            e.printStackTrace();
        }
        return "unknown";
    }

    public String getCurrentServerName() {
    /* tra ve ten server sql hien tai */
        String sql = "SELECT @@SERVERNAME AS server_name";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getString("server_name");
            }
        } catch (SQLException e) {
            System.out.println("[SystemLogDAO] getCurrentServerName ERROR: " + e.getMessage());
            e.printStackTrace();
        }
        return "unknown";
    }

    public List<String> getSystemLogColumns() {
    /* lay danh sach cot cua bang SystemLogs */
        List<String> columns = new ArrayList<>();
        String sql = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = 'dbo' AND TABLE_NAME = 'SystemLogs' ORDER BY ORDINAL_POSITION";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                columns.add(rs.getString("COLUMN_NAME"));
            }
        } catch (SQLException e) {
            System.out.println("[SystemLogDAO] getSystemLogColumns ERROR: " + e.getMessage());
            e.printStackTrace();
        }
        return columns;
    }

    public boolean insertLog(int userId, String action) {
    /* chen mot dong log moi vao bang SystemLogs */
        String sql = "INSERT INTO dbo.SystemLogs (user_id, action, created_at) VALUES (?, ?, GETDATE())";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, action);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("[SystemLogDAO] insertLog ERROR: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}

