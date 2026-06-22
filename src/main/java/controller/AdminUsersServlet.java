/**
 *
 * Author : Nguyen Trung Hieu & Nguyen Thi Phuong Linh
 * Date : 03/02/2026
 * Description : Quan ly nguoi dung boi admin (danh sach, khoa/mo, sua quyen).
 */
package controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import DAO.SystemLogDAO;
import DAO.UsersDAO;
import model.UsersDTO;
import util.HashUtil;

@WebServlet(name = "AdminUsersServlet", urlPatterns = {"/admin/users"})
public class AdminUsersServlet extends HttpServlet {

    private static final int PAGE_SIZE = 20;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    /* hien thi danh sach nguoi dung cho admin voi loc va phan trang */
        Integer roleFilter = parseRole(request.getParameter("role"));
        Integer statusFilter = parseStatus(request.getParameter("status"));
        int page = parsePage(request.getParameter("page"));
        int offset = (page - 1) * PAGE_SIZE;

        UsersDAO dao = new UsersDAO();
        int total = dao.countUsersByRole(roleFilter, statusFilter);
        int totalPages = (int) Math.ceil(total / (double) PAGE_SIZE);
        if (totalPages == 0) {
            totalPages = 1;
        }
        if (page > totalPages) {
            page = totalPages;
            offset = (page - 1) * PAGE_SIZE;
        }

        List<UsersDTO> users = dao.listUsersByRole(roleFilter, statusFilter, offset, PAGE_SIZE);

        request.setAttribute("users", users);
        request.setAttribute("page", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("roleFilter", roleFilter);
        request.setAttribute("statusFilter", statusFilter);

        request.getRequestDispatcher("/jsp/adminUsers.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    /* xu ly cac hanh dong POST tu trang admin users (update, delete, restore) */
        String action = request.getParameter("action");
        if ("update".equals(action)) {
            handleUpdate(request, response);
            return;
        }
        if ("toggleStatus".equals(action)) {
            handleToggleStatus(request, response);
            return;
        }
        if (!"delete".equals(action) && !"restore".equals(action)) {
            response.sendRedirect(request.getContextPath() + "/dispatchcontroller?button=AdminUsers");
            return;
        }

        int userId = parseId(request.getParameter("userId"));
        if (userId <= 0) {
            response.sendRedirect(request.getContextPath() + "/dispatchcontroller?button=AdminUsers");
            return;
        }

        UsersDAO dao = new UsersDAO();
        if ("delete".equals(action)) {
            if (dao.softDeleteUser(userId)) {
                new SystemLogDAO().insertLog(userId, "This user is deleted.");
            }
        } else if ("restore".equals(action)) {
            dao.restoreUser(userId);
        }

        response.sendRedirect(request.getContextPath() + "/dispatchcontroller?button=AdminUsers");
    }

    private void handleUpdate(HttpServletRequest request, HttpServletResponse response) throws IOException {
    /* xu ly cap nhat thong tin user tu admin */
        String idRaw = request.getParameter("userId");
        String fullName = request.getParameter("fullName");
        String phone = request.getParameter("phone");
        String email = request.getParameter("email");
        String roleRaw = request.getParameter("roleId");
        String statusRaw = request.getParameter("status");
        String newPassword = request.getParameter("newPassword");

        int userId;
        int roleId;
        int status;
        try {
            userId = Integer.parseInt(idRaw);
            roleId = Integer.parseInt(roleRaw);
            status = Integer.parseInt(statusRaw);
        } catch (NumberFormatException ex) {
            request.getSession().setAttribute("adminMessage", "Dữ liệu cập nhật không hợp lệ.");
            response.sendRedirect(request.getContextPath() + "/dispatchcontroller?button=AdminUsers");
            return;
        }

        if (roleId == 2) {
            request.getSession().setAttribute("adminMessage", "Không thể gán vai trò ShopAdmin vì vai trò này đã bị loại bỏ.");
            response.sendRedirect(request.getContextPath() + "/dispatchcontroller?button=AdminUsers");
            return;
        }

        if (fullName == null || fullName.trim().isEmpty()
                || phone == null || phone.trim().isEmpty()
                || email == null || email.trim().isEmpty()) {
            request.getSession().setAttribute("adminMessage", "Vui lòng nhập đầy đủ thông tin cập nhật.");
            response.sendRedirect(request.getContextPath() + "/dispatchcontroller?button=AdminUsers");
            return;
        }

        UsersDAO dao = new UsersDAO();
        if (dao.isEmailExistExceptId(email.trim(), userId)) {
            request.getSession().setAttribute("adminMessage", "Email đã tồn tại.");
            response.sendRedirect(request.getContextPath() + "/dispatchcontroller?button=AdminUsers");
            return;
        }

        String hashedPassword = null;
        if (newPassword != null && !newPassword.trim().isEmpty()) {
            hashedPassword = HashUtil.md5(newPassword.trim());
        }

        boolean updated = dao.updateUserAdmin(userId, fullName.trim(), phone.trim(), email.trim(), roleId, status, hashedPassword);
        request.getSession().setAttribute("adminMessage", updated ? "Cập nhật user thành công." : "Cập nhật user thất bại.");
        response.sendRedirect(request.getContextPath() + "/dispatchcontroller?button=AdminUsers");
    }

    private Integer parseRole(String value) {
    /* chuyen tham so role sang Integer neu hop le */
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private Integer parseStatus(String value) {
    /* chuyen tham so status sang Integer neu hop le (0 hoac 1) */
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            int status = Integer.parseInt(value);
            return (status == 0 || status == 1) ? status : null;
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private int parsePage(String value) {
    /* chuyen tham so page sang int, tra ve 1 neu sai */
        if (value == null || value.trim().isEmpty()) {
            return 1;
        }
        try {
            int page = Integer.parseInt(value);
            return page <= 0 ? 1 : page;
        } catch (NumberFormatException ex) {
            return 1;
        }
    }

    private int parseId(String value) {
    /* chuyen tham so id sang int, tra ve -1 neu sai */
        if (value == null || value.trim().isEmpty()) {
            return -1;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            return -1;
        }
    }

    private void handleToggleStatus(HttpServletRequest request, HttpServletResponse response) throws IOException {
    /* xu ly khoa/mo tai khoan va ghi log hanh dong */
        int userId = parseId(request.getParameter("userId"));
        if (userId <= 0) {
            request.getSession().setAttribute("adminMessage", "ID tài khoản không hợp lệ.");
            response.sendRedirect(request.getContextPath() + "/dispatchcontroller?button=AdminUsers");
            return;
        }

        UsersDAO dao = new UsersDAO();
        UsersDTO targetUser = dao.getUserById(userId);
        if (targetUser == null) {
            request.getSession().setAttribute("adminMessage", "Không tìm thấy tài khoản.");
            response.sendRedirect(request.getContextPath() + "/dispatchcontroller?button=AdminUsers");
            return;
        }

        UsersDTO currentUser = (UsersDTO) request.getSession().getAttribute("acc");
        if (currentUser != null && currentUser.getId() == userId && targetUser.getStatus() == 1) {
            request.getSession().setAttribute("adminMessage", "Không thể tự khóa tài khoản đang đăng nhập.");
            response.sendRedirect(request.getContextPath() + "/dispatchcontroller?button=AdminUsers");
            return;
        }

        boolean updated;
        String message;
        if (targetUser.getStatus() == 1) {
            updated = dao.softDeleteUser(userId);
            message = updated ? "Đã khóa tài khoản thành công." : "Khóa tài khoản thất bại.";
        } else {
            updated = dao.restoreUser(userId);
            message = updated ? "Đã mở khóa tài khoản thành công." : "Mở khóa tài khoản thất bại.";
        }

        if (updated && currentUser != null) {
            String action = targetUser.getStatus() == 1
                    ? "Khóa tài khoản user ID=" + userId
                    : "Mở khóa tài khoản user ID=" + userId;
            new SystemLogDAO().insertLog(currentUser.getId(), action);
        }

        request.getSession().setAttribute("adminMessage", message);
        response.sendRedirect(request.getContextPath() + "/dispatchcontroller?button=AdminUsers");
    }
}
