/**
 *
 * Author : Nguyen Trung Hieu & Nguyen Thi Phuong Linh
 * Date : 07/03/2026
 * Description : Hien thi lich su he thong / logs cho admin.
 */
package controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import DAO.SystemLogDAO;
import model.SystemLogDTO;
import model.UsersDTO;

@WebServlet(name = "SystemLogServlet", urlPatterns = {"/admin/systemlogs"})
public class SystemLogServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        UsersDTO acc = session == null ? null : (UsersDTO) session.getAttribute("acc");
        
        // Only Admin (role_id = 1) can access
        if (acc == null || acc.getRole_id() != 1) {
            response.sendRedirect(request.getContextPath() + "/dispatchcontroller?button=LoginPage");
            return;
        }

        String filterType = request.getParameter("filterType");
        if (filterType == null) {
            filterType = "all";
        }

        SystemLogDAO dao = new SystemLogDAO();
        List<SystemLogDTO> logs = new ArrayList<>();
        String logError = null;
        try {
            if ("all".equals(filterType)) {
                logs = dao.getAllLogs(500);
            } else if ("user".equals(filterType)) {
                int userId = acc.getId();
                logs = dao.getLogsByUser(userId, 500);
            }
        } catch (Exception ex) {
            logError = ex.getMessage();
            System.out.println("[SystemLogServlet] error fetching logs: " + logError);
            ex.printStackTrace();
        }
        
        // DEBUG LOGGING
        System.out.println("=== SystemLogServlet Debug ===");
        System.out.println("User: " + acc.getUsername() + " (ID=" + acc.getId() + ", Role=" + acc.getRole_id() + ")");
        System.out.println("FilterType: " + filterType);
        System.out.println("Total logs retrieved: " + logs.size());
        if (!logs.isEmpty()) {
            System.out.println("First log: " + logs.get(0).getAction() + " at " + logs.get(0).getLogTime());
        }
        System.out.println("=====================================");

        request.setAttribute("logs", logs);
        request.setAttribute("filterType", filterType);
        if (logError != null) {
            request.setAttribute("logError", logError);
        }
        request.getRequestDispatcher("/jsp/systemlogs.jsp").forward(request, response);
    }
}
