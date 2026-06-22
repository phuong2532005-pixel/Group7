/**
 *
 * Author : Nguyen Trung Hieu & Nguyen Thi Phuong Linh
 * Date : 10/02/2026
 * Description : Dieu huong va chuan bi du lieu cho trang dashboard (bao cao).
 */
package controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import DAO.StatsDAO;

@WebServlet(name = "DashboardController", urlPatterns = {"/admin/dashboard"})
public class DashboardController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        /* chuan bi du lieu thong ke va hien thi trang dashboard cho role thich hop */
        javax.servlet.http.HttpSession session = request.getSession(false);
        model.UsersDTO acc = session == null ? null : (model.UsersDTO) session.getAttribute("acc");
        
        // Role 1 (Admin): Redirect to user management page (no more access to revenue reports)
        if (acc != null && acc.getRole_id() == 1) {
            response.sendRedirect(request.getContextPath() + "/admin/users");
            return;
        }
        
        // Role 4 (Director): Allow to see revenue dashboard
        if (acc != null && acc.getRole_id() == 4) {
            StatsDAO dao = new StatsDAO();
            Map<String, Object> stats = dao.getDashboardStats();
            request.setAttribute("stats", stats);
            request.setAttribute("orderCountsByShop", dao.getOrderCountByShop());
            request.setAttribute("roleId", acc.getRole_id());
            request.getRequestDispatcher("/jsp/admin_dashboard.jsp").forward(request, response);
            return;
        }
        
        // Other roles: redirect to home
        response.sendRedirect(request.getContextPath() + "/home");
    }
}
