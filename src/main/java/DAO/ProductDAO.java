/**
 *
 * Author : Nguyen Trung Hieu & Nguyen Thi Phuong Linh
 * Date : 15/03/2026
 * Description : Truy van san pham, bien the, hinh anh va cac ham phuc vu tim kiem.
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

public class ProductDAO extends DBContext {

    public int createProduct(String name, String description, int categoryId, long basePrice,
            String thumbnail, int status) {
    /* tao san pham moi va tra ve productId neu thanh cong */
        String sql = "INSERT INTO Products (name, description, category_id, base_price, thumbnail, status) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, name);
            ps.setString(2, description);
            ps.setInt(3, categoryId);
            ps.setLong(4, basePrice);
            ps.setString(5, thumbnail);
            ps.setInt(6, status);
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
        return -1;
    }

    public int createVariant(int productId, int sizeId, int colorId, long price, String imageUrl) {
    /* tao variant moi cho product va tra ve variantId */
        String sql = "INSERT INTO ProductVariants (product_id, size_id, color_id, price, image_url, stock) "
                + "VALUES (?, ?, ?, ?, ?, 0)";
        try {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, productId);
            ps.setInt(2, sizeId);
            ps.setInt(3, colorId);
            ps.setLong(4, price);
            ps.setString(5, imageUrl);
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
        return -1;
    }

    public boolean updateProduct(int productId, String name, String description, int categoryId,
            long basePrice, String thumbnail, int status) {
    /* cap nhat thong tin san pham */
        String sql = "UPDATE Products "
                + "SET name = ?, description = ?, category_id = ?, base_price = ?, thumbnail = ?, status = ? "
                + "WHERE id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, description);
            ps.setInt(3, categoryId);
            ps.setLong(4, basePrice);
            ps.setString(5, thumbnail);
            ps.setInt(6, status);
            ps.setInt(7, productId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println(e);
        }
        return false;
    }

    public boolean updateVariant(int variantId, int sizeId, int colorId, long price, String imageUrl) {
    /* cap nhat thong tin variant */
        String sql = "UPDATE ProductVariants SET size_id = ?, color_id = ?, price = ?, image_url = ? WHERE id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, sizeId);
            ps.setInt(2, colorId);
            ps.setLong(3, price);
            ps.setString(4, imageUrl);
            ps.setInt(5, variantId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println(e);
        }
        return false;
    }

    public ProductVariantDTO getVariantById(int variantId) {
    /* lay thong tin variant theo id */
        String sql = "SELECT v.id, v.product_id, v.size_id, v.color_id, s.size_name, c.color_name, v.price, v.image_url "
                + "FROM ProductVariants v "
                + "JOIN Sizes s ON s.id = v.size_id "
                + "JOIN Colors c ON c.id = v.color_id "
                + "WHERE v.id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, variantId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new ProductVariantDTO(
                        rs.getInt("id"),
                        rs.getInt("product_id"),
                        rs.getInt("size_id"),
                        rs.getInt("color_id"),
                        rs.getString("size_name"),
                        rs.getString("color_name"),
                        rs.getLong("price"),
                        rs.getString("image_url"));
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return null;
    }

    public boolean isProductUsedInOrders(int productId) {
    /* kiem tra neu san pham da duoc su dung trong don hang */
        String sql = "SELECT COUNT(1) AS total "
                + "FROM OrderItems oi "
                + "JOIN ProductVariants pv ON oi.variant_id = pv.id "
                + "WHERE pv.product_id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("total") > 0;
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return true;
    }

    public int getTotalSoldByProductId(int productId) {
    /* tinh tong so luong da ban cua san pham (khong tinh don da huy) */
        String sql = "SELECT ISNULL(SUM(oi.quantity), 0) AS total_sold "
                + "FROM OrderItems oi "
                + "JOIN ProductVariants pv ON oi.variant_id = pv.id "
                + "JOIN Orders o ON o.id = oi.order_id "
                + "WHERE pv.product_id = ? "
                + "AND UPPER(ISNULL(o.shipping_status, '')) <> 'CANCELLED'";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("total_sold");
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return 0;
    }

    public boolean createVariantImage(int variantId, String imageUrl, boolean isThumbnail) {
    /* them anh cho variant (co the la thumbnail) */
        String sql = "INSERT INTO VariantImages (variant_id, image_url, is_thumbnail) VALUES (?, ?, ?)";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, variantId);
            ps.setString(2, imageUrl);
            ps.setBoolean(3, isThumbnail);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println(e);
        }
        return false;
    }

    public List<ProductDTO> getProductsByStatus(int status) {
    /* lay danh sach san pham theo trang thai */
        List<ProductDTO> products = new ArrayList<>();
        String sql = "SELECT id, name, description, category_id, base_price, thumbnail, status "
                + "FROM Products WHERE status = ? ORDER BY id DESC";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, status);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                products.add(mapProduct(rs));
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return products;
    }

    public List<SearchCandidateDTO> getSearchCandidates(int limit) {
    /* lay danh sach ung vien tim kiem (de su dung autocomplete) */
        List<SearchCandidateDTO> candidates = new ArrayList<>();
        String sql = "SELECT TOP (?) p.id, p.name, p.description, cat.name AS category_name, "
                + "ISNULL(SUM(CASE "
                + "    WHEN oi.id IS NOT NULL AND UPPER(ISNULL(o.shipping_status, '')) <> 'CANCELLED' THEN oi.quantity "
                + "    ELSE 0 "
                + "END), 0) AS total_sold "
                + "FROM Products p "
                + "LEFT JOIN Categories cat ON cat.id = p.category_id "
                + "LEFT JOIN ProductVariants pv ON pv.product_id = p.id "
                + "LEFT JOIN OrderItems oi ON oi.variant_id = pv.id "
                + "LEFT JOIN Orders o ON o.id = oi.order_id "
                + "WHERE p.status = 1 "
                + "GROUP BY p.id, p.name, p.description, cat.name "
                + "ORDER BY p.id DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                candidates.add(new SearchCandidateDTO(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getString("category_name"),
                        rs.getInt("total_sold")));
            }
        } catch (SQLException e) {
            System.out.println(e);
        }

        return candidates;
    }

    public ProductDTO getProductById(int productId) {
    /* lay thong tin san pham theo id */
        String sql = "SELECT id, name, description, category_id, base_price, thumbnail, status "
                + "FROM Products WHERE id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapProduct(rs);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return null;
    }

    public boolean updateStatus(int productId, int status) {
    /* cap nhat trang thai cua san pham */
        String sql = "UPDATE Products SET status = ? WHERE id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, status);
            ps.setInt(2, productId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println(e);
        }
        return false;
    }

    public boolean deleteProduct(int productId) {
    /* xoa san pham khoi he thong */
        String sql = "DELETE FROM Products WHERE id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, productId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println(e);
        }
        return false;
    }

    public List<ProductVariantDTO> getVariantsByProductId(int productId) {
    /* lay tat ca variant cua mot san pham */
        List<ProductVariantDTO> variants = new ArrayList<>();
        String sql = "SELECT v.id, v.product_id, v.size_id, v.color_id, s.size_name, c.color_name, v.price, v.image_url "
                + "FROM ProductVariants v "
                + "JOIN Sizes s ON s.id = v.size_id "
                + "JOIN Colors c ON c.id = v.color_id "
                + "WHERE v.product_id = ? ORDER BY v.id";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                variants.add(new ProductVariantDTO(
                        rs.getInt("id"),
                        rs.getInt("product_id"),
                        rs.getInt("size_id"),
                        rs.getInt("color_id"),
                        rs.getString("size_name"),
                        rs.getString("color_name"),
                        rs.getLong("price"),
                        rs.getString("image_url")));
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return variants;
    }

    public List<ProductVariantDTO> getAvailableVariantsByProductId(int productId) {
    /* lay variant con ton kho cua san pham */
        List<ProductVariantDTO> variants = new ArrayList<>();
        String sql = "SELECT v.id, v.product_id, v.size_id, v.color_id, s.size_name, c.color_name, v.price, v.image_url "
                + "FROM ProductVariants v "
                + "JOIN Sizes s ON s.id = v.size_id "
                + "JOIN Colors c ON c.id = v.color_id "
                + "WHERE v.product_id = ? AND v.stock > 0 ORDER BY v.id";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                variants.add(new ProductVariantDTO(
                        rs.getInt("id"),
                        rs.getInt("product_id"),
                        rs.getInt("size_id"),
                        rs.getInt("color_id"),
                        rs.getString("size_name"),
                        rs.getString("color_name"),
                        rs.getLong("price"),
                        rs.getString("image_url")));
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return variants;
    }

    public CartItemDTO getVariantSummary(int variantId) {
    /* lay thong tin tom tat cua variant de them vao gio hang */
        String sql = "SELECT v.id AS variant_id, p.id AS product_id, v.size_id, v.color_id, p.name AS product_name, c.color_name, s.size_name, "
                + "v.price, v.image_url, p.thumbnail "
                + "FROM ProductVariants v "
                + "JOIN Sizes s ON s.id = v.size_id "
                + "JOIN Colors c ON c.id = v.color_id "
                + "JOIN Products p ON p.id = v.product_id "
                + "WHERE v.id = ? AND p.status = 1";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, variantId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String imageUrl = rs.getString("image_url");
                if (imageUrl == null || imageUrl.trim().isEmpty()) {
                    imageUrl = rs.getString("thumbnail");
                }
                return new CartItemDTO(
                        rs.getInt("variant_id"),
                        rs.getInt("product_id"),
                        rs.getInt("size_id"),
                        rs.getInt("color_id"),
                        rs.getString("product_name"),
                        rs.getString("color_name"),
                        rs.getString("size_name"),
                        rs.getLong("price"),
                        1,
                        imageUrl);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return null;
    }

    public List<ProductDTO> searchProducts(String keyword, Integer categoryId,
            Double minPrice, Double maxPrice, Integer shopId, int offset, int limit) {
    /* tim san pham theo tu khoa, danh muc va phan trang */
        List<ProductDTO> products = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        StringBuilder sql = new StringBuilder();

        if (categoryId != null) {
            sql.append("WITH cte AS (")
                    .append("SELECT id FROM Categories WHERE id = ? ")
                    .append("UNION ALL ")
                    .append("SELECT c.id FROM Categories c JOIN cte ON c.parent_id = cte.id")
                    .append(") ");
            params.add(categoryId);
        }

        sql.append("SELECT p.id, p.name, p.description, p.category_id, p.base_price, p.thumbnail, p.status ")
                .append("FROM Products p ")
                .append("WHERE p.status = 1 ");

        if (categoryId != null) {
            sql.append("AND p.category_id IN (SELECT id FROM cte) ");
        }

        // Token-based search on product name only.
        List<String> tokens = splitSearchTokens(keyword);
        if (tokens != null && !tokens.isEmpty()) {
            for (String token : tokens) {
                sql.append("AND ( ' ' + LOWER(p.name) + ' ' COLLATE Latin1_General_CI_AI LIKE ? ) ");
                String pattern = "% " + token.toLowerCase() + " %";
                params.add(pattern);
            }
        } else {
            String likePattern = normalizeLikePattern(keyword);
            if (likePattern != null) {
                sql.append("AND (p.name COLLATE Latin1_General_CI_AI LIKE ?) ");
                params.add(likePattern);
            }
        }

        if (minPrice != null || maxPrice != null) {
            sql.append("AND EXISTS (SELECT 1 FROM ProductVariants v WHERE v.product_id = p.id ");
            if (minPrice != null) {
                sql.append("AND v.price >= ? ");
                params.add(minPrice);
            }
            if (maxPrice != null) {
                sql.append("AND v.price <= ? ");
                params.add(maxPrice);
            }
            sql.append(") ");
        }

        sql.append("ORDER BY p.id DESC ")
                .append("OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        params.add(offset);
        params.add(limit);

        try {
            PreparedStatement ps = connection.prepareStatement(sql.toString());
            bindParams(ps, params);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                products.add(mapProduct(rs));
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return products;
    }

    public int countProducts(String keyword, Integer categoryId, Double minPrice, Double maxPrice, Integer shopId) {
    /* dem tong so san pham phu hop voi tieu chi tim kiem */
        List<Object> params = new ArrayList<>();
        StringBuilder sql = new StringBuilder();

        if (categoryId != null) {
            sql.append("WITH cte AS (")
                    .append("SELECT id FROM Categories WHERE id = ? ")
                    .append("UNION ALL ")
                    .append("SELECT c.id FROM Categories c JOIN cte ON c.parent_id = cte.id")
                    .append(") ");
            params.add(categoryId);
        }

        sql.append("SELECT COUNT(1) AS total FROM Products p ")
                .append("WHERE p.status = 1 ");

        if (categoryId != null) {
            sql.append("AND p.category_id IN (SELECT id FROM cte) ");
        }

        // Keep the same token logic as list query so pagination totals stay consistent.
        List<String> tokens = splitSearchTokens(keyword);
        if (tokens != null && !tokens.isEmpty()) {
            for (String token : tokens) {
                sql.append("AND ( ' ' + LOWER(p.name) + ' ' COLLATE Latin1_General_CI_AI LIKE ? ) ");
                String pattern = "% " + token.toLowerCase() + " %";
                params.add(pattern);
            }
        } else {
            String likePattern = normalizeLikePattern(keyword);
            if (likePattern != null) {
                sql.append("AND (p.name COLLATE Latin1_General_CI_AI LIKE ?) ");
                params.add(likePattern);
            }
        }

        if (minPrice != null || maxPrice != null) {
            sql.append("AND EXISTS (SELECT 1 FROM ProductVariants v WHERE v.product_id = p.id ");
            if (minPrice != null) {
                sql.append("AND v.price >= ? ");
                params.add(minPrice);
            }
            if (maxPrice != null) {
                sql.append("AND v.price <= ? ");
                params.add(maxPrice);
            }
            sql.append(") ");
        }

        try {
            PreparedStatement ps = connection.prepareStatement(sql.toString());
            bindParams(ps, params);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return 0;
    }

    public List<ProductDTO> getProductsByTopCategoryName(String topCategoryName, int limit) {
    /* lay san pham theo ten nhom danh muc cap tren */
        List<ProductDTO> products = new ArrayList<>();
        String sql = "WITH cte AS ("
                + "SELECT id FROM Categories WHERE name = ? AND parent_id IS NULL "
                + "UNION ALL "
                + "SELECT c.id FROM Categories c JOIN cte ON c.parent_id = cte.id"
                + ") "
                + "SELECT TOP (?) p.id, p.name, p.description, p.category_id, p.base_price, p.thumbnail, p.status "
                + "FROM Products p "
                + "WHERE p.status = 1 AND p.category_id IN (SELECT id FROM cte) "
                + "ORDER BY p.id DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, topCategoryName);
            ps.setInt(2, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                products.add(mapProduct(rs));
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return products;
    }

    public List<ProductDTO> getBestSellingProducts(int limit) {
    /* lay danh sach san pham ban chay nhat */
        List<ProductDTO> products = new ArrayList<>();
        String sql = "SELECT TOP (?) p.id, p.name, p.description, p.category_id, p.base_price, p.thumbnail, p.status "
                + "FROM OrderItems oi "
                + "JOIN ProductVariants v ON oi.variant_id = v.id "
                + "JOIN Products p ON v.product_id = p.id "
                + "WHERE p.status = 1 "
                + "GROUP BY p.id, p.name, p.description, p.category_id, p.base_price, p.thumbnail, p.status "
                + "ORDER BY SUM(oi.quantity) DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                products.add(mapProduct(rs));
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return products;
    }

    private void bindParams(PreparedStatement ps, List<Object> params) throws SQLException {
        for (int i = 0; i < params.size(); i++) {
            Object value = params.get(i);
            if (value instanceof Integer) {
                ps.setInt(i + 1, (Integer) value);
            } else if (value instanceof Double) {
                ps.setDouble(i + 1, (Double) value);
            } else {
                ps.setString(i + 1, String.valueOf(value));
            }
        }
    }

    public List<VariantImageDTO> getVariantImagesByVariantId(int variantId) {
        List<VariantImageDTO> images = new ArrayList<>();
        String sql = "SELECT id, variant_id, image_url, is_thumbnail FROM VariantImages WHERE variant_id = ? ORDER BY is_thumbnail DESC, id";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, variantId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                images.add(new VariantImageDTO(
                        rs.getInt("id"),
                        rs.getInt("variant_id"),
                        rs.getString("image_url"),
                        rs.getBoolean("is_thumbnail")));
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return images;
    }

    public List<VariantImageDTO> getVariantImagesByProductId(int productId) {
        List<VariantImageDTO> images = new ArrayList<>();
        String sql = "SELECT vi.id, vi.variant_id, vi.image_url, vi.is_thumbnail "
                + "FROM VariantImages vi "
                + "JOIN ProductVariants v ON vi.variant_id = v.id "
                + "WHERE v.product_id = ? "
                + "ORDER BY v.id, vi.is_thumbnail DESC, vi.id";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                images.add(new VariantImageDTO(
                        rs.getInt("id"),
                        rs.getInt("variant_id"),
                        rs.getString("image_url"),
                        rs.getBoolean("is_thumbnail")));
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return images;
    }

    public List<SizeDTO> getUniqueSizesByProductId(int productId) {
        List<SizeDTO> sizes = new ArrayList<>();
        String sql = "SELECT DISTINCT s.id, s.size_name "
                + "FROM ProductVariants v "
                + "JOIN Sizes s ON v.size_id = s.id "
                + "WHERE v.product_id = ? "
                + "ORDER BY s.id";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                sizes.add(new SizeDTO(
                        rs.getInt("id"),
                        rs.getString("size_name")));
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return sizes;
    }

    public List<ColorDTO> getUniqueColorsByProductId(int productId) {
        List<ColorDTO> colors = new ArrayList<>();
        String sql = "SELECT DISTINCT c.id, c.color_name "
                + "FROM ProductVariants v "
                + "JOIN Colors c ON v.color_id = c.id "
                + "WHERE v.product_id = ? "
                + "ORDER BY c.id";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                colors.add(new ColorDTO(
                        rs.getInt("id"),
                        rs.getString("color_name")));
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return colors;
    }

    public List<ShopVariantStockDTO> listVariantStocksByShop(int shopId, int offset, int limit) {
        List<ShopVariantStockDTO> rows = new ArrayList<>();
        String sql = "SELECT v.id AS variant_id, p.id AS product_id, p.name AS product_name, "
                + "COALESCE(NULLIF(LTRIM(RTRIM(v.image_url)), ''), p.thumbnail) AS variant_image_url, "
                + "s.size_name, c.color_name, v.price, v.stock "
                + "FROM ProductVariants v "
                + "JOIN Products p ON p.id = v.product_id "
                + "LEFT JOIN Sizes s ON s.id = v.size_id "
                + "LEFT JOIN Colors c ON c.id = v.color_id "
                + "WHERE p.status IN (0, 1) "
                + "ORDER BY p.name ASC, v.id ASC "
                + "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, offset);
            ps.setInt(2, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String pName = rs.getString("product_name");
                String size = rs.getString("size_name");
                String color = rs.getString("color_name");
                String variantTitle = pName;
                if (size != null && !size.trim().isEmpty()) {
                    variantTitle += " - Size " + size;
                }
                if (color != null && !color.trim().isEmpty()) {
                    variantTitle += " - Mau " + color;
                }

                rows.add(new ShopVariantStockDTO(
                        rs.getInt("variant_id"),
                        rs.getInt("product_id"),
                        pName,
                        variantTitle,
                        rs.getString("variant_image_url"),
                        size,
                        color,
                        rs.getLong("price"),
                        rs.getInt("stock")));
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return rows;
    }

    public List<ShopProductStockSummaryDTO> listProductStockSummariesByShop(int shopId, int offset, int limit) {
        return listProductStockSummariesByShop(shopId, null, offset, limit);
    }

    public List<ShopProductStockSummaryDTO> listProductStockSummariesByShop(int shopId, String keyword, int offset,
            int limit) {
        List<ShopProductStockSummaryDTO> rows = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT p.id AS product_id, p.name AS product_name, SUM(v.stock) AS total_stock ")
                .append("FROM ProductVariants v ")
                .append("JOIN Products p ON p.id = v.product_id ")
                .append("WHERE p.status IN (0, 1) ");

        List<Object> params = new ArrayList<>();
        String kw = keyword == null ? null : keyword.trim();
        if (kw != null && !kw.isEmpty()) {
            sql.append("AND p.name COLLATE Latin1_General_CI_AI LIKE ? ");
            params.add("%" + kw + "%");
        }

        sql.append("GROUP BY p.id, p.name ")
                .append("ORDER BY p.name ASC ")
                .append("OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        params.add(offset);
        params.add(limit);

        try {
            PreparedStatement ps = connection.prepareStatement(sql.toString());
            for (int i = 0; i < params.size(); i++) {
                Object p = params.get(i);
                if (p instanceof Integer) {
                    ps.setInt(i + 1, (Integer) p);
                } else {
                    ps.setString(i + 1, String.valueOf(p));
                }
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int productId = rs.getInt("product_id");
                rows.add(new ShopProductStockSummaryDTO(
                        productId,
                        rs.getString("product_name"),
                        rs.getInt("total_stock"),
                        listVariantStocksByShopAndProduct(shopId, productId)));
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return rows;
    }

    public int countProductsWithStockByShop(int shopId) {
        return countProductsWithStockByShop(shopId, null);
    }

    public int countProductsWithStockByShop(int shopId, String keyword) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(1) AS total ")
                .append("FROM ( ")
                .append("    SELECT v.product_id ")
                .append("    FROM ProductVariants v ")
                .append("    JOIN Products p ON p.id = v.product_id ")
                .append("    WHERE p.status IN (0, 1) ");

        List<String> params = new ArrayList<>();
        String kw = keyword == null ? null : keyword.trim();
        if (kw != null && !kw.isEmpty()) {
            sql.append("AND p.name COLLATE Latin1_General_CI_AI LIKE ? ");
            params.add("%" + kw + "%");
        }

        sql.append("    GROUP BY v.product_id ")
                .append(") x");
        try {
            PreparedStatement ps = connection.prepareStatement(sql.toString());
            for (int i = 0; i < params.size(); i++) {
                ps.setString(i + 1, params.get(i));
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

    public List<ShopVariantStockDTO> listVariantStocksByShopAndProduct(int shopId, int productId) {
        List<ShopVariantStockDTO> rows = new ArrayList<>();
        String sql = "SELECT v.id AS variant_id, p.id AS product_id, p.name AS product_name, "
                + "COALESCE(NULLIF(LTRIM(RTRIM(v.image_url)), ''), p.thumbnail) AS variant_image_url, "
                + "s.size_name, c.color_name, v.price, v.stock "
                + "FROM ProductVariants v "
                + "JOIN Products p ON p.id = v.product_id "
                + "LEFT JOIN Sizes s ON s.id = v.size_id "
                + "LEFT JOIN Colors c ON c.id = v.color_id "
                + "WHERE p.id = ? "
                + "ORDER BY v.id ASC";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String pName = rs.getString("product_name");
                String size = rs.getString("size_name");
                String color = rs.getString("color_name");
                String variantTitle = pName;
                if (size != null && !size.trim().isEmpty()) {
                    variantTitle += " - Size " + size;
                }
                if (color != null && !color.trim().isEmpty()) {
                    variantTitle += " - Mau " + color;
                }

                rows.add(new ShopVariantStockDTO(
                        rs.getInt("variant_id"),
                        rs.getInt("product_id"),
                        pName,
                        variantTitle,
                        rs.getString("variant_image_url"),
                        size,
                        color,
                        rs.getLong("price"),
                        rs.getInt("stock")));
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return rows;
    }

    public int countVariantStocksByShop(int shopId) {
        String sql = "SELECT COUNT(1) AS total FROM ProductVariants";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return 0;
    }

    public List<ShopVariantStockDTO> searchVariantStocksByProductName(int shopId, String productName) {
        List<ShopVariantStockDTO> variants = new ArrayList<>();
        String sql = "SELECT pv.id AS variantId, p.id AS productId, p.name AS productName, p.name AS commonProductName, "
                +
                "pv.image_url AS variantImageUrl, s.size_name AS sizeName, c.color_name AS colorName, pv.price AS price, "
                +
                "ISNULL(pv.stock, 0) AS currentStock " +
                "FROM ProductVariants pv " +
                "INNER JOIN Products p ON pv.product_id = p.id " +
                "INNER JOIN Sizes s ON pv.size_id = s.id " +
                "INNER JOIN Colors c ON pv.color_id = c.id " +
                "WHERE p.name COLLATE Latin1_General_CI_AI LIKE ? AND p.status IN (0, 1) " +
                "ORDER BY p.name, s.size_name, c.color_name";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, "%" + productName.trim() + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                variants.add(new ShopVariantStockDTO(
                        rs.getInt("variantId"),
                        rs.getInt("productId"),
                        rs.getString("commonProductName"),
                        rs.getString("productName"),
                        rs.getString("variantImageUrl"),
                        rs.getString("sizeName"),
                        rs.getString("colorName"),
                        rs.getLong("price"),
                        rs.getInt("currentStock")));
            }
            System.out.println("Search product name: '" + productName + "' - Found: " + variants.size() + " variants");
        } catch (SQLException e) {
            System.out.println("Error searching product by name: " + e.getMessage());
            e.printStackTrace();
        }
        return variants;
    }

    public boolean addVariantStock(int shopId, int variantId, int addQuantity) {
        if (addQuantity == 0) {
            return false;
        }

        String updateSql = "UPDATE ProductVariants SET stock = stock + ? WHERE id = ? AND stock + ? >= 0";
        try {
            PreparedStatement update = connection.prepareStatement(updateSql);
            update.setInt(1, addQuantity);
            update.setInt(2, variantId);
            update.setInt(3, addQuantity);
            int updated = update.executeUpdate();
            update.close();
            return updated > 0;
        } catch (SQLException e) {
            System.out.println(e);
        }
        return false;
    }

    public boolean isVariantUsedInOrders(int variantId) {
        String sql = "SELECT COUNT(1) AS total FROM OrderItems WHERE variant_id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, variantId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("total") > 0;
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return false;
    }

    public boolean deleteVariantForShop(int shopId, int variantId) {
        String deleteCartItemsSql = "DELETE FROM CartItems WHERE variant_id = ?";
        String deleteImagesSql = "DELETE FROM VariantImages WHERE variant_id = ?";
        String deleteVariantSql = "DELETE FROM ProductVariants WHERE id = ?";

        try {
            connection.setAutoCommit(false);
            try {
                try (PreparedStatement cartPs = connection.prepareStatement(deleteCartItemsSql)) {
                    cartPs.setInt(1, variantId);
                    cartPs.executeUpdate();
                }

                // VariantImages is optional in current schema; delete from it only when table
                // exists.
                if (hasTable("VariantImages")) {
                    try (PreparedStatement imgPs = connection.prepareStatement(deleteImagesSql)) {
                        imgPs.setInt(1, variantId);
                        imgPs.executeUpdate();
                    }
                }

                int deleted;
                try (PreparedStatement varPs = connection.prepareStatement(deleteVariantSql)) {
                    varPs.setInt(1, variantId);
                    deleted = varPs.executeUpdate();
                }

                connection.commit();
                return deleted > 0;
            } catch (SQLException ex) {
                connection.rollback();
                System.out.println(ex);
                return false;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }

        return false;
    }

    private boolean hasTable(String tableName) {
        String sql = "SELECT OBJECT_ID(?, 'U')";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, "dbo." + tableName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getObject(1) != null;
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return false;
    }

    private ProductDTO mapProduct(ResultSet rs) throws SQLException {
        return new ProductDTO(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getInt("category_id"),
                rs.getLong("base_price"),
                rs.getString("thumbnail"),
                rs.getInt("status"));
    }

    private String normalizeLikePattern(String keyword) {
        if (keyword == null) {
            return null;
        }
        String cleaned = keyword.trim().replaceAll("[\\p{Punct}]+", " ");
        cleaned = cleaned.replaceAll("\\s+", " ").trim();
        if (cleaned.isEmpty()) {
            return null;
        }
        String[] parts = cleaned.split(" ");
        return "%" + String.join("%", parts) + "%";
    }

    /**
     * Split keyword into normalized tokens (lowercase, punctuation removed).
     * Example: "áo khoác nam" -> ["áo","khoác","nam"]
     */
    private List<String> splitSearchTokens(String keyword) {
        if (keyword == null) {
            return null;
        }
        String cleaned = keyword.trim().replaceAll("[\\p{Punct}]+", " ");
        cleaned = cleaned.replaceAll("\\s+", " ").trim();
        if (cleaned.isEmpty()) {
            return null;
        }
        String[] parts = cleaned.split(" ");
        List<String> tokens = new ArrayList<>();
        for (String p : parts) {
            if (p != null && !p.trim().isEmpty()) {
                tokens.add(p.trim());
            }
        }
        return tokens;
    }

    public boolean isProductNameExists(String name) {
    /* kiem tra neu ten san pham da ton tai khong co id loai tru */
        return isProductNameExists(name, -1);
    }

    public boolean isProductNameExists(String name, int excludeProductId) {
        String sql = "SELECT COUNT(1) AS total FROM Products WHERE name = ?";
        if (excludeProductId > 0) {
            sql += " AND id <> ?";
        }
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, name);
            if (excludeProductId > 0) {
                ps.setInt(2, excludeProductId);
            }
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("total") > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error checking product name: " + e.getMessage());
        }
        return false;
    }

}
