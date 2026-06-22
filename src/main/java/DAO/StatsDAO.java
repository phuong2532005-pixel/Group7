/**
 *
 * Author : Nguyen Trung Hieu & Nguyen Thi Phuong Linh
 * Date : 19/03/2026
 * Description : Thuc hien cac truy van thong ke (doanh thu, ban chay) cho dashboard.
 */
package DAO;

import model.*;
import dal.DBContext;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.ShopReportDTO;
import model.ShopMetricDTO;

public class StatsDAO extends DBContext {
    public Map<String, Object> getDashboardStats() {
    /* lay cac thong ke tong quat cho dashboard (users, orders, revenue) */
        Map<String, Object> stats = new HashMap<>();
        try (Statement st = connection.createStatement()) {
            ResultSet rs = st.executeQuery("SELECT COUNT(*) AS total_users FROM Users");
            if (rs.next()) stats.put("totalUsers", rs.getInt("total_users"));
            rs = st.executeQuery("SELECT COUNT(*) AS total_orders FROM Orders");
            if (rs.next()) stats.put("totalOrders", rs.getInt("total_orders"));
            rs = st.executeQuery("SELECT SUM(total_amount) AS total_revenue FROM Orders");
            if (rs.next()) stats.put("totalRevenue", rs.getLong("total_revenue"));
        } catch (Exception e) {
            System.out.println(e);
        }
        return stats;
    }

    /**
     * Lấy doanh thu theo tháng trong năm cho toàn hệ thống.
     */
    public List<Double> getMonthlyRevenue(int year, Integer shopId) {
    /* lay doanh thu theo thang trong nam cho toan he thong hoac shop */
        List<Double> months = new ArrayList<>();
        for (int i = 0; i < 12; i++) months.add(0.0);
        String sql = "SELECT MONTH(order_date) AS m, SUM(total_amount) AS revenue FROM Orders WHERE YEAR(order_date)=? GROUP BY MONTH(order_date)";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, year);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int m = rs.getInt("m");
                long rev = rs.getLong("revenue");
                if (m >= 1 && m <= 12) months.set(m - 1, (double) rev);
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return months;
    }

    /**
     * Lấy số đơn theo tháng trong năm cho toàn hệ thống.
     */
    public List<Integer> getMonthlyOrderCount(int year, Integer shopId) {
    /* lay so don theo thang trong nam cho toan he thong hoac shop */
        List<Integer> months = new ArrayList<>();
        for (int i = 0; i < 12; i++) months.add(0);
        String sql = "SELECT MONTH(order_date) AS m, COUNT(1) AS total FROM Orders WHERE YEAR(order_date)=? GROUP BY MONTH(order_date)";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, year);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int m = rs.getInt("m");
                int total = rs.getInt("total");
                if (m >= 1 && m <= 12) months.set(m - 1, total);
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return months;
    }

    public int getMaxOrderYear(Integer shopId) {
    /* lay nam lon nhat co du lieu don hang, neu khong thi tra nam hien tai */
        int year = java.time.LocalDate.now().getYear();
        String sql = "SELECT MAX(YEAR(order_date)) AS max_year FROM Orders";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int maxYear = rs.getInt("max_year");
                if (!rs.wasNull()) year = maxYear;
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return year;
    }

    /**
     * Lấy số đơn tổng toàn hệ thống (giữ DTO cũ để tương thích UI).
     */
    public List<ShopReportDTO> getOrderCountByShop() {
    /* lay so don theo tung shop (giu DTO de tuong thich UI) */
        List<ShopReportDTO> results = new ArrayList<>();
        String sql = "SELECT COUNT(1) AS total_orders FROM Orders";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                results.add(new ShopReportDTO(
                        1,
                        "Toan he thong",
                        rs.getInt("total_orders")
                ));
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return results;
    }

    public List<ShopMetricDTO> getMonthlyRevenueByShop(int year, int month) {
    /* lay doanh thu theo shop cho thang va nam duoc chi dinh */
        List<ShopMetricDTO> results = new ArrayList<>();
        String sql = "SELECT COALESCE(SUM(o.total_amount), 0) AS total_revenue "
            + "FROM Orders o WHERE YEAR(o.order_date) = ? AND MONTH(o.order_date) = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, year);
            ps.setInt(2, month);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                results.add(new ShopMetricDTO(
                1,
                "Toan he thong",
                        (double) rs.getLong("total_revenue")
                ));
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return results;
    }

    public List<ShopMetricDTO> getMonthlyOrderCountByShop(int year, int month) {
    /* lay so don theo shop cho thang va nam duoc chi dinh */
        List<ShopMetricDTO> results = new ArrayList<>();
        String sql = "SELECT COALESCE(COUNT(o.id), 0) AS total_orders "
            + "FROM Orders o WHERE YEAR(o.order_date) = ? AND MONTH(o.order_date) = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, year);
            ps.setInt(2, month);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                results.add(new ShopMetricDTO(
                1,
                "Toan he thong",
                        (double) rs.getInt("total_orders")
                ));
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return results;
    }

    /**
     * Lấy thống kê chi tiết toàn hệ thống.
     */
    public Map<String, Object> getShopStats(Integer shopId) {
    /* lay thong ke chi tiet cho mot shop hoac toan he thong */
        Map<String, Object> stats = new HashMap<>();
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT COUNT(DISTINCT o.id) AS totalOrders, " +
                "COALESCE(SUM(o.total_amount), 0) AS totalRevenue, " +
                "COALESCE(AVG(o.total_amount), 0) AS avgOrder " +
                "FROM Orders o")) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                stats.put("totalOrders", rs.getInt("totalOrders"));
                stats.put("totalRevenue", rs.getLong("totalRevenue"));
                stats.put("avgOrder", (long) rs.getDouble("avgOrder"));
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return stats;
    }

    /**
     * Lấy doanh thu toàn hệ thống trong một năm (giữ cấu trúc cũ cho chatbot).
     */
    public List<Map<String, Object>> getAllShopsRevenue(int year) {
    /* lay doanh thu cua tat ca shop trong nam (tra ve danh sach map) */
        List<Map<String, Object>> results = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT COALESCE(SUM(o.total_amount), 0) AS revenue " +
                "FROM Orders o WHERE YEAR(o.order_date) = ?")) {
            ps.setInt(1, year);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", 1);
                map.put("branchName", "Toan he thong");
                map.put("revenue", rs.getLong("revenue"));
                results.add(map);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return results;
    }

    /**
     * Lấy tóm tắt kinh doanh cho chatbot
     * Nếu shopId != null, lấy data của shop đó
     * Nếu shopId == null, lấy data toàn bộ hệ thống
     */
    public String getBusinessSummary(Integer shopId) {
    /* tao chuoi tom tat kinh doanh cho chatbot (shop hoac toan he thong) */
        StringBuilder summary = new StringBuilder();
        try {
            if (shopId != null) {
                // Data cho shop cụ thể
                Map<String, Object> shopStats = getShopStats(shopId);
                summary.append("Dữ liệu kinh doanh của shop:\n");
                summary.append("- Tổng số đơn hàng: ").append(shopStats.get("totalOrders")).append("\n");
                summary.append("- Tổng doanh thu: ").append(shopStats.get("totalRevenue")).append(" VND\n");
                summary.append("- Trung bình đơn hàng: ").append(shopStats.get("avgOrder")).append(" VND\n");

                // Thêm doanh thu theo tháng trong năm hiện tại
                int year = java.time.LocalDate.now().getYear();
                List<Double> monthlyRev = getMonthlyRevenue(year, shopId);
                summary.append("- Doanh thu theo tháng trong năm ").append(year).append(": ");
                for (int i = 0; i < 12; i++) {
                    summary.append(String.format("%.0f", monthlyRev.get(i)));
                    if (i < 11) summary.append(", ");
                }
                summary.append(" VND\n");

            } else {
                // Data toàn bộ hệ thống
                Map<String, Object> globalStats = getDashboardStats();
                summary.append("Dữ liệu kinh doanh toàn hệ thống:\n");
                summary.append("- Tổng số người dùng: ").append(globalStats.get("totalUsers")).append("\n");
                summary.append("- Tổng số đơn hàng: ").append(globalStats.get("totalOrders")).append("\n");
                summary.append("- Tổng doanh thu: ").append(globalStats.get("totalRevenue")).append(" VND\n");

                // Doanh thu của từng shop trong năm hiện tại
                int year = java.time.LocalDate.now().getYear();
                List<Map<String, Object>> shopRevenues = getAllShopsRevenue(year);
                summary.append("- Doanh thu từng shop trong năm ").append(year).append(":\n");
                for (Map<String, Object> shop : shopRevenues) {
                    summary.append("  - ").append(shop.get("branchName")).append(": ").append(shop.get("revenue")).append(" VND\n");
                }
            }
        } catch (Exception e) {
            summary.append("Lỗi khi lấy dữ liệu kinh doanh.\n");
        }
        return summary.toString();
    }
}

