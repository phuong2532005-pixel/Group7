/**
 *
 * Author : Nguyen Trung Hieu & Nguyen Thi Phuong Linh
 * Date : 09/02/2026
 * Description : Hien thi lich su don hang cua khach hang (phan trang, trang thai).
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

@WebServlet(name = "CustomerOrdersServlet", urlPatterns = {"/orders"})
public class CustomerOrdersServlet extends HttpServlet {
    private static final int PAGE_SIZE = 20;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        /* hien thi lich su don hang cua khach hang ke ca phan trang va loc */
        HttpSession session = request.getSession(false);
        UsersDTO acc = session == null ? null : (UsersDTO) session.getAttribute("acc");
        if (acc == null) {
            response.sendRedirect(request.getContextPath() + "/dispatchcontroller?button=LoginPage");
            return;
        }
        
        String status = request.getParameter("status");
        int page = parsePage(request.getParameter("page"));
        int offset = (page - 1) * PAGE_SIZE;
        OrderDAO dao = new OrderDAO();
        
        int total = dao.countOrdersByUser(acc.getId(), status);
        int totalPages = (int) Math.ceil(total / (double) PAGE_SIZE);
        if (totalPages == 0) totalPages = 1;
        if (page > totalPages) { page = totalPages; offset = (page - 1) * PAGE_SIZE; }
        
        List<OrderSummaryDTO> orders = dao.listOrdersByUser(acc.getId(), status, offset, PAGE_SIZE);
        Map<Integer, List<OrderItemSummaryDTO>> orderItemsMap = new LinkedHashMap<>();
        for (OrderSummaryDTO order : orders) {
            orderItemsMap.put(order.getId(), dao.listOrderItemsByOrderId(order.getId()));
        }
        request.setAttribute("orders", orders);
        request.setAttribute("orderItemsMap", orderItemsMap);
        request.setAttribute("page", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("currentStatus", status);
        request.getRequestDispatcher("/jsp/orders.jsp").forward(request, response);
    }

    private int parsePage(String value) {
        /* chuyen tham so trang sang so nguyen hop le */
        try { int p = Integer.parseInt(value); return p < 1 ? 1 : p; } catch (Exception ex) { return 1; }
    }
}
