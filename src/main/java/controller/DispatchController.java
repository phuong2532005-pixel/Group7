/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
/**
 *
 * Author : Nguyen Trung Hieu & Nguyen Thi Phuong Linh
 * Date : 12/02/2026
 * Description : Bo dinh tuy trung tam chuyen huong cac hanh dong theo tham so button.
 */
package controller;

import DAO.UsersDAO;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Base64;
import java.nio.charset.StandardCharsets;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * Author : LinhNTP Date : Feb 3, 2026 Desciption :
 */
@WebServlet(name = "DispatchController", urlPatterns = {"/dispatchcontroller"})
public class DispatchController extends HttpServlet {

    private static final String LOGIN_SERVLET = "/login";
    private static final String HOME_PAGE = "/home";
    private static final String SEND_CODE = "/sendregistercode";
    private static final String VERIFY_CODE = "/verifyemail";
    private static final String REGISTER_SERVLET = "/register";
    private static final String ADMIN_USERS = "/admin/users";
    private static final String ADMIN_DASHBOARD = "/admin/dashboard";
    private static final String ADMIN_ORDERS = "/admin/orders";
    private static final String PROFILE = "/profile";
    private static final String LOGOUT = "/logout";
    private static final String LOGIN_PAGE = "/jsp/login.jsp";
    private static final String REGISTER_PAGE = "/jsp/register.jsp";
    private static final String EMAIL_CONFIRM_PAGE = "/jsp/conformEmail.jsp";
    private static final String FORGOT_PAGE = "/jsp/forgot.jsp";
    private static final String PRODUCT_LIST = "/products";

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        /* dieu huong cac yeu cau theo tham so button va tra ve duong dan */
        response.setContentType("text/html;charset=UTF-8");
        String button = request.getParameter("button");
        String url = HOME_PAGE;

        if (button == null) {
            url = HOME_PAGE;
        } else {
            switch (button) {
                case "Home":
                    url = HOME_PAGE;
                    break;
                case "Login":
                    url = LOGIN_SERVLET;
                    break;
                case "SendRegisterCode":
                    url = SEND_CODE; // /sendregistercode
                    break;
                case "RegisterPage":
                    url = REGISTER_PAGE;
                    break;
                case "ConformEmailPage":
                    url = EMAIL_CONFIRM_PAGE;
                    break;
                case "ForgotPage":
                    url = FORGOT_PAGE;
                    break;

                case "VerifyEmail":
                    url = VERIFY_CODE;
                    break;
                case "Register":
                    url = REGISTER_SERVLET;
                    break;
                case "AdminUsers":
                    url = ADMIN_USERS;
                    break;
                case "AdminCategories":
                    url = "/admin/categories";
                    break;
                case "AdminDashboard":
                    url = ADMIN_DASHBOARD;
                    break;
                case "AdminOrders":
                    url = ADMIN_ORDERS;
                    break;
                case "AdminProducts":
                    url = "/admin/products";
                    break;
                case "Profile":
                    url = PROFILE;
                    break;
                case "ShopProducts":
                    url = ADMIN_USERS;
                    break;
                case "Logout":
                    url = LOGOUT;
                    break;
                case "LoginPage":
                    // populate login fields from remember-me cookies if present
                    Cookie[] cookies = request.getCookies();
                    String cookieUser = null, cookiePass = null;
                    if (cookies != null) {
                        for (Cookie c : cookies) {
                            if ("rememberUser".equals(c.getName())) {
                                try {
                                    cookieUser = new String(Base64.getDecoder().decode(c.getValue()), StandardCharsets.UTF_8);
                                    request.setAttribute("oldUser", cookieUser);
                                } catch (Exception e) { /* ignore */ }
                            }
                            if ("rememberPass".equals(c.getName())) {
                                try {
                                    cookiePass = new String(Base64.getDecoder().decode(c.getValue()), StandardCharsets.UTF_8);
                                    request.setAttribute("oldPass", cookiePass);
                                } catch (Exception e) { /* ignore */ }
                            }
                        }
                    }
                    // if both cookies present and no session yet, attempt auto-login
                    if (cookieUser != null && cookiePass != null && request.getSession(false) == null) {
                        UsersDAO dao = new UsersDAO();
                        Integer userStatus = dao.getUserStatusByLoginKey(cookieUser);
                        if (userStatus != null && userStatus == 0) {
                            // clear invalid remember cookies for locked account
                            Cookie clearUser = new Cookie("rememberUser", "");
                            Cookie clearPass = new Cookie("rememberPass", "");
                            clearUser.setMaxAge(0);
                            clearPass.setMaxAge(0);
                            clearUser.setPath(request.getContextPath());
                            clearPass.setPath(request.getContextPath());
                            response.addCookie(clearUser);
                            response.addCookie(clearPass);
                        } else {
                        model.UsersDTO acc = dao.loginWithRawPasswordByLoginKey(cookieUser, cookiePass);
                        if (acc == null) {
                            String hashed = util.HashUtil.md5(cookiePass);
                            acc = dao.loginByLoginKey(cookieUser, hashed);
                        }
                        if (acc != null) {
                            // assume role stored as 3 by default; real role cannot be determined from cookie
                            request.getSession().setAttribute("acc", acc);
                            response.sendRedirect(request.getContextPath() + "/dispatchcontroller?button=Home");
                            return;
                        }
                        }
                    }
                    url = LOGIN_PAGE;
                    break;
                case "RegisterEmail":
                    url = EMAIL_CONFIRM_PAGE;
                    break;
                case "ProductList":
                    url = PRODUCT_LIST;
                    break;
                default:
                    url = HOME_PAGE;
                    break;
            }
        }

        request.getRequestDispatcher(url).forward(request, response);

    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        /* xu ly GET thong qua processRequest */
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        /* xu ly POST thong qua processRequest */
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        /* thong tin ngan ve servlet */
        return "Short description";
    }// </editor-fold>

}
