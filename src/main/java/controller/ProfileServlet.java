/**
 *
 * Author : Nguyen Trung Hieu & Nguyen Thi Phuong Linh
 * Date : 24/02/2026
 * Description : Quan ly thong tin ca nhan, thay doi mat khau va xac thuc OTP.
 */
package controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import DAO.AddressDAO;
import DAO.UsersDAO;
import model.UsersDTO;
import util.EmailUtil;
import util.HashUtil;

@WebServlet(name = "ProfileServlet", urlPatterns = {"/profile"})
public class ProfileServlet extends HttpServlet {

    private static final long VERIFY_TTL_MS = 60000;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        UsersDTO acc = session == null ? null : (UsersDTO) session.getAttribute("acc");
        if (acc != null) {
            loadAddresses(request, acc.getId());
        }
        request.getRequestDispatcher("/jsp/profile.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        UsersDTO acc = session == null ? null : (UsersDTO) session.getAttribute("acc");
        if (acc == null) {
            response.sendRedirect(request.getContextPath() + "/dispatchcontroller?button=LoginPage");
            return;
        }

        String action = request.getParameter("action");
        if ("updateInfo".equals(action)) {
            handleUpdateInfo(request, response, session, acc);
            return;
        }
        if ("requestPasswordChange".equals(action)) {
            handleRequestPasswordChange(request, response, session, acc);
            return;
        }
        if ("addAddress".equals(action)) {
            handleAddAddress(request, response, acc);
            return;
        }
        if ("updateAddress".equals(action)) {
            handleUpdateAddress(request, response, acc);
            return;
        }
        if ("deleteAddress".equals(action)) {
            handleDeleteAddress(request, response, acc);
            return;
        }
        if ("verifyCode".equals(action)) {
            handleVerifyCode(request, response, session, acc);
            return;
        }

        response.sendRedirect(request.getContextPath() + "/dispatchcontroller?button=Profile");
    }

    private void handleUpdateInfo(HttpServletRequest request, HttpServletResponse response,
            HttpSession session, UsersDTO acc) throws ServletException, IOException {
        String fullName = request.getParameter("fullName");
        String phone = request.getParameter("phone");
        String email = request.getParameter("email");
        // keep values for re-display
        request.setAttribute("oldFullName", fullName);
        request.setAttribute("oldPhone", phone);
        request.setAttribute("oldEmail", email);

        if (fullName == null || fullName.trim().isEmpty()
                || phone == null || phone.trim().isEmpty()
                || email == null || email.trim().isEmpty()) {
            request.setAttribute("error", "Vui lòng nhập đầy đủ thông tin.");
            forwardProfile(request, response, acc);
            return;
        }
        // format checks: phone digits only 10-11, email basic pattern
        String phoneTrim = phone.trim();
        if (!phoneTrim.matches("\\d{10,11}")) {
            request.setAttribute("error", "Số điện thoại không hợp lệ (10-11 chữ số). ");
            forwardProfile(request, response, acc);
            return;
        }
        String emailTrimmed = email.trim();
        if (!emailTrimmed.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            request.setAttribute("error", "Email không hợp lệ.");
            forwardProfile(request, response, acc);
            return;
        }

        UsersDAO dao = new UsersDAO();
        // determine what has changed
        boolean nameChanged = acc.getFull_name() == null || !fullName.trim().equals(acc.getFull_name());
        boolean phoneChanged = acc.getPhone() == null || !phone.trim().equals(acc.getPhone());
        boolean emailChanged = acc.getEmail() == null || !emailTrimmed.equalsIgnoreCase(acc.getEmail());

        if (emailChanged && dao.isEmailExistExceptId(emailTrimmed, acc.getId())) {
            request.setAttribute("error", "Email đã tồn tại.");
            forwardProfile(request, response, acc);
            return;
        }

        // If any profile field changes, verify via the currently verified email first.
        if (nameChanged || phoneChanged || emailChanged) {
            session.setAttribute("PENDING_FULLNAME", fullName.trim());
            session.setAttribute("PENDING_PHONE", phone.trim());
            session.setAttribute("PENDING_EMAIL", emailTrimmed);
            String toEmail = acc.getEmail();
            if (sendVerifyCode(session, toEmail, "changeInfo")) {
                request.setAttribute("message", "Đã gửi mã xác nhận tới email hiện tại của bạn: " + toEmail + ". Vui lòng nhập OTP.");
                request.setAttribute("activeSection", "info");
            } else {
                request.setAttribute("error", "Không gửi được mã xác nhận email.");
            }
            forwardProfile(request, response, acc);
            return;
        }

        // nothing changed
        request.setAttribute("message", "Không có gì thay đổi.");
        forwardProfile(request, response, acc);
        return;
    }

    private void handleRequestPasswordChange(HttpServletRequest request, HttpServletResponse response,
            HttpSession session, UsersDTO acc) throws ServletException, IOException {
        request.setAttribute("activeSection", "password");
        String oldPassword = request.getParameter("oldPassword");
        String newPassword = request.getParameter("newPassword");
        String confirm = request.getParameter("confirmPassword");
        preservePasswordInputs(request, oldPassword, newPassword, confirm);

        if (oldPassword == null || oldPassword.trim().isEmpty()) {
            request.setAttribute("error", "Vui lòng nhập mật khẩu hiện tại.");
            forwardProfile(request, response, acc);
            return;
        }

        if (newPassword == null || newPassword.trim().isEmpty()
                || confirm == null || confirm.trim().isEmpty()) {
            request.setAttribute("error", "Vui lòng nhập mật khẩu mới và xác nhận.");
            forwardProfile(request, response, acc);
            return;
        }

        String oldTrimmed = oldPassword.trim();
        String currentHash = acc.getPassword();
        boolean oldPasswordValid = currentHash != null && (
                currentHash.equalsIgnoreCase(HashUtil.md5(oldTrimmed))
                || currentHash.equalsIgnoreCase(HashUtil.md5Utf8(oldTrimmed))
                || currentHash.equalsIgnoreCase(HashUtil.md5Windows1258(oldTrimmed))
        );
        if (!oldPasswordValid) {
            request.setAttribute("error", "Mật khẩu hiện tại không đúng.");
            forwardProfile(request, response, acc);
            return;
        }

        if (!newPassword.equals(confirm)) {
            request.setAttribute("error", "Mật khẩu xác nhận không khớp.");
            forwardProfile(request, response, acc);
            return;
        }

        if (oldTrimmed.equals(newPassword.trim())) {
            request.setAttribute("error", "Mật khẩu mới phải khác mật khẩu hiện tại.");
            forwardProfile(request, response, acc);
            return;
        }

        session.setAttribute("PENDING_PASSWORD_HASH", HashUtil.md5(newPassword.trim()));
        if (sendVerifyCode(session, acc.getEmail(), "changePassword")) {
            request.setAttribute("message", "Đã gửi mã xác nhận tới email của bạn.");
        } else {
            request.setAttribute("error", "Không gửi được mã xác nhận mật khẩu.");
        }

        forwardProfile(request, response, acc);
    }

    private void handleAddAddress(HttpServletRequest request, HttpServletResponse response,
            UsersDTO acc) throws ServletException, IOException {
        String street = request.getParameter("street");
        String district = request.getParameter("district");
        String city = request.getParameter("city");
        boolean isDefault = "on".equalsIgnoreCase(request.getParameter("isDefault"))
                || "1".equals(request.getParameter("isDefault"));

        if (isBlank(street) || isBlank(district) || isBlank(city)) {
            request.setAttribute("error", "Vui lòng nhập đầy đủ thông tin địa chỉ.");
            forwardProfile(request, response, acc);
            return;
        }

        AddressDAO dao = new AddressDAO();
        boolean ok = dao.insertAddress(
                acc.getId(),
                street.trim(),
                district.trim(),
                city.trim(),
                isDefault
        );
        if (ok) {
            request.setAttribute("message", "Thêm địa chỉ giao hàng thành công.");
        } else {
            request.setAttribute("error", "Không thể thêm địa chỉ giao hàng.");
        }
        forwardProfile(request, response, acc);
    }

    private void handleUpdateAddress(HttpServletRequest request, HttpServletResponse response,
            UsersDTO acc) throws ServletException, IOException {
        Integer addressId = parseIntOrNull(request.getParameter("addressId"));
        String street = request.getParameter("street");
        String district = request.getParameter("district");
        String city = request.getParameter("city");
        boolean isDefault = "on".equalsIgnoreCase(request.getParameter("isDefault"))
                || "1".equals(request.getParameter("isDefault"));

        if (addressId == null || isBlank(street) || isBlank(district) || isBlank(city)) {
            request.setAttribute("error", "Thông tin cập nhật địa chỉ chưa hợp lệ.");
            forwardProfile(request, response, acc);
            return;
        }

        AddressDAO dao = new AddressDAO();
        boolean ok = dao.updateAddress(addressId, acc.getId(), street.trim(), district.trim(), city.trim(), isDefault);
        if (ok) {
            request.setAttribute("message", "Cập nhật địa chỉ thành công.");
        } else {
            request.setAttribute("error", "Không thể cập nhật địa chỉ.");
        }
        forwardProfile(request, response, acc);
    }

    private void handleDeleteAddress(HttpServletRequest request, HttpServletResponse response,
            UsersDTO acc) throws ServletException, IOException {
        Integer addressId = parseIntOrNull(request.getParameter("addressId"));
        if (addressId == null) {
            request.setAttribute("error", "Không xác định được địa chỉ cần xóa.");
            forwardProfile(request, response, acc);
            return;
        }

        AddressDAO dao = new AddressDAO();
        boolean ok = dao.deleteAddress(addressId, acc.getId());
        if (ok) {
            request.setAttribute("message", "Đã xóa địa chỉ.");
        } else {
            request.setAttribute("error", "Không thể xóa địa chỉ.");
        }
        forwardProfile(request, response, acc);
    }

    private void handleVerifyCode(HttpServletRequest request, HttpServletResponse response,
            HttpSession session, UsersDTO acc) throws ServletException, IOException {
        String inputCode = request.getParameter("code");
        String realCode = (String) session.getAttribute("VERIFY_CODE");
        Long time = (Long) session.getAttribute("VERIFY_TIME");
        String mode = (String) session.getAttribute("VERIFY_MODE");
        String oldPassword = request.getParameter("oldPassword");
        String newPassword = request.getParameter("newPassword");
        String confirm = request.getParameter("confirmPassword");

        if ("changePassword".equals(mode)) {
            request.setAttribute("activeSection", "password");
            preservePasswordInputs(request, oldPassword, newPassword, confirm);
        } else if ("changeEmail".equals(mode) || "changeInfo".equals(mode)) {
            request.setAttribute("activeSection", "info");
        }

        if (realCode == null || time == null || mode == null) {
            request.setAttribute("error", "Không có mã xác nhận để kiểm tra.");
            forwardProfile(request, response, acc);
            return;
        }

        if (System.currentTimeMillis() - time > VERIFY_TTL_MS) {
            clearVerifySession(session);
            request.setAttribute("error", "Mã đã hết hạn.");
            forwardProfile(request, response, acc);
            return;
        }

        if (inputCode == null || !realCode.equals(inputCode)) {
            request.setAttribute("error", "Mã xác nhận không đúng.");
            forwardProfile(request, response, acc);
            return;
        }

        UsersDAO dao = new UsersDAO();
        if ("changeInfo".equals(mode)) {
            // apply pending personal info fields
            String pendingFull = (String) session.getAttribute("PENDING_FULLNAME");
            String pendingPhone = (String) session.getAttribute("PENDING_PHONE");
            String pendingEmail2 = (String) session.getAttribute("PENDING_EMAIL");
            boolean ok2 = dao.updateProfileBasic(acc.getId(), acc.getUsername(),
                    pendingFull == null ? acc.getFull_name() : pendingFull,
                    pendingPhone == null ? acc.getPhone() : pendingPhone);
            if (ok2) {
                if (pendingEmail2 != null && !pendingEmail2.trim().isEmpty() &&
                        !pendingEmail2.equalsIgnoreCase(acc.getEmail())) {
                    dao.updateEmail(acc.getId(), pendingEmail2.trim());
                }
                UsersDTO refreshed = dao.getUserById(acc.getId());
                if (refreshed != null) {
                    session.setAttribute("acc", refreshed);
                }
                request.setAttribute("message", "Cập nhật thông tin thành công.");
            } else {
                request.setAttribute("error", "Cập nhật thông tin thất bại.");
            }
        } else if ("changeEmail".equals(mode)) {
            String pendingEmail = (String) session.getAttribute("PENDING_EMAIL");
            if (pendingEmail == null || pendingEmail.trim().isEmpty()) {
                request.setAttribute("error", "Không có email chờ xác nhận.");
            } else if (dao.updateEmail(acc.getId(), pendingEmail.trim())) {
                UsersDTO refreshed = dao.getUserById(acc.getId());
                if (refreshed != null) {
                    session.setAttribute("acc", refreshed);
                }
                request.setAttribute("message", "Cập nhật email thành công.");
            } else {
                request.setAttribute("error", "Cập nhật email thất bại.");
            }
        } else if ("changePassword".equals(mode)) {
            String pendingHash = (String) session.getAttribute("PENDING_PASSWORD_HASH");
            if (pendingHash == null || pendingHash.trim().isEmpty()) {
                request.setAttribute("error", "Không có mật khẩu chờ xác nhận.");
            } else if (dao.updatePassword(acc.getId(), pendingHash)) {
                request.setAttribute("message", "Đổi mật khẩu thành công.");
                preservePasswordInputs(request, "", "", "");
            } else {
                request.setAttribute("error", "Đổi mật khẩu thất bại.");
            }
        }

        clearVerifySession(session);
        forwardProfile(request, response, acc);
    }

    private boolean sendVerifyCode(HttpSession session, String email, String mode) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        String code = String.valueOf((int) (Math.random() * 900000 + 100000));
        long now = System.currentTimeMillis();

        session.setAttribute("VERIFY_CODE", code);
        session.setAttribute("VERIFY_TIME", now);
        session.setAttribute("VERIFY_MODE", mode);

        try {
            EmailUtil.sendEmail(
                    email,
                    "Xác nhận thay đổi",
                    "Mã xác nhận của bạn là: " + code + "\nCó hiệu lực 60 giây."
            );
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private void clearVerifySession(HttpSession session) {
        session.removeAttribute("VERIFY_CODE");
        session.removeAttribute("VERIFY_TIME");
        session.removeAttribute("VERIFY_MODE");
        session.removeAttribute("PENDING_FULLNAME");
        session.removeAttribute("PENDING_PHONE");
        session.removeAttribute("PENDING_EMAIL");
        session.removeAttribute("PENDING_PASSWORD_HASH");
    }

    private void loadAddresses(HttpServletRequest request, int userId) {
        AddressDAO dao = new AddressDAO();
        request.setAttribute("addresses", dao.listByUser(userId));
    }

    private void preservePasswordInputs(HttpServletRequest request, String oldPassword,
            String newPassword, String confirmPassword) {
        request.setAttribute("oldPasswordValue", oldPassword == null ? "" : oldPassword);
        request.setAttribute("newPasswordValue", newPassword == null ? "" : newPassword);
        request.setAttribute("confirmPasswordValue", confirmPassword == null ? "" : confirmPassword);
    }

    private void forwardProfile(HttpServletRequest request, HttpServletResponse response,
            UsersDTO acc) throws ServletException, IOException {
        if (acc != null) {
            loadAddresses(request, acc.getId());
        }
        request.getRequestDispatcher("/jsp/profile.jsp").forward(request, response);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private Integer parseIntOrNull(String value) {
        if (isBlank(value)) {
            return null;
        }
        try {
            return Integer.valueOf(value.trim());
        } catch (Exception ex) {
            return null;
        }
    }
}
