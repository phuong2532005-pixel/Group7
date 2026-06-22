/**
 *
 * Author : Nguyen Trung Hieu & Nguyen Thi Phuong Linh
 * Date : 08/03/2026
 * Description : Hien thi dieu khoan, chinh sach trang web.
 */
package controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

@WebServlet(name = "TermsServlet", urlPatterns = {"/terms"})
public class TermsServlet extends HttpServlet {

    private String getContentFilePath() {
        String realPath = getServletContext().getRealPath("/WEB-INF");
        return realPath + File.separator + "terms_content.txt";
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String content = readContentFromFile();
        request.setAttribute("termsContent", content);
        request.getRequestDispatcher("/jsp/terms.jsp").forward(request, response);
    }

    private String readContentFromFile() {
        String filePath = getContentFilePath();
        File file = new File(filePath);
        
        if (!file.exists()) {
            return ""; // Return empty if file doesn't exist, JSP will show default
        }
        
        try {
            return new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
}
