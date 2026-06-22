/**
 *
 * Author : Nguyen Trung Hieu & Nguyen Thi Phuong Linh
 * Date : 18/02/2026
 * Description : Dang xuat: huy session, xoa cookie remember-me va redirect ve trang chu.
 */
package controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Cookie;
import java.io.IOException;

@WebServlet(name = "LogoutServlet", urlPatterns = {"/logout"})
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        /* huy session, xoa cookie remember-me va chuyen huong ve trang chu */
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        // clear remember-me cookies as well
        Cookie userCookie = new Cookie("rememberUser", "");
        Cookie passCookie = new Cookie("rememberPass", "");
        userCookie.setMaxAge(0);
        passCookie.setMaxAge(0);
        userCookie.setPath(request.getContextPath());
        passCookie.setPath(request.getContextPath());
        response.addCookie(userCookie);
        response.addCookie(passCookie);
        // After logout, redirect to root with a flash message
        response.sendRedirect(request.getContextPath() + "/?msg=logout_success");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        /* xu ly POST nhu GET de dang xuat */
        doGet(request, response);
    }
}
