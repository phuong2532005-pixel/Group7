/**
 *
 * Author : Nguyen Trung Hieu & Nguyen Thi Phuong Linh
 * Date : 05/03/2026
 * Description : Goi y cua hang hoac chi nhanh lien quan den san pham/nguoi dung.
 */
package controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "SuggestShopServlet", urlPatterns = {"/suggest-shop"})
public class SuggestShopServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendError(HttpServletResponse.SC_GONE, "Suggest shop feature has been removed.");
    }
}
