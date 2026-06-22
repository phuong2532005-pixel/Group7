/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

/**
 *
 * Author : Nguyen Trung Hieu & Nguyen Thi Phuong Linh
 * Date : 01/03/2026
 * Description : Gui ma xac thuc dang ky toi email nguoi dung.
 */
package controller;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import DAO.UsersDAO;
import util.EmailUtil;

/**
 *
 * Author : LinhNTP
 * Date  : Feb 3, 2026
 * Desciption : 
 */
@WebServlet(name="SendRegisterCodeServlet", urlPatterns={"/sendregistercode"})
public class SendRegisterCodeServlet extends HttpServlet {
   
    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet SendRegisterCodeServlet</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet SendRegisterCodeServlet at " + request.getContextPath () + "</h1>");
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
        String email = request.getParameter("email");
        HttpSession session = request.getSession();

        if (email == null || email.trim().isEmpty()) {
            request.setAttribute("error", "Vui lòng nhập email!");
            request.getRequestDispatcher("/dispatchcontroller?button=ConformEmailPage").forward(request, response);
            return;
        }

        email = email.trim();

        // Kiểm tra định dạng email hợp lệ (phải có TLD như .com, .net...)
        if (!email.matches("^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$")) {
            request.setAttribute("error", "Email không hợp lệ! Vui lòng nhập đúng dạng abc@domain.com");
            request.setAttribute("email", email);
            request.getRequestDispatcher("/dispatchcontroller?button=ConformEmailPage").forward(request, response);
            return;
        }

        UsersDAO dao = new UsersDAO();
        if (dao.isEmailExist(email)) {
            request.setAttribute("error", "Email đã được đăng ký!");
            request.getRequestDispatcher("/dispatchcontroller?button=ConformEmailPage").forward(request, response);
            return;
        }

        String code = String.valueOf((int)(Math.random() * 900000 + 100000));
        long now = System.currentTimeMillis();

        session.setAttribute("VERIFY_CODE", code);
        session.setAttribute("VERIFY_TIME", now);
        session.setAttribute("EMAIL_REGISTER", email);
        session.setAttribute("EMAIL_VERIFIED", false);

        try {
            EmailUtil.sendEmail(
                email,
                "Xác nhận đăng ký",
                "Mã xác nhận của bạn là: " + code + "\nCó hiệu lực 60 giây."
            );
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Không gửi được email: " + e.getMessage());
            request.getRequestDispatcher("/dispatchcontroller?button=ConformEmailPage").forward(request, response);
            return;
        }

        request.setAttribute("email", email);
        request.setAttribute("success", "Đã gửi mã xác nhận! Vui lòng kiểm tra Email.");
        request.getRequestDispatcher("/dispatchcontroller?button=ConformEmailPage").forward(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
