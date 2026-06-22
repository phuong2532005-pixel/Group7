/**
 *
 * Author : Nguyen Trung Hieu & Nguyen Thi Phuong Linh
 * Date : 04/03/2026
 * Description : API tim kiem thong minh san pham (autocomplete / goi y).
 */
package controller;

import DAO.ProductDAO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import model.SearchCandidateDTO;
import util.TextNormalizeUtil;

@WebServlet(name = "SmartProductSearchServlet", urlPatterns = {"/api/search/products"})
public class SmartProductSearchServlet extends HttpServlet {

    private static final int DEFAULT_LIMIT = 10;
    private static final int MAX_LIMIT = 30;
    private static final int CANDIDATE_LIMIT = 500;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        /* tra ve ket qua tim kiem thong minh (autocomplete/goi y) duoi dang json */
        response.setContentType("application/json;charset=UTF-8");

        String rawQuery = request.getParameter("q");
        int limit = parseLimit(request.getParameter("limit"));

        if (rawQuery == null || rawQuery.trim().isEmpty()) {
            response.getWriter().write("[]");
            return;
        }

        String normalizedQuery = normalize(rawQuery);
        if (normalizedQuery.isEmpty()) {
            response.getWriter().write("[]");
            return;
        }

        List<SearchCandidateDTO> candidates = new ProductDAO().getSearchCandidates(CANDIDATE_LIMIT);
        if (candidates.isEmpty()) {
            response.getWriter().write("[]");
            return;
        }

        List<ScoredResult> results = new ArrayList<>();
        for (SearchCandidateDTO candidate : candidates) {
            double matchScore = computeNameMatchScore(normalizedQuery, candidate.getName());
            if (matchScore <= 0d) {
                continue;
            }

            results.add(new ScoredResult(
                    candidate.getId(),
                    candidate.getName(),
                    "casual",
                    round4(matchScore)
            ));
        }

        results.sort(new Comparator<ScoredResult>() {
            @Override
            public int compare(ScoredResult a, ScoredResult b) {
                int byScore = Double.compare(b.getScore(), a.getScore());
                if (byScore != 0) {
                    return byScore;
                }
                return Integer.compare(b.getId(), a.getId());
            }
        });

        if (results.size() > limit) {
            results = results.subList(0, limit);
        }

        response.getWriter().write(toJson(results));
    }

    private int parseLimit(String value) {
        /* phan tich tham so limit va tra ve gia tri hop le */
        try {
            int parsed = Integer.parseInt(value);
            if (parsed < 1) {
                return DEFAULT_LIMIT;
            }
            return Math.min(parsed, MAX_LIMIT);
        } catch (Exception ex) {
            return DEFAULT_LIMIT;
        }
    }

    private String normalize(String raw) {
        /* chuan hoa chuoi: loai dau, bo ky tu dac biet va cat khoang trang */
        String normalized = TextNormalizeUtil.normalizeNoAccent(raw);
        if (normalized == null) {
            return "";
        }
        return normalized.replaceAll("[^a-z0-9\\s]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private List<String> tokenize(String text) {
        /* tach van ban thanh danh sach token hop le */
        if (text == null || text.isEmpty()) {
            return Collections.emptyList();
        }

        String[] parts = text.split("\\s+");
        List<String> tokens = new ArrayList<>();
        for (String part : parts) {
            if (part != null && !part.isEmpty()) {
                tokens.add(part);
            }
        }
        return tokens;
    }

    private double computeNameMatchScore(String normalizedQuery, String productName) {
        /* tinh diem khop giua query da chuan hoa va ten san pham */
        String normalizedName = normalize(productName);
        if (normalizedName.isEmpty()) {
            return 0d;
        }

        if (containsWholePhrase(normalizedName, normalizedQuery)) {
            return 1d;
        }

        List<String> queryTokens = tokenize(normalizedQuery);
        if (queryTokens.isEmpty()) {
            return 0d;
        }

        Set<String> uniqueTokens = new HashSet<>(queryTokens);
        int matchedCount = 0;
        for (String token : uniqueTokens) {
            if (containsWholeWord(normalizedName, token)) {
                matchedCount++;
            }
        }

        if (matchedCount <= 0) {
            return 0d;
        }

        return matchedCount / (double) uniqueTokens.size();
    }

    private boolean containsWholePhrase(String text, String phrase) {
        /* kiem tra neu toan bo cau hoi xuat hien lien tiep trong van ban */
        if (text == null || phrase == null || text.isEmpty() || phrase.isEmpty()) {
            return false;
        }
        String paddedText = " " + text + " ";
        String paddedPhrase = " " + phrase + " ";
        return paddedText.contains(paddedPhrase);
    }

    private boolean containsWholeWord(String text, String word) {
        /* kiem tra neu mot tu xuat hien nhu mot tu doc lap trong van ban */
        if (text == null || word == null || text.isEmpty() || word.isEmpty()) {
            return false;
        }
        String paddedText = " " + text + " ";
        String paddedWord = " " + word + " ";
        return paddedText.contains(paddedWord);
    }

    private double round4(double value) {
        /* lam tron diem den 4 chu so thap phan */
        return Math.round(value * 10000d) / 10000d;
    }

    private String toJson(List<ScoredResult> results) {
        /* chuyen danh sach ket qua thanh chuoi json */
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < results.size(); i++) {
            ScoredResult result = results.get(i);
            if (i > 0) {
                sb.append(',');
            }
            sb.append('{')
                    .append("\"id\":").append(result.getId())
                    .append(',')
                    .append("\"name\":\"").append(escapeJson(result.getName())).append("\"")
                    .append(',')
                    .append("\"style\":\"").append(escapeJson(result.getStyle())).append("\"")
                    .append(',')
                    .append("\"relevance_score\":").append(String.format(Locale.US, "%.4f", result.getScore()))
                    .append('}');
        }
        sb.append("]");
        return sb.toString();
    }

    private String escapeJson(String value) {
        /* thoat ky tu dac biet de su dung trong json */
        if (value == null) {
            return "";
        }
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private static final class ScoredResult {

        private final int id;
        private final String name;
        private final String style;
        private final double score;

        private ScoredResult(int id, String name, String style, double score) {
            this.id = id;
            this.name = name;
            this.style = style;
            this.score = score;
        }

        private int getId() {
            return id;
        }

        private String getName() {
            return name;
        }

        private String getStyle() {
            return style;
        }

        private double getScore() {
            return score;
        }
    }
}
