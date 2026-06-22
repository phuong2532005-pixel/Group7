/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

/**
 *
 * Author : Nguyen Trung Hieu & Nguyen Thi Phuong Linh
 * Date : 10/03/2026
 * Description : Xac thuc ma email khi dang ky hoac thay doi email (confirm).
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

/**
 *
 * Author : LinhNTP
 * Date  : Feb 3, 2026
 * Desciption : 
 */
@WebServlet(name="VerifyEmailServlet", urlPatterns={"/verifyemail"})
public class VerifyEmailServlet extends HttpServlet {
   
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
            out.println("<title>Servlet VerifyEmailServlet</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet VerifyEmailServlet at " + request.getContextPath () + "</h1>");
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
        String inputCode = request.getParameter("code");
        HttpSession session = request.getSession();

        String realCode = (String) session.getAttribute("VERIFY_CODE");
        Long time = (Long) session.getAttribute("VERIFY_TIME");

        if (realCode == null || time == null) {
            request.setAttribute("error", "Vui lòng gửi mã trước!");
            request.setAttribute("email", session.getAttribute("EMAIL_REGISTER"));
            request.getRequestDispatcher("/dispatchcontroller?button=ConformEmailPage").forward(request, response);
            return;
        }

        if (System.currentTimeMillis() - time > 60000) {
            request.setAttribute("error", "Mã đã hết hạn!");
            request.setAttribute("email", session.getAttribute("EMAIL_REGISTER"));
            request.getRequestDispatcher("/dispatchcontroller?button=ConformEmailPage").forward(request, response);
            return;
        }

        if (!realCode.equals(inputCode)) {
            request.setAttribute("error", "Mã không đúng!");
            request.setAttribute("email", session.getAttribute("EMAIL_REGISTER"));
            request.getRequestDispatcher("/dispatchcontroller?button=ConformEmailPage").forward(request, response);
            return;
        }

        session.setAttribute("EMAIL_VERIFIED", true);

        response.sendRedirect(request.getContextPath() + "/dispatchcontroller?button=RegisterPage");
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
