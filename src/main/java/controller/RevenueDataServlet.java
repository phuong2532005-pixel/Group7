/**
 *
 * Author : Nguyen Trung Hieu & Nguyen Thi Phuong Linh
 * Date : 27/02/2026
 * Description : Cung cap du lieu doanh thu (API) cho bieu do/dashboard.
 */
package controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import DAO.StatsDAO;

@WebServlet(name = "RevenueDataServlet", urlPatterns = {"/admin/revenue-data"})
public class RevenueDataServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String yearRaw = request.getParameter("year");
        String shopRaw = request.getParameter("shopId");
        int year = java.time.LocalDate.now().getYear();
        Integer shopId = null;
        try {
            if (yearRaw != null) year = Integer.parseInt(yearRaw);
        } catch (NumberFormatException e) {
        }
        try {
            if (shopRaw != null && !shopRaw.isEmpty()) shopId = Integer.parseInt(shopRaw);
        } catch (NumberFormatException e) {
        }

        // Access control: only Director (role_id = 4)
        javax.servlet.http.HttpSession session = request.getSession(false);
        model.UsersDTO acc = session == null ? null : (model.UsersDTO) session.getAttribute("acc");
        if (acc == null || acc.getRole_id() != 4) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().print("{\"error\":\"Access denied. Only Director can view revenue reports.\"}");
            return;
        }

        StatsDAO dao = new StatsDAO();
        List<Double> months = dao.getMonthlyRevenue(year, shopId);
        List<Integer> orderCounts = dao.getMonthlyOrderCount(year, shopId);

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        sb.append("\"year\":").append(year).append(',');
        sb.append("\"months\":");
        sb.append('[');
        for (int i = 0; i < months.size(); i++) {
            if (i > 0) sb.append(',');
            sb.append(String.format(java.util.Locale.US, "%.2f", months.get(i)));
        }
        sb.append(']');
        sb.append(',');
        sb.append("\"orderCounts\":");
        sb.append('[');
        for (int i = 0; i < orderCounts.size(); i++) {
            if (i > 0) sb.append(',');
            sb.append(orderCounts.get(i));
        }
        sb.append(']');
        sb.append('}');
        out.print(sb.toString());
        out.flush();
    }
}
