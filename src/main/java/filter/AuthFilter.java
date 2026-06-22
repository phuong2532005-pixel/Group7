/**
 *
 * Author : Nguyen Trung Hieu & Nguyen Thi Phuong Linh
 * Date : 02/04/2026
 * Description : Loc xac thuc truy cap cac duong dan can dang nhap/kiem tra quyen.
 */
package filter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import DAO.UsersDAO;
import model.UsersDTO;

@WebFilter(filterName = "AuthFilter", urlPatterns = {"/jsp/*", "/admin/*"})
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String uri = req.getRequestURI();
        // Allow root context path (welcome file) to be public so unauthenticated users can see home
        String context = req.getContextPath();
        if (uri.equals(context) || uri.equals(context + "/")) {
            chain.doFilter(request, response);
            return;
        }
        // Allow public pages including terms and shopping guide
        if (isPublicPage(uri) || uri.endsWith("/terms") || uri.endsWith("/shopping-guide")) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(false);
        UsersDTO acc = session == null ? null : (UsersDTO) session.getAttribute("acc");
        if (acc == null) {
            res.sendRedirect(req.getContextPath() + "/dispatchcontroller?button=LoginPage");
            return;
        }

        // If account is locked after login, invalidate session immediately.
        UsersDTO latestUser = new UsersDAO().getUserById(acc.getId());
        if (latestUser == null || latestUser.getStatus() == 0) {
            if (session != null) {
                session.invalidate();
            }
            res.sendRedirect(req.getContextPath() + "/dispatchcontroller?button=LoginPage");
            return;
        }

        if (uri.contains("/admin")) {
            // Role 4 (Director): Được xem báo cáo doanh thu và quản lý category
            if (acc.getRole_id() == 4) {
                if (!uri.endsWith("/admin/dashboard") && !uri.endsWith("/admin/revenue-data") 
                        && !uri.endsWith("/admin/categories")
                        && !uri.endsWith("/admin/products")
                        && !uri.endsWith("/admin/orders")) {
                    res.sendRedirect(req.getContextPath() + "/dispatchcontroller?button=LoginPage");
                    return;
                }
            }
            // Role 1 (System Admin): Chỉ quản lý users và lịch sử hệ thống.
            else if (acc.getRole_id() == 1) {
                if (!uri.endsWith("/admin/users") && !uri.endsWith("/admin/systemlogs")) {
                    res.sendRedirect(req.getContextPath() + "/admin/users");
                    return;
                }
            }
            // Các role khác không được phép
            else {
                res.sendRedirect(req.getContextPath() + "/dispatchcontroller?button=LoginPage");
                return;
            }
        }

        if (uri.contains("/shop")) {
            res.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        if (uri.contains("/jsp/shop")) {
            res.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean isPublicPage(String uri) {
        return uri.endsWith("/jsp/login.jsp")
                || uri.endsWith("/jsp/register.jsp")
                || uri.endsWith("/jsp/conformEmail.jsp")
                || uri.endsWith("/jsp/HomePage.jsp")
                || uri.endsWith("/jsp/ProductList.jsp")
            || uri.endsWith("/jsp/productDetail.jsp")
                || uri.endsWith("/jsp/TempletProduct.jsp");
    }

    @Override
    public void init(javax.servlet.FilterConfig filterConfig) throws javax.servlet.ServletException {
        // Init logic
    }

    @Override
    public void destroy() {
        // Destroy logic
    }
}
