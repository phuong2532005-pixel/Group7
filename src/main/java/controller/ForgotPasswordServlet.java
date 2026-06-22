/**
 *
 * Author : Nguyen Trung Hieu & Nguyen Thi Phuong Linh
 * Date : 13/02/2026
 * Description : Quy trinh quen mat khau: gui ma, xac thuc va dat lai mat khau.
 */
package controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import DAO.UsersDAO;
import util.EmailUtil;
import util.HashUtil;

@WebServlet(name = "ForgotPasswordServlet", urlPatterns = {"/forgot"})
public class ForgotPasswordServlet extends HttpServlet {

    private static final long VERIFY_TTL_MS = 60000;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        /* dieu huong cac hanh dong send va reset mat khau */
        String action = request.getParameter("action");
        if ("send".equalsIgnoreCase(action)) {
            handleSendCode(request, response);
            return;
        }
        if ("reset".equalsIgnoreCase(action)) {
            handleReset(request, response);
            return;
        }
        response.sendRedirect(request.getContextPath() + "/dispatchcontroller?button=ForgotPage");
    }

    private void handleSendCode(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        /* xu ly gui ma xac nhan den email va luu tam vao session */
        String email = request.getParameter("email");
        request.setAttribute("email", email);

        if (email == null || email.trim().isEmpty()) {
            request.setAttribute("error", "Vui lòng nhập email!");
            request.getRequestDispatcher("/jsp/forgot.jsp").forward(request, response);
            return;
        }

        UsersDAO dao = new UsersDAO();
        if (!dao.isEmailExist(email.trim())) {
            request.setAttribute("error", "Email không tồn tại trong hệ thống!");
            request.getRequestDispatcher("/jsp/forgot.jsp").forward(request, response);
            return;
        }

        String code = String.valueOf((int)(Math.random() * 900000 + 100000));
        long now = System.currentTimeMillis();

        HttpSession session = request.getSession();
        session.setAttribute("FORGOT_CODE", code);
        session.setAttribute("FORGOT_TIME", now);
        session.setAttribute("FORGOT_EMAIL", email.trim());

        try {
            EmailUtil.sendEmail(
                email.trim(),
                "Xác nhận đổi mật khẩu",
                "Mã xác nhận của bạn là: " + code + "\nCó hiệu lực 60 giây."
            );
        } catch (Exception e) {
            request.setAttribute("error", "Không gửi được email: " + e.getMessage());
            request.getRequestDispatcher("/jsp/forgot.jsp").forward(request, response);
            return;
        }

        request.setAttribute("success", "Đã gửi mã xác nhận.");
        request.setAttribute("sentAt", now);
        request.getRequestDispatcher("/jsp/forgot.jsp").forward(request, response);
    }

    private void handleReset(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        /* xac thuc ma va dat lai mat khau neu hop le */
        HttpSession session = request.getSession(false);
        String email = request.getParameter("email");
        String code = request.getParameter("code");
        String newPassword = request.getParameter("newPassword");
        String confirm = request.getParameter("confirm");

        request.setAttribute("email", email);
        request.setAttribute("code", code);

        if (session == null) {
            request.setAttribute("error", "Phiên làm việc đã hết hạn. Vui lòng gửi lại mã.");
            request.getRequestDispatcher("/jsp/forgot.jsp").forward(request, response);
            return;
        }

        String realCode = (String) session.getAttribute("FORGOT_CODE");
        Long time = (Long) session.getAttribute("FORGOT_TIME");
        String storedEmail = (String) session.getAttribute("FORGOT_EMAIL");

        if (realCode == null || time == null || storedEmail == null) {
            request.setAttribute("error", "Chưa có mã xác nhận. Vui lòng gửi mã.");
            request.getRequestDispatcher("/jsp/forgot.jsp").forward(request, response);
            return;
        }

        if (System.currentTimeMillis() - time > VERIFY_TTL_MS) {
            clearSession(session);
            request.setAttribute("error", "Mã đã hết hạn. Vui lòng gửi lại mã.");
            request.getRequestDispatcher("/jsp/forgot.jsp").forward(request, response);
            return;
        }

        if (email == null || !storedEmail.equalsIgnoreCase(email.trim())) {
            request.setAttribute("error", "Email không khớp với email đã gửi mã.");
            request.getRequestDispatcher("/jsp/forgot.jsp").forward(request, response);
            return;
        }

        if (code == null || !realCode.equals(code.trim())) {
            request.setAttribute("error", "Mã xác nhận không đúng.");
            request.getRequestDispatcher("/jsp/forgot.jsp").forward(request, response);
            return;
        }

        // if passwords not provided yet, assume user just verified OTP and forward to
        // the dedicated change-password page so they can enter the new password.
        if (newPassword == null || newPassword.trim().isEmpty()
                || confirm == null || confirm.trim().isEmpty()) {
            // OTP has been validated above; send user to the reset form
            request.setAttribute("email", email);
            request.setAttribute("code", code);
            // inform user that OTP is correct and they can set new password
            request.setAttribute("message", "OTP hợp lệ, vui lòng nhập mật khẩu mới.");
            request.getRequestDispatcher("/jsp/changePassword.jsp").forward(request, response);
            return;
        }
        if (!newPassword.equals(confirm)) {
            request.setAttribute("error", "Mật khẩu xác nhận không khớp.");
            request.getRequestDispatcher("/jsp/forgot.jsp").forward(request, response);
            return;
        }

        UsersDAO dao = new UsersDAO();
        Integer userId = dao.getUserIdByEmail(email.trim());
        if (userId == null) {
            request.setAttribute("error", "Không tìm thấy tài khoản theo email.");
            request.getRequestDispatcher("/jsp/forgot.jsp").forward(request, response);
            return;
        }

        boolean updated = dao.updatePassword(userId, HashUtil.md5(newPassword.trim()));
        if (!updated) {
            request.setAttribute("error", "Đổi mật khẩu thất bại. Vui lòng thử lại.");
            request.getRequestDispatcher("/jsp/forgot.jsp").forward(request, response);
            return;
        }

        clearSession(session);
        response.sendRedirect(request.getContextPath() + "/dispatchcontroller?button=LoginPage");
    }

    private void clearSession(HttpSession session) {
        /* xoa cac thong tin FORGOT tu session */
        session.removeAttribute("FORGOT_CODE");
        session.removeAttribute("FORGOT_TIME");
        session.removeAttribute("FORGOT_EMAIL");
    }
}
