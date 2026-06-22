/**
 *
 * Author : Nguyen Trung Hieu & Nguyen Thi Phuong Linh
 * Date : 29/01/2026
 * Description : Quan ly cac danh muc san pham (CRUD) cho phan admin.
 */
package controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import DAO.CategoryDAO;
import model.CategoryDTO;
import model.UsersDTO;

@WebServlet(name = "AdminCategoriesServlet", urlPatterns = {"/admin/categories"})
public class AdminCategoriesServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    /* hien thi danh sach category va xu ly cac yeu cau GET (check name) */
        UsersDTO acc = (UsersDTO) request.getSession().getAttribute("acc");
        // Chỉ giám đốc (role=4) mới được truy cập
        if (acc == null || acc.getRole_id() != 4) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        String action = request.getParameter("action");
        if ("checkCategoryName".equalsIgnoreCase(action)) {
            handleCheckCategoryName(request, response);
            return;
        }

        CategoryDAO dao = new CategoryDAO();
        List<CategoryDTO> categories = dao.getAllCategories();
        request.setAttribute("categories", categories);

        String message = (String) request.getSession().getAttribute("adminCategoryMessage");
        if (message != null) {
            request.setAttribute("adminCategoryMessage", message);
            request.getSession().removeAttribute("adminCategoryMessage");
        }

        request.getRequestDispatcher("/jsp/adminCategories.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    /* xu ly cac hanh dong POST tao/sua/xoa category va chuyen huong */
        UsersDTO acc = (UsersDTO) request.getSession().getAttribute("acc");
        // Chỉ giám đốc (role=4) mới được truy cập
        if (acc == null || acc.getRole_id() != 4) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        String action = request.getParameter("action");
        String message = null;

        CategoryDAO dao = new CategoryDAO();

        if ("create".equals(action)) {
            String name = request.getParameter("name");
            Integer parentId = parseIntegerOrNull(request.getParameter("parentId"));

            // Kiểm tra trùng tên category (case-insensitive) với cùng parentId
            Integer existingId = dao.getCategoryIdByNameAndParentInsensitive(name, parentId);
            if (existingId != null) {
                message = "Tên danh mục đã tồn tại trong cùng danh mục cha. Vui lòng chọn tên khác.";
            } else {
                Integer created = dao.createCategory(name, parentId);
                if (created != null) {
                    message = "Đã tạo category thành công.";
                } else {
                    message = "Tạo category thất bại. Vui lòng kiểm tra tên và thử lại.";
                }
            }
        } else if ("update".equals(action)) {
            Integer id = parseIntegerOrNull(request.getParameter("categoryId"));
            String name = request.getParameter("name");
            Integer parentId = parseIntegerOrNull(request.getParameter("parentId"));
            if (id == null) {
                message = "ID category không hợp lệ.";
            } else {
                Integer existingId = dao.getCategoryIdByNameAndParentInsensitive(name, parentId);
                if (existingId != null && !existingId.equals(id)) {
                    message = "Tên danh mục đã tồn tại trong cùng danh mục cha. Vui lòng chọn tên khác.";
                } else {
                    boolean ok = dao.updateCategory(id, name, parentId);
                    message = ok ? "Cập nhật category thành công." : "Cập nhật category thất bại.";
                }
            }
        } else if ("delete".equals(action)) {
            Integer id = parseIntegerOrNull(request.getParameter("categoryId"));
            if (id == null) {
                message = "ID category không hợp lệ.";
            } else if (dao.hasChildCategories(id)) {
                message = "Không thể xóa category vì đang có danh mục con tham chiếu.";
            } else if (dao.hasProducts(id)) {
                message = "Không thể xóa category vì đang có sản phẩm thuộc danh mục này.";
            } else {
                boolean ok = dao.deleteCategory(id);
                message = ok ? "Đã xóa category." : "Xóa category thất bại. Vui lòng thử lại.";
            }
        }

        if (message != null) {
            request.getSession().setAttribute("adminCategoryMessage", message);
        }

        response.sendRedirect(request.getContextPath() + "/dispatchcontroller?button=AdminCategories");
    }

    private Integer parseIntegerOrNull(String value) {
    /* chuyen chuoi sang Integer hoac tra ve null neu khong hop le */
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Integer.valueOf(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void handleCheckCategoryName(HttpServletRequest request, HttpServletResponse response) throws IOException {
    /* kiem tra ten category da ton tai trong cung parent chua va tra ve json */
        String name = request.getParameter("name");
        Integer categoryId = parseIntegerOrNull(request.getParameter("categoryId"));
        boolean exists = false;
        Integer parentId = parseIntegerOrNull(request.getParameter("parentId"));
        if (name != null && !name.trim().isEmpty()) {
            Integer existingId = new CategoryDAO().getCategoryIdByNameAndParentInsensitive(name, parentId);
            if (existingId != null) {
                if (categoryId == null || !existingId.equals(categoryId)) {
                    exists = true;
                }
            }
        }
        response.setContentType("application/json;charset=UTF-8");
        try (java.io.PrintWriter out = response.getWriter()) {
            out.write("{\"exists\":" + exists + "}");
        }
    }
}
