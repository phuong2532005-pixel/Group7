/**
 *
 * Author : Nguyen Trung Hieu & Nguyen Thi Phuong Linh
 * Date : 23/02/2026
 * Description : Liet ke san pham theo bo loc/ tim kiem voi phan trang.
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

@WebServlet(name = "ProductListServlet", urlPatterns = {"/products"})
public class ProductListServlet extends HttpServlet {

    private static final int DEFAULT_PAGE = 1;
    private static final int PAGE_SIZE = 12;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String keyword = trimToNull(request.getParameter("q"));
        Integer categoryId = parseInt(request.getParameter("cate"));
        String categoryName = trimToNull(request.getParameter("catName"));
        Double minPrice = parseDouble(request.getParameter("min"));
        Double maxPrice = parseDouble(request.getParameter("max"));
        int page = parsePage(request.getParameter("page"));

        int offset = (page - 1) * PAGE_SIZE;

        Integer shopIdFilter = null;

        if (categoryId == null && categoryName != null) {
            Integer resolved = new CategoryDAO().getCategoryIdByNameInsensitive(categoryName);
            if (resolved != null) {
                categoryId = resolved;
            }
        }

        ProductDAO dao = new ProductDAO();
        List<ProductDTO> products = dao.searchProducts(keyword, categoryId, minPrice, maxPrice, shopIdFilter, offset, PAGE_SIZE);
        int total = dao.countProducts(keyword, categoryId, minPrice, maxPrice, shopIdFilter);
        int totalPages = (int) Math.ceil(total / (double) PAGE_SIZE);
        if (totalPages == 0) {
            totalPages = 1;
        }

        CategoryDAO categoryDAO = new CategoryDAO();
        List<CategoryDTO> categories = buildCategoryTree(categoryDAO.getAllCategories());

        request.setAttribute("products", products);
        request.setAttribute("categories", categories);
        request.setAttribute("page", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("q", keyword);
        request.setAttribute("cate", categoryId);
        request.setAttribute("catName", categoryName);
        request.setAttribute("min", minPrice);
        request.setAttribute("max", maxPrice);
        request.getRequestDispatcher("/jsp/ProductList.jsp").forward(request, response);
    }

    private List<CategoryDTO> buildCategoryTree(List<CategoryDTO> input) {
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
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private Integer parseInt(String value) {
        try {
            return value == null || value.trim().isEmpty() ? null : Integer.valueOf(value.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private Double parseDouble(String value) {
        try {
            return value == null || value.trim().isEmpty() ? null : Double.valueOf(value.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private int parsePage(String value) {
        try {
            int page = Integer.parseInt(value);
            return page < 1 ? DEFAULT_PAGE : page;
        } catch (NumberFormatException ex) {
            return DEFAULT_PAGE;
        }
    }
}
