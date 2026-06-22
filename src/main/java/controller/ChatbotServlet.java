/**
 *
 * Author : Nguyen Trung Hieu & Nguyen Thi Phuong Linh
 * Date : 06/02/2026
 * Description : Endpoint cho AI/chatbot (giao tiep tro ly noi bo).
 */
package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

import model.UsersDTO;
import DAO.StatsDAO;

@WebServlet("/api/chatbot")
public class ChatbotServlet extends HttpServlet {

    private static final String GEMINI_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";

    private String API_KEY;
    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public void init() throws ServletException {
        try {
            Properties p = new Properties();
            InputStream is = getServletContext()
                    .getResourceAsStream("/WEB-INF/ConnectDB.properties");

            p.load(is);
            API_KEY = p.getProperty("gemini_api_key");

            if (API_KEY == null || API_KEY.isEmpty()) {
                throw new ServletException("Gemini API key not found!");
            }

            System.out.println("Gemini API loaded successfully");

        } catch (Exception e) {
            throw new ServletException("Cannot load API key", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(false);

        if (session == null) {
            out.print("{\"error\":\"Not logged in\"}");
            return;
        }

        UsersDTO user = (UsersDTO) session.getAttribute("acc");

        if (user == null) {
            out.print("{\"error\":\"Not logged in\"}");
            return;
        }

        String message = request.getParameter("message");

        if (message == null || message.trim().isEmpty()) {
            out.print("{\"error\":\"Message empty\"}");
            return;
        }

        try {

            // Lấy dữ liệu kinh doanh
            StatsDAO statsDAO = new StatsDAO();
            String businessData = "";
            String roleInstruction = "";

                if (user.getRole_id() == 4 || user.getRole_id() == 1) {
                businessData = statsDAO.getBusinessSummary(null);

                roleInstruction =
                        "Bạn là trợ lý AI giúp phân tích kinh doanh cho toàn doanh nghiệp.\n"
                                + "Dữ liệu doanh nghiệp:\n"
                                + businessData
                                + "\nHãy phân tích và đưa ra chiến lược.\n\n";

            } else {

                roleInstruction =
                        "Bạn là trợ lý AI phân tích kinh doanh. Trả lời bằng tiếng Việt.\n\n";
            }

            String prompt = roleInstruction + message;

            // Tạo body JSON gửi Gemini
            Map<String, Object> body = new HashMap<>();

            Map<String, String> part = new HashMap<>();
            part.put("text", prompt);

            List<Map<String, String>> parts = new ArrayList<>();
            parts.add(part);

            Map<String, Object> content = new HashMap<>();
            content.put("parts", parts);

            List<Map<String, Object>> contents = new ArrayList<>();
            contents.add(content);

            body.put("contents", contents);

            String json = mapper.writeValueAsString(body);

            URL url = new URL(GEMINI_URL + "?key=" + API_KEY);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(30000);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = json.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            String responseBody = "";
            InputStream responseStream = (responseCode >= 200 && responseCode < 300) 
                    ? conn.getInputStream() 
                    : conn.getErrorStream();
            
            if (responseStream != null) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(responseStream, "utf-8"))) {
                    StringBuilder sb = new StringBuilder();
                    String responseLine = null;
                    while ((responseLine = br.readLine()) != null) {
                        sb.append(responseLine.trim());
                    }
                    responseBody = sb.toString();
                }
            }

            if (responseCode != 200) {
                System.out.println("Gemini error code " + responseCode + ": " + responseBody);
                out.print("{\"error\":\"AI API error\"}");
                return;
            }

            Map result = mapper.readValue(responseBody, Map.class);

            List candidates = (List) result.get("candidates");

            if (candidates == null || candidates.isEmpty()) {
                out.print("{\"error\":\"No AI response\"}");
                return;
            }

            Map candidate = (Map) candidates.get(0);
            Map contentRes = (Map) candidate.get("content");

            List partsRes = (List) contentRes.get("parts");
            Map partRes = (Map) partsRes.get(0);

            String aiText = (String) partRes.get("text");

            Map<String, Object> responseJson = new HashMap<>();
            responseJson.put("success", true);
            responseJson.put("response", aiText);

            out.print(mapper.writeValueAsString(responseJson));

        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"error\":\"Server error\"}");
        }
    }
}
