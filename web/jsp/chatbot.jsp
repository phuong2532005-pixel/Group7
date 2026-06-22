<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="model.UsersDTO" %>
<%
    UsersDTO acc = (UsersDTO) session.getAttribute("acc");
    // Chỉ hiển thị cho admin (role 1) và director (role 4)
    if (acc == null || (acc.getRole_id() != 1 && acc.getRole_id() != 4)) {
        response.sendRedirect(request.getContextPath() + "/dispatchcontroller?button=LoginPage");
        return;
    }
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>AI Chatbot - Trợ lý Kinh Doanh</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/chatbot.css?v=202600308">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/fashionStyle.css?v=20260c307b">

    
</head>
<body>
    <div class="chatbot-container">
        <!-- Header -->
        <div class="chat-header">
            <div>
                <h2>
                    <i class="fas fa-robot chat-header-icon"></i>
                    AI Trợ Lý Kinh Doanh
                </h2>
                <div class="chat-info">
                    <%
                        if (acc.getRole_id() == 1 || acc.getRole_id() == 4) {
                            out.println("Giám Đốc/Admin - Chiến lược toàn doanh nghiệp");
                        }
                    %>
                </div>
            </div>
            <div>
                <a href="javascript:history.back()" class="btn-back">
                    <i class="fas fa-arrow-left"></i> Quay lại
                </a>
            </div>
        </div>

        <!-- Messages -->
        <div class="chat-messages" id="chatMessages">
            <div class="info-message"><i class="fas fa-lightbulb"></i> Xin chào! Tôi là trợ lý AI của bạn. Hãy hỏi tôi bất kỳ câu hỏi nào về kinh doanh!</div>
            <div class="info-message">
                <%
                    if (acc.getRole_id() == 1 || acc.getRole_id() == 4) {
                        out.println("📊 Tôi sẽ phân tích toàn bộ hệ thống và đưa ra hướng đi cho doanh nghiệp.");
                    }
                %>
            </div>
        </div>

        <!-- Input -->
        <div class="chat-input-area">
            <div class="input-group">
                <textarea id="messageInput" placeholder="Nhập câu hỏi của bạn..." rows="1"></textarea>
                <button class="btn-send" id="sendBtn" title="Gửi">
                    <i class="fas fa-paper-plane"></i>
                </button>
            </div>
        </div>
    </div>

     <script>var contextPath = '${pageContext.request.contextPath}';</script>
     <script src="${pageContext.request.contextPath}/js/chatbot.js"></script>
</body>
</html>