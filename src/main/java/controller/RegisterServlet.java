/**
 *
 * Author : Nguyen Trung Hieu & Nguyen Thi Phuong Linh
 * Date : 26/02/2026
 * Description : Xu ly dang ky tai khoan, tao ho so tam va gui ma xac thuc email.
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
import java.util.List;
import DAO.SystemLogDAO;
import DAO.UsersDAO;
import model.UsersDTO;
import util.HashUtil;

@WebServlet(name = "RegisterServlet", urlPatterns = {"/register"})
public class RegisterServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        String verifiedEmail = (String) session.getAttribute("EMAIL_REGISTER");
        Boolean verified = (Boolean) session.getAttribute("EMAIL_VERIFIED");

        String username = request.getParameter("username");
        String phone = request.getParameter("phone");
        String fullName = request.getParameter("fullName");
        String password = request.getParameter("password");
        String confirm = request.getParameter("confirm");

        request.setAttribute("oldUser", username);
        request.setAttribute("oldPhone", phone);
        request.setAttribute("oldName", fullName);
        request.setAttribute("email", verifiedEmail);

        if (verifiedEmail == null || verifiedEmail.trim().isEmpty() || verified == null || !verified) {
            request.setAttribute("mess", "Vui lòng xác thực email trước!");
            request.getRequestDispatcher("/dispatchcontroller?button=RegisterPage").forward(request, response);
            return;
        }

        if (username == null || username.trim().isEmpty()
                || phone == null || phone.trim().isEmpty()
                || fullName == null || fullName.trim().isEmpty()
                || password == null || password.isEmpty()
                || confirm == null || confirm.isEmpty()) {
            request.setAttribute("mess", "Vui lòng nhập đầy đủ thông tin!");
            request.getRequestDispatcher("/dispatchcontroller?button=RegisterPage").forward(request, response);
            return;
        }

        if (!password.equals(confirm)) {
            request.setAttribute("mess", "Mật khẩu không khớp!");
            request.getRequestDispatcher("/dispatchcontroller?button=RegisterPage").forward(request, response);
            return;
        }

        UsersDAO dao = new UsersDAO();
        if (dao.isUsernameExist(username.trim())) {
            request.setAttribute("mess", "Username đã tồn tại!");
            request.setAttribute("suggestedUsers", suggestUsernames(username.trim(), dao));
            request.getRequestDispatcher("/dispatchcontroller?button=RegisterPage").forward(request, response);
            return;
        }

        if (dao.isEmailExist(verifiedEmail.trim())) {
            request.setAttribute("mess", "Email đã được đăng ký!");
            request.getRequestDispatcher("/dispatchcontroller?button=RegisterPage").forward(request, response);
            return;
        }

            // Kiểm tra trùng số điện thoại
            if (dao.isPhoneExist(phone.trim())) {
                request.setAttribute("mess", "Số điện thoại đã được đăng ký!");
                request.getRequestDispatcher("/dispatchcontroller?button=RegisterPage").forward(request, response);
                return;
            }

        UsersDTO user = new UsersDTO();
        user.setUsername(username.trim());
        user.setPhone(phone.trim());
        user.setFull_name(fullName.trim());
        user.setEmail(verifiedEmail.trim());
        user.setStatus(1);
        user.setRole_id(3);
        user.setPassword(HashUtil.md5(password));

        int userId = dao.createUser(user);
        if (userId <= 0) {
            request.setAttribute("mess", "Đăng ký thất bại, vui lòng thử lại! (Kiểm tra kết nối DB/constraints)");
            request.getRequestDispatcher("/dispatchcontroller?button=RegisterPage").forward(request, response);
            return;
        }

        new SystemLogDAO().insertLog(userId, "New user registered");
        session.removeAttribute("EMAIL_VERIFIED");
        session.removeAttribute("VERIFY_CODE");
        session.removeAttribute("VERIFY_TIME");

        request.setAttribute("success", "Đăng ký thành công. Vui lòng đăng nhập!");
        request.getRequestDispatcher("/dispatchcontroller?button=LoginPage").forward(request, response);
    }

    private List<String> suggestUsernames(String username, UsersDAO dao) {
        String base = username == null ? "" : username.trim().toLowerCase();
        base = base.replaceAll("[^a-z0-9]", "");
        if (base.isEmpty()) {
            base = "user";
        }

        List<String> suggestions = new ArrayList<>();
        for (int i = 1; i <= 50 && suggestions.size() < 5; i++) {
            String candidate = base + i;
            if (!dao.isUsernameExist(candidate)) {
                suggestions.add(candidate);
            }
        }
        return suggestions;
    }
}
