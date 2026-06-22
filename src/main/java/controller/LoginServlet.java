/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

/**
 *
 * Author : Nguyen Trung Hieu & Nguyen Thi Phuong Linh
 * Date : 16/02/2026
 * Description : Xu ly dang nhap nguoi dung (session, remember-me, kiem tra quyen).
 */
package controller;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import DAO.SystemLogDAO;
import DAO.UsersDAO;
import model.UsersDTO;
import util.HashUtil;
import java.util.Base64;
import java.nio.charset.StandardCharsets;
import javax.servlet.http.Cookie;

/**
 *
 * Author : LinhNTP
 * Date  : Feb 3, 2026
 * Desciption : 
 */
@WebServlet(name="LoginServlet", urlPatterns={"/login"})
public class LoginServlet extends HttpServlet {
   
    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
    /* xu ly yeu cau GET va POST cho trang dang nhap */
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet LoginServlet</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet LoginServlet at " + request.getContextPath () + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    } 

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        /* hien thi trang dang nhap hoac xu ly remember-me */
        processRequest(request, response);
    } 

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        /* xu ly thong tin dang nhap va quan ly session, cookie remember-me */
        String loginKey = request.getParameter("username");
        String password = request.getParameter("password");
        String roleRaw = request.getParameter("role");
        String returnUrl = request.getParameter("returnUrl");

        if (returnUrl != null) {
            request.setAttribute("returnUrl", returnUrl);
        }

        request.setAttribute("oldUser", loginKey);
        request.setAttribute("oldPass", password);

        if (roleRaw == null) {
            request.setAttribute("mess", "Vui lòng chọn role!");
            request.getRequestDispatcher("/dispatchcontroller?button=LoginPage").forward(request, response);
            return;
        }

        int roleId;
        try {
            roleId = Integer.parseInt(roleRaw);
        } catch (NumberFormatException ex) {
            request.setAttribute("mess", "Vai trò đăng nhập không hợp lệ!");
            request.getRequestDispatcher("/dispatchcontroller?button=LoginPage").forward(request, response);
            return;
        }
        if (roleId == 2) {
            request.setAttribute("mess", "Vai trò Shop Manager đã bị loại bỏ khỏi hệ thống.");
            request.getRequestDispatcher("/dispatchcontroller?button=LoginPage").forward(request, response);
            return;
        }
        UsersDAO dao = new UsersDAO();

        // Login key (username/email/phone) không tồn tại
        try {
            if (!dao.isLoginKeyExist(loginKey)) {
                request.setAttribute("mess", "Tài khoản không tồn tại!");
                request.getRequestDispatcher("/dispatchcontroller?button=LoginPage").forward(request, response);
                return;
            }
        } catch (Exception ex) {
            request.setAttribute("mess", "Lỗi kết nối cơ sở dữ liệu khi kiểm tra tài khoản. Vui lòng thử lại.");
            request.getRequestDispatcher("/dispatchcontroller?button=LoginPage").forward(request, response);
            return;
        }

        Integer userStatus = dao.getUserStatusByLoginKey(loginKey);
        if (userStatus != null && userStatus == 0) {
            request.setAttribute("mess", "Tài khoản đã bị khóa. Vui lòng liên hệ quản trị viên.");
            request.getRequestDispatcher("/dispatchcontroller?button=LoginPage").forward(request, response);
            return;
        }

        // Sai mật khẩu
        UsersDTO acc = dao.loginWithRawPasswordByLoginKey(loginKey, password);
        if (acc == null) {
            String hashedPassword = HashUtil.md5(password);
            acc = dao.loginByLoginKey(loginKey, hashedPassword);
        }
        if (acc == null) {
            String utf8Hash = HashUtil.md5Utf8(password);
            acc = dao.loginByLoginKey(loginKey, utf8Hash);
        }
        if (acc == null) {
            String win1258Hash = HashUtil.md5Windows1258(password);
            acc = dao.loginByLoginKey(loginKey, win1258Hash);
        }
        if (acc == null) {
            request.setAttribute("mess", "Sai mật khẩu!");
            request.getRequestDispatcher("/dispatchcontroller?button=LoginPage").forward(request, response);
            return;
        }

        // Sai role
        if (acc.getRole_id() != roleId) {
            request.setAttribute("mess", "Sai quyền truy cập!");
            request.getRequestDispatcher("/dispatchcontroller?button=LoginPage").forward(request, response);
            return;
        }

        // Thành công
        request.getSession().setAttribute("acc", acc);
        // record login in system logs
        try {
            new SystemLogDAO().insertLog(acc.getId(), "User logged in");
        } catch (Exception e) {
            System.out.println("[LoginServlet] failed to write log: " + e.getMessage());
        }
        // handle remember-me cookie
        String remember = request.getParameter("rememberMe");
        if ("on".equals(remember)) {
            // encode loginKey/password into Base64 (note: in production use token instead)
            String encodedUser = Base64.getEncoder().encodeToString(loginKey.getBytes(StandardCharsets.UTF_8));
            String encodedPass = Base64.getEncoder().encodeToString(password.getBytes(StandardCharsets.UTF_8));
            Cookie userCookie = new Cookie("rememberUser", encodedUser);
            Cookie passCookie = new Cookie("rememberPass", encodedPass);
            userCookie.setMaxAge(60*60*24*30); // 30 days
            passCookie.setMaxAge(60*60*24*30);
            userCookie.setHttpOnly(true);
            passCookie.setHttpOnly(true);
            userCookie.setPath(request.getContextPath());
            passCookie.setPath(request.getContextPath());
            response.addCookie(userCookie);
            response.addCookie(passCookie);
        } else {
            // clear any existing remember cookies
            Cookie userCookie = new Cookie("rememberUser", "");
            Cookie passCookie = new Cookie("rememberPass", "");
            userCookie.setMaxAge(0);
            passCookie.setMaxAge(0);
            userCookie.setPath(request.getContextPath());
            passCookie.setPath(request.getContextPath());
            response.addCookie(userCookie);
            response.addCookie(passCookie);
        }
        if (acc.getRole_id() == 3 && isSafeReturn(returnUrl)) {
            response.sendRedirect(request.getContextPath() + returnUrl);
        } else if (acc.getRole_id() == 1) {
            response.sendRedirect(request.getContextPath() + "/dispatchcontroller?button=AdminUsers");
        } else if (acc.getRole_id() == 4) {
            // Director - redirect to dashboard to view revenue reports
            response.sendRedirect(request.getContextPath() + "/dispatchcontroller?button=AdminDashboard");
        } else {
            response.sendRedirect(request.getContextPath() + "/dispatchcontroller?button=Home");
        }
    }

    private boolean isSafeReturn(String returnUrl) {
        /* kiem tra returnUrl an toan de tranh open redirect */
        if (returnUrl == null || returnUrl.trim().isEmpty()) {
            return false;
        }
        if (!returnUrl.startsWith("/")) {
            return false;
        }
        if (returnUrl.startsWith("//") || returnUrl.contains("://")) {
            return false;
        }
        return true;
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        /* thong tin ngan ve servlet */
        return "Short description";
    }// </editor-fold>

}
