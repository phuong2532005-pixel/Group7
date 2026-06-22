/**
 *
 * Author : Nguyen Trung Hieu & Nguyen Thi Phuong Linh
 * Date : 01/02/2026
 * Description : Quan ly san pham (them/sua/xoa/hien thi) cho admin.
 */
package controller;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import DAO.CategoryDAO;
import model.CategoryDTO;
import DAO.ColorDAO;
import DAO.ProductDAO;
import model.ProductDTO;
import model.ProductVariantDTO;
import model.ShopProductStockSummaryDTO;
import DAO.SizeDAO;
import model.UsersDTO;

@WebServlet(name = "AdminProductsServlet", urlPatterns = {"/admin/products"})
@MultipartConfig
public class AdminProductsServlet extends HttpServlet {

    private static final int PAGE_SIZE = 10;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    /* hien thi trang quan ly san pham, tim kiem va load form khi can */
        UsersDTO acc = getCurrentUser(request);
        if (acc == null || acc.getRole_id() != 4) {
            response.sendRedirect(request.getContextPath() + "/dispatchcontroller?button=LoginPage");
            return;
        }

        String action = trimToNull(request.getParameter("action"));
        if ("checkProductName".equalsIgnoreCase(action)) {
            handleCheckProductName(request, response);
            return;
        }

        String mode = trimToNull(request.getParameter("mode"));
        if ("add".equalsIgnoreCase(mode)) {
            loadFormData(request);
            request.getRequestDispatcher("/jsp/shopProductFormNew.jsp").forward(request, response);
            return;
        }

        ProductDAO productDAO = new ProductDAO();
        String keyword = trimToNull(request.getParameter("keyword"));
        int page = parsePositiveInt(request.getParameter("page"), 1);
        int total = productDAO.countProductsWithStockByShop(0, keyword);
        int totalPages = Math.max(1, (int) Math.ceil(total / (double) PAGE_SIZE));
        if (page > totalPages) {
            page = totalPages;
        }
        int offset = (page - 1) * PAGE_SIZE;

        List<ShopProductStockSummaryDTO> productStocks = productDAO.listProductStockSummariesByShop(0, keyword, offset, PAGE_SIZE);
        Map<Integer, ProductDTO> productDetails = new HashMap<>();
        for (ShopProductStockSummaryDTO s : productStocks) {
            ProductDTO p = productDAO.getProductById(s.getProductId());
            if (p != null) {
                productDetails.put(s.getProductId(), p);
            }
        }
        request.setAttribute("productStocks", productStocks);
        request.setAttribute("productDetails", productDetails);
        request.setAttribute("page", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("keyword", keyword == null ? "" : keyword);
        loadFormData(request);

        HttpSession session = request.getSession(false);
        if (session != null) {
            Object msg = session.getAttribute("adminProductMessage");
            Object err = session.getAttribute("adminProductError");
            if (msg != null) {
                request.setAttribute("message", msg);
                session.removeAttribute("adminProductMessage");
            }
            if (err != null) {
                request.setAttribute("error", err);
                session.removeAttribute("adminProductError");
            }
        }
        request.getRequestDispatcher("/jsp/shopProducts.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    /* xu ly cac hanh dong POST cho admin products (them, cap nhat, xoa, ...) */
        UsersDTO acc = getCurrentUser(request);
        if (acc == null || acc.getRole_id() != 4) {
            response.sendRedirect(request.getContextPath() + "/dispatchcontroller?button=LoginPage");
            return;
        }

        String action = trimToNull(request.getParameter("action"));
        if (action == null) {
            response.sendRedirect(request.getContextPath() + "/admin/products");
            return;
        }

        switch (action) {
            case "addCategory":
                handleAddCategory(request, response);
                return;
            case "addSize":
                handleAddSize(request, response);
                return;
            case "addColor":
                handleAddColor(request, response);
                return;
            case "createProduct":
                handleCreateProduct(request, response);
                return;
            case "addVariant":
                handleAddVariant(request, response);
                return;
            case "addStock":
                handleAddStock(request, response);
                return;
            case "updateProduct":
                handleUpdateProduct(request, response);
                return;
            case "updateVariant":
                handleUpdateVariant(request, response);
                return;
            case "deleteVariant":
                handleDeleteVariant(request, response);
                return;
            case "deleteProduct":
                handleDeleteProduct(request, response);
                return;
            default:
                response.sendRedirect(request.getContextPath() + "/admin/products");
        }
    }

    private void handleCreateProduct(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
    /* xu ly tao san pham moi va bien the kem theo anh va ton kho */
        ProductDAO productDAO = new ProductDAO();
        String name = trimToNull(request.getParameter("productName"));
        String description = request.getParameter("description");
        int categoryId = parsePositiveInt(request.getParameter("categoryId"), -1);
        long basePrice = parsePositiveLong(request.getParameter("basePrice"), -1);

        String[] sizeIds = request.getParameterValues("variantSize");
        String[] colorIds = request.getParameterValues("variantColor");
        String[] prices = request.getParameterValues("variantPrice");
        String[] initialStocks = request.getParameterValues("initialStock");
        List<Part> variantImageParts = getPartsByName(request, "variantImage");

        if (name == null || categoryId <= 0 || basePrice < 0 || sizeIds == null || sizeIds.length == 0) {
            setError(request, "Dữ liệu sản phẩm không hợp lệ.");
            response.sendRedirect(request.getContextPath() + "/admin/products?mode=add");
            return;
        }

        if (productDAO.isProductNameExists(name)) {
            request.setAttribute("nameError", "❌ Tên sản phẩm '" + name + "' đã tồn tại trong hệ thống! Vui lòng chọn tên khác.");
            request.setAttribute("productName", name);
            request.setAttribute("description", description);
            request.setAttribute("categoryId", categoryId);
            request.setAttribute("basePrice", basePrice);
            loadFormData(request);
            request.getRequestDispatcher("/jsp/shopProductFormNew.jsp").forward(request, response);
            return;
        }

        String thumbnailPath = saveImageAndReturnPath(request.getPart("thumbnail"));
        int productId = productDAO.createProduct(name, description, categoryId, basePrice, thumbnailPath, 1);
        if (productId <= 0) {
            setError(request, "Tạo sản phẩm thất bại.");
            response.sendRedirect(request.getContextPath() + "/admin/products?mode=add");
            return;
        }

        for (int i = 0; i < sizeIds.length; i++) {
            int sizeId = parsePositiveInt(sizeIds[i], -1);
            int colorId = (colorIds != null && i < colorIds.length) ? parsePositiveInt(colorIds[i], -1) : -1;
            long price = (prices != null && i < prices.length) ? parsePositiveLong(prices[i], 0) : 0;
            int initialStock = (initialStocks != null && i < initialStocks.length) ? parseInt(initialStocks[i], 0) : 0;
            String variantImage = null;
            if (i < variantImageParts.size()) {
                variantImage = saveImageAndReturnPath(variantImageParts.get(i));
            }

            if (sizeId > 0 && colorId > 0 && price >= 0) {
                int variantId = productDAO.createVariant(productId, sizeId, colorId, price, variantImage);
                if (variantId > 0 && initialStock > 0) {
                    productDAO.addVariantStock(0, variantId, initialStock);
                }
            }
        }

        setMessage(request, "Đã tạo sản phẩm và biến thể thành công.");
        response.sendRedirect(request.getContextPath() + "/admin/products");
    }

    private void handleCheckProductName(HttpServletRequest request, HttpServletResponse response) throws IOException {
    /* kiem tra ten san pham da ton tai (tra json) */
        String name = trimToNull(request.getParameter("productName"));
        int productId = parsePositiveInt(request.getParameter("productId"), -1);
        boolean exists = false;
        if (name != null && !name.trim().isEmpty()) {
            exists = new ProductDAO().isProductNameExists(name, productId);
        }
        response.setContentType("application/json;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.write("{\"exists\":" + exists + "}");
        }
    }

    private void handleAddVariant(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
    /* xu ly them mot variant cho san pham va cap nhat ton kho */
        ProductDAO dao = new ProductDAO();
        int productId = parsePositiveInt(request.getParameter("productId"), -1);
        int sizeId = parsePositiveInt(request.getParameter("sizeId"), -1);
        int colorId = parsePositiveInt(request.getParameter("colorId"), -1);
        long price = parsePositiveLong(request.getParameter("price"), -1);
        int stock = parseInt(request.getParameter("initialStock"), 0);

        if (productId <= 0 || sizeId <= 0 || colorId <= 0 || price < 0) {
            setError(request, "Biến thể không hợp lệ.");
            response.sendRedirect(request.getContextPath() + "/admin/products");
            return;
        }

        String image = saveImageAndReturnPath(request.getPart("variantImage"));
        int variantId = dao.createVariant(productId, sizeId, colorId, price, image);
        if (variantId > 0 && stock > 0) {
            dao.addVariantStock(0, variantId, stock);
        }
        if (variantId > 0) {
            setMessage(request, "Đã thêm biến thể thành công.");
        } else {
            setError(request, "Thêm biến thể thất bại.");
        }
        response.sendRedirect(request.getContextPath() + "/admin/products?page=" + parsePositiveInt(request.getParameter("page"), 1));
    }

    private void handleAddStock(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
    /* cap nhat so luong ton cho mot variant */
        ProductDAO dao = new ProductDAO();
        int variantId = parsePositiveInt(request.getParameter("variantId"), -1);
        int addQty = parseInt(request.getParameter("addQuantity"), 0);
        if (variantId <= 0 || addQty == 0) {
            setError(request, "Số lượng cập nhật không hợp lệ.");
            response.sendRedirect(request.getContextPath() + "/admin/products");
            return;
        }
        if (dao.addVariantStock(0, variantId, addQty)) {
            setMessage(request, "Đã cập nhật tồn kho biến thể.");
        } else {
            setError(request, "Không thể cập nhật tồn kho.");
        }
        response.sendRedirect(request.getContextPath() + "/admin/products?page=" + parsePositiveInt(request.getParameter("page"), 1));
    }

    private void handleUpdateProduct(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
    /* xu ly cap nhat thong tin san pham va thumbnail */
        ProductDAO dao = new ProductDAO();
        int productId = parsePositiveInt(request.getParameter("productId"), -1);
        String name = trimToNull(request.getParameter("productName"));
        String description = request.getParameter("description");
        int categoryId = parsePositiveInt(request.getParameter("categoryId"), -1);
        long basePrice = parsePositiveLong(request.getParameter("basePrice"), -1);
        int status = parseInt(request.getParameter("status"), 1);
        if (status != 0 && status != 1) {
            status = 1;
        }
        String oldThumbnail = request.getParameter("existingThumbnail");

        if (productId <= 0 || name == null || categoryId <= 0 || basePrice < 0) {
            setError(request, "Dữ liệu cập nhật sản phẩm không hợp lệ.");
            response.sendRedirect(request.getContextPath() + "/admin/products");
            return;
        }

        // Kiểm tra trùng tên sản phẩm (ngoại trừ chính sản phẩm hiện tại)
        if (dao.isProductNameExists(name, productId)) {
            setError(request, "❌ Tên sản phẩm '" + name + "' đã tồn tại trong hệ thống! Vui lòng chọn tên khác.");
            response.sendRedirect(request.getContextPath() + "/admin/products?page=" + parsePositiveInt(request.getParameter("page"), 1));
            return;
        }

        String thumbnail = saveImageAndReturnPath(request.getPart("thumbnail"));
        if (thumbnail == null || thumbnail.trim().isEmpty()) {
            thumbnail = oldThumbnail;
        }

        boolean ok = dao.updateProduct(productId, name, description, categoryId, basePrice, thumbnail, status);
        if (ok) {
            setMessage(request, "Đã cập nhật sản phẩm.");
        } else {
            setError(request, "Cập nhật sản phẩm thất bại.");
        }
        response.sendRedirect(request.getContextPath() + "/admin/products?page=" + parsePositiveInt(request.getParameter("page"), 1));
    }

    private void handleUpdateVariant(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
    /* xu ly cap nhat thong tin variant va anh */
        ProductDAO dao = new ProductDAO();
        int variantId = parsePositiveInt(request.getParameter("variantId"), -1);
        int sizeId = parsePositiveInt(request.getParameter("sizeId"), -1);
        int colorId = parsePositiveInt(request.getParameter("colorId"), -1);
        long price = parsePositiveLong(request.getParameter("price"), -1);
        String oldImage = request.getParameter("existingImage");

        if (variantId <= 0 || sizeId <= 0 || colorId <= 0 || price < 0) {
            setError(request, "Dữ liệu cập nhật biến thể không hợp lệ.");
            response.sendRedirect(request.getContextPath() + "/admin/products");
            return;
        }

        String image = saveImageAndReturnPath(request.getPart("variantImage"));
        if (image == null || image.trim().isEmpty()) {
            image = oldImage;
        }

        boolean ok = dao.updateVariant(variantId, sizeId, colorId, price, image);
        if (ok) {
            setMessage(request, "Đã cập nhật biến thể.");
        } else {
            setError(request, "Cập nhật biến thể thất bại.");
        }
        response.sendRedirect(request.getContextPath() + "/admin/products?page=" + parsePositiveInt(request.getParameter("page"), 1));
    }

    private void handleDeleteVariant(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
    /* xu ly xoa mot variant (kiem tra neu da co don hang) */
        ProductDAO dao = new ProductDAO();
        int variantId = parsePositiveInt(request.getParameter("variantId"), -1);
        if (variantId <= 0) {
            setError(request, "Biến thể không hợp lệ.");
            response.sendRedirect(request.getContextPath() + "/admin/products");
            return;
        }

        if (dao.isVariantUsedInOrders(variantId)) {
            setError(request, "Biến thể đã phát sinh đơn hàng, không thể xóa.");
        } else if (dao.deleteVariantForShop(0, variantId)) {
            setMessage(request, "Đã xóa biến thể.");
        } else {
            setError(request, "Xóa biến thể thất bại.");
        }
        response.sendRedirect(request.getContextPath() + "/admin/products?page=" + parsePositiveInt(request.getParameter("page"), 1));
    }

    private void handleDeleteProduct(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
    /* xu ly xoa mot san pham neu khong bi su dung tren don hang */
        ProductDAO dao = new ProductDAO();
        int productId = parsePositiveInt(request.getParameter("productId"), -1);
        if (productId <= 0) {
            setError(request, "Sản phẩm không hợp lệ.");
            response.sendRedirect(request.getContextPath() + "/admin/products");
            return;
        }

        if (dao.isProductUsedInOrders(productId)) {
            setError(request, "Sản phẩm đã phát sinh đơn hàng, không thể xóa.");
            response.sendRedirect(request.getContextPath() + "/admin/products");
            return;
        }

        List<ProductVariantDTO> variants = dao.getVariantsByProductId(productId);
        boolean ok = true;
        for (ProductVariantDTO v : variants) {
            ok = ok && dao.deleteVariantForShop(0, v.getId());
        }
        if (ok) {
            ok = dao.deleteProduct(productId);
        }

        if (ok) {
            setMessage(request, "Đã xóa sản phẩm.");
        } else {
            setError(request, "Xóa sản phẩm thất bại.");
        }
        response.sendRedirect(request.getContextPath() + "/admin/products?page=" + parsePositiveInt(request.getParameter("page"), 1));
    }

    private void handleAddCategory(HttpServletRequest request, HttpServletResponse response) throws IOException {
    /* them category nhanh tu form admin va tra ve json */
        CategoryDAO dao = new CategoryDAO();
        String name = trimToNull(request.getParameter("categoryName"));
        Integer id = dao.getCategoryIdByNameInsensitive(name);
        if (id == null) {
            id = dao.createCategory(name, null);
        }
        writeJson(response, id != null, "categoryId", id);
    }

    private void handleAddSize(HttpServletRequest request, HttpServletResponse response) throws IOException {
    /* them kich co nhanh tu form admin va tra ve json */
        SizeDAO dao = new SizeDAO();
        String name = trimToNull(request.getParameter("sizeName"));
        Integer id = dao.getSizeIdByName(name);
        if (id == null) {
            id = dao.createSize(name);
        }
        writeJson(response, id != null, "sizeId", id);
    }

    private void handleAddColor(HttpServletRequest request, HttpServletResponse response) throws IOException {
    /* them mau nhanh tu form admin va tra ve json */
        ColorDAO dao = new ColorDAO();
        String name = trimToNull(request.getParameter("colorName"));
        Integer id = dao.getColorIdByName(name);
        if (id == null) {
            id = dao.createColor(name);
        }
        writeJson(response, id != null, "colorId", id);
    }

    private void loadFormData(HttpServletRequest request) {
    /* nap du lieu can thiet cho form (categories, sizes, colors) */
        request.setAttribute("categories", new CategoryDAO().getAllCategories());
        request.setAttribute("allSizes", new SizeDAO().getAllSizes());
        request.setAttribute("allColors", new ColorDAO().getAllColors());
    }

    private UsersDTO getCurrentUser(HttpServletRequest request) {
    /* lay user dang dang nhap tu session neu co */
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        Object acc = session.getAttribute("acc");
        if (acc instanceof UsersDTO) {
            return (UsersDTO) acc;
        }
        return null;
    }

    private int parsePositiveInt(String raw, int defaultValue) {
    /* chuyen chuoi sang int neu duong, nguoc lai tra default */
        try {
            int v = Integer.parseInt(raw);
            return v > 0 ? v : defaultValue;
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private int parseInt(String raw, int defaultValue) {
    /* chuyen chuoi sang int neu hop le, neu sai tra default */
        try {
            return Integer.parseInt(raw);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private long parsePositiveLong(String raw, long defaultValue) {
    /* chuyen chuoi sang long neu hop le va khong am, nguoc lai tra default */
        try {
            if (raw == null) {
                return defaultValue;
            }
            String value = raw.trim();
            if (value.isEmpty()) {
                return defaultValue;
            }

            try {
                long v = Long.parseLong(value);
                return v >= 0 ? v : defaultValue;
            } catch (NumberFormatException ex) {
                // Accept decimal inputs from HTML number fields (e.g. 0.00) and convert to VND integer.
                double dv = Double.parseDouble(value);
                if (dv < 0) {
                    return defaultValue;
                }
                return Math.round(dv);
            }
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private String trimToNull(String value) {
    /* cat bo khoang trang va tra ve null neu rong */
        if (value == null) {
            return null;
        }
        String t = value.trim();
        return t.isEmpty() ? null : t;
    }

    private void setMessage(HttpServletRequest request, String msg) {
    /* dat thong diep thanh cong vao session de hien thi sau */
        request.getSession().setAttribute("adminProductMessage", msg);
    }

    private void setError(HttpServletRequest request, String msg) {
    /* dat thong diep loi vao session de hien thi sau */
        request.getSession().setAttribute("adminProductError", msg);
    }

    private List<Part> getPartsByName(HttpServletRequest request, String name) throws IOException, ServletException {
    /* lay cac Part cua ten field cho truong hop upload nhieu file */
        List<Part> list = new ArrayList<>();
        Collection<Part> parts = request.getParts();
        for (Part p : parts) {
            if (name.equals(p.getName())) {
                list.add(p);
            }
        }
        return list;
    }

    private String saveImageAndReturnPath(Part part) throws IOException {
    /* luu file upload vao thu muc img/products va tra ve duong dan relative */
        if (part == null) {
            return null;
        }
        String original = part.getSubmittedFileName();
        if (original == null || original.trim().isEmpty() || part.getSize() <= 0) {
            return null;
        }

        String clean = Paths.get(original).getFileName().toString();
        int dot = clean.lastIndexOf('.');
        String ext = dot >= 0 ? clean.substring(dot).toLowerCase() : "";
        String fileName = UUID.randomUUID().toString().replace("-", "") + ext;

        String uploadDir = getServletContext().getRealPath("/img/products");
        if (uploadDir == null || uploadDir.trim().isEmpty()) {
            throw new IOException("Không xác định được thư mục deploy /img/products");
        }

        byte[] data;
        try (java.io.InputStream is = part.getInputStream()) {
            java.io.ByteArrayOutputStream buffer = new java.io.ByteArrayOutputStream();
            int nRead;
            byte[] temp = new byte[4096];
            while ((nRead = is.read(temp, 0, temp.length)) != -1) {
                buffer.write(temp, 0, nRead);
            }
            data = buffer.toByteArray();
        }

        Path deployedDir = Paths.get(uploadDir);
        Files.createDirectories(deployedDir);
        Files.write(deployedDir.resolve(fileName), data);

        // Trong môi trường NetBeans/Ant, đồng bộ thêm về source web/img/products
        // để bạn có thể thấy file ngay trong project.
        Path sourceDir = resolveProjectWebProductsDir();
        if (sourceDir != null) {
            Files.createDirectories(sourceDir);
            Files.write(sourceDir.resolve(fileName), data);
        }

        return "img/products/" + fileName;
    }

    private Path resolveProjectWebProductsDir() {
    /* tim duong dan source web/img/products trong project de dong bo file */
        String webRoot = getServletContext().getRealPath("/");
        if (webRoot == null || webRoot.trim().isEmpty()) {
            return null;
        }

        Path root = Paths.get(webRoot).normalize();
        String rootStr = root.toString().replace('\\', '/');
        String marker = "/build/web";
        int idx = rootStr.lastIndexOf(marker);
        if (idx < 0) {
            return null;
        }

        String projectRoot = rootStr.substring(0, idx);
        return Paths.get(projectRoot, "web", "img", "products");
    }

    private void writeJson(HttpServletResponse response, boolean success, String idField, Integer idValue) throws IOException {
    /* ghi json don gian vao response cho cac thao tac AJAX */
        response.setContentType("application/json;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            if (success && idValue != null) {
                out.write("{\"success\":true,\"" + idField + "\":" + idValue + "}");
            } else {
                out.write("{\"success\":false,\"error\":\"Thao tác thất bại\"}");
            }
        }
    }
}
