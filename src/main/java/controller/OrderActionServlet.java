/**
 *
 * Author : Nguyen Trung Hieu & Nguyen Thi Phuong Linh
 * Date : 20/02/2026
 * Description : Xu ly cac hanh dong tren don hang (thay doi trang thai, huy, redirect).
 */
package controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import DAO.OrderDAO;
import DAO.SystemLogDAO;
import model.UsersDTO;

@WebServlet(name = "OrderActionServlet", urlPatterns = {"/order/action"})
public class OrderActionServlet extends HttpServlet {

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
        String idRaw = request.getParameter("orderId");
        int orderId;
        try { orderId = Integer.parseInt(idRaw); } catch (Exception ex) { response.sendRedirect(request.getHeader("Referer")); return; }
        OrderDAO dao = new OrderDAO();
        boolean updated = false;
        String referer = request.getHeader("Referer");

        if (acc.getRole_id() == 4) {
            // Director actions: accept -> Processing, ship -> Shipped
            if ("accept".equals(action)) {
                updated = dao.updateOrderStatusConditional(orderId, "PENDING", "PROCESSING");
                if (updated) new SystemLogDAO().insertLog(acc.getId(), "Director accepted order " + orderId);
            } else if ("ship".equals(action)) {
                updated = dao.updateOrderStatusConditional(orderId, "PROCESSING", "SHIPPED");
                if (updated) new SystemLogDAO().insertLog(acc.getId(), "Director shipped order " + orderId);
            }
            response.sendRedirect(referer != null ? referer : request.getContextPath() + "/admin/orders");
            return;
        }

        if (acc.getRole_id() == 3) {
            // customer actions: cancel (when Pending) -> Cancelled, received (when Shipped) -> Delivered
            Integer userId = acc.getId();
            Integer orderUserId = dao.getOrderUserId(orderId);
            if (orderUserId == null || !orderUserId.equals(userId)) {
                response.sendRedirect(referer != null ? referer : request.getContextPath() + "/orders");
                return;
            }
            if ("cancel".equals(action)) {
                updated = dao.updateOrderStatusConditional(orderId, "PENDING", "CANCELLED");
                if (updated) new SystemLogDAO().insertLog(acc.getId(), "User cancelled order " + orderId);
            } else if ("received".equals(action)) {
                updated = dao.updateOrderStatusConditional(orderId, "SHIPPED", "DELIVERED");
                if (updated) new SystemLogDAO().insertLog(acc.getId(), "User received order " + orderId);
            }
            response.sendRedirect(referer != null ? referer : request.getContextPath() + "/orders");
            return;
        }

        // fallback
        response.sendRedirect(referer != null ? referer : request.getContextPath() + "/");
    }
}
