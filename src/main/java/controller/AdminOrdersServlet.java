/**
 *
 * Author : Nguyen Trung Hieu & Nguyen Thi Phuong Linh
 * Date : 31/01/2026
 * Description : Xem va xu ly don hang o khu vuc admin (duyet, cap nhat trang thai).
 */
package controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import DAO.OrderDAO;
import model.OrderItemSummaryDTO;
import model.OrderSummaryDTO;
import model.UsersDTO;

@WebServlet(name = "AdminOrdersServlet", urlPatterns = {"/admin/orders"})
public class AdminOrdersServlet extends HttpServlet {

    private static final int PAGE_SIZE = 20;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    /* hien thi danh sach don hang cho admin voi loc va phan trang */
        HttpSession session = request.getSession(false);
        UsersDTO acc = session == null ? null : (UsersDTO) session.getAttribute("acc");
        if (acc == null) {
            response.sendRedirect(request.getContextPath() + "/dispatchcontroller?button=LoginPage");
            return;
        }

        if (acc.getRole_id() != 4) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        String status = request.getParameter("status");
        String sort = request.getParameter("sort");
        int page = parsePage(request.getParameter("page"));
        int offset = (page - 1) * PAGE_SIZE;

        OrderDAO dao = new OrderDAO();
        int total = dao.countOrdersForDirector(status);
        int totalPages = (int) Math.ceil(total / (double) PAGE_SIZE);
        if (totalPages == 0) {
            totalPages = 1;
        }
        if (page > totalPages) {
            page = totalPages;
            offset = (page - 1) * PAGE_SIZE;
        }

        List<OrderSummaryDTO> orders = dao.listOrdersForDirector(status, sort, offset, PAGE_SIZE);
        Map<Integer, List<OrderItemSummaryDTO>> orderItemsMap = new LinkedHashMap<>();
        for (OrderSummaryDTO order : orders) {
            orderItemsMap.put(order.getId(), dao.listOrderItemsByOrderId(order.getId()));
        }

        request.setAttribute("orders", orders);
        request.setAttribute("orderItemsMap", orderItemsMap);
        request.setAttribute("statusFilter", status);
        request.setAttribute("sort", sort);
        request.setAttribute("page", page);
        request.setAttribute("totalPages", totalPages);
        request.getRequestDispatcher("/jsp/admin_orders.jsp").forward(request, response);
    }

    private int parsePage(String value) {
    /* chuyen tham so page sang int, tra ve 1 neu sai */
        try {
            int p = Integer.parseInt(value);
            return p < 1 ? 1 : p;
        } catch (Exception ex) {
            return 1;
        }
    }
}
