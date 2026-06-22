/**
 *
 * Author : Nguyen Trung Hieu & Nguyen Thi Phuong Linh
 * Date : 15/02/2026
 * Description : Chuan bi du lieu cho trang chu (san pham noi bat, theo danh muc).
 */
package controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import DAO.CategoryDAO;
import model.CategoryDTO;
import DAO.ProductDAO;
import model.ProductDTO;

@WebServlet(name = "HomeServlet", urlPatterns = {"/home"})
public class HomeServlet extends HttpServlet {
    private static final int SECTION_LIMIT = 8;
    private static final int PRODUCT_PAGE_SIZE = 12;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        /* chuan bi du lieu va hien thi trang chu */
        ProductDAO dao = new ProductDAO();
        List<ProductDTO> listBest = dao.getBestSellingProducts(SECTION_LIMIT);
        List<ProductDTO> listNam = dao.getProductsByTopCategoryName("NAM", SECTION_LIMIT);
        List<ProductDTO> listNu = dao.getProductsByTopCategoryName("NỮ", SECTION_LIMIT);
        List<ProductDTO> listUnisex = dao.getProductsByTopCategoryName("UNISEX", SECTION_LIMIT);

        String keyword = trimToNull(request.getParameter("q"));
        Integer categoryId = parseInt(request.getParameter("cate"));
        String categoryName = trimToNull(request.getParameter("catName"));
        Double minPrice = parseDouble(request.getParameter("min"));
        Double maxPrice = parseDouble(request.getParameter("max"));
        int page = parsePage(request.getParameter("page"));
        int offset = (page - 1) * PRODUCT_PAGE_SIZE;

        Integer shopIdFilter = null;

        if (categoryId == null && categoryName != null) {
            Integer resolved = new CategoryDAO().getCategoryIdByNameInsensitive(categoryName);
            if (resolved != null) {
                categoryId = resolved;
            }
        }

        List<ProductDTO> products = dao.searchProducts(keyword, categoryId, minPrice, maxPrice, shopIdFilter, offset, PRODUCT_PAGE_SIZE);
        int total = dao.countProducts(keyword, categoryId, minPrice, maxPrice, shopIdFilter);
        int totalPages = (int) Math.ceil(total / (double) PRODUCT_PAGE_SIZE);
        if (totalPages == 0) {
            totalPages = 1;
        }

        CategoryDAO categoryDAO = new CategoryDAO();
        List<CategoryDTO> categories = buildCategoryTree(categoryDAO.getAllCategories());

        request.setAttribute("listBest", listBest);
        request.setAttribute("listNam", listNam);
        request.setAttribute("listNu", listNu);
        request.setAttribute("listUnisex", listUnisex);
        request.setAttribute("products", products);
        request.setAttribute("categories", categories);
        request.setAttribute("page", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("q", keyword);
        request.setAttribute("cate", categoryId);
        request.setAttribute("catName", categoryName);
        request.setAttribute("min", minPrice);
        request.setAttribute("max", maxPrice);
        request.getRequestDispatcher("/jsp/HomePage.jsp").forward(request, response);
    }

    private List<CategoryDTO> buildCategoryTree(List<CategoryDTO> input) {
        /* xay dung cay danh muc co thu tu de hien thi */
        Map<Integer, List<CategoryDTO>> byParent = new HashMap<>();
        for (CategoryDTO cat : input) {
            Integer parentId = cat.getParentId();
            byParent.computeIfAbsent(parentId, key -> new ArrayList<>()).add(cat);
        }

        List<CategoryDTO> ordered = new ArrayList<>();
        appendChildren(null, 0, byParent, ordered);
        return ordered;
    }

    private void appendChildren(Integer parentId, int level, Map<Integer, List<CategoryDTO>> byParent,
            List<CategoryDTO> ordered) {
        /* them cac danh muc con vao danh sach co dang de hien thi */
        List<CategoryDTO> children = byParent.get(parentId);
        if (children == null) {
            return;
        }
        for (CategoryDTO child : children) {
            ordered.add(new CategoryDTO(child.getId(), child.getName(), child.getParentId(), level));
            appendChildren(child.getId(), level + 1, byParent, ordered);
        }
    }

    private String trimToNull(String value) {
        /* cat khoang trang va tra ve null neu chuoi rong */
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private Integer parseInt(String value) {
        /* chuyen chuoi sang Integer hoac tra ve null neu khong hop le */
        try {
            return value == null || value.trim().isEmpty() ? null : Integer.valueOf(value.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private Double parseDouble(String value) {
        /* chuyen chuoi sang Double hoac tra ve null neu khong hop le */
        try {
            return value == null || value.trim().isEmpty() ? null : Double.valueOf(value.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private int parsePage(String value) {
        /* chuyen tham so trang sang so nguyen hop le */
        try {
            int page = Integer.parseInt(value);
            return page < 1 ? 1 : page;
        } catch (NumberFormatException ex) {
            return 1;
        }
    }
}
