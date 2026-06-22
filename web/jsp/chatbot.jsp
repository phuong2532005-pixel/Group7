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

    <!-- CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/chatbot.css?v=202600308">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/fashionStyle.css?v=20260c307b">

    <style>
        body {
            background: #f5f7fb;
        }

        .chat-wrapper {
            max-width: 900px;
            margin: 30px auto;
            background: #fff;
            border-radius: 16px;
            overflow: hidden;
            box-shadow: 0 10px 30px rgba(0,0,0,0.08);
            display: flex;
            flex-direction: column;
            height: 85vh;
        }

        /* Header */
        .chat-header {
            background: linear-gradient(135deg, #1e3c72, #2a5298);
            color: #fff;
            padding: 16px 20px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .chat-header h2 {
            margin: 0;
            font-size: 18px;
        }

        .chat-info {
            font-size: 13px;
            opacity: 0.9;
        }

        .btn-back {
            color: #fff;
            text-decoration: none;
            background: rgba(255,255,255,0.15);
            padding: 8px 12px;
            border-radius: 8px;
        }

        .btn-back:hover {
            background: rgba(255,255,255,0.25);
        }

        /* Messages */
        .chat-messages {
            flex: 1;
            padding: 20px;
            overflow-y: auto;
            background: #f9fafc;
        }

        .message {
            padding: 10px 14px;
            border-radius: 10px;
            margin-bottom: 10px;
            max-width: 75%;
        }

        .info-message {
            background: #e9f2ff;
            color: #1a4b8c;
            padding: 10px 14px;
            border-radius: 10px;
            margin-bottom: 10px;
            display: inline-block;
        }

        /* Input */
        .chat-input-area {
            padding: 12px;
            border-top: 1px solid #eee;
            background: #fff;
        }

        .input-group textarea {
            resize: none;
            border-radius: 10px;
            padding: 10px;
        }

        .btn-send {
            background: #2a5298;
            color: #fff;
            border: none;
            padding: 0 18px;
            border-radius: 10px;
            margin-left: 8px;
        }

        .btn-send:hover {
            background: #1e3c72;
        }
    </style>
</head>

<body>

<div class="chat-wrapper">

    <!-- HEADER -->
    <div class="chat-header">
        <div>
            <h2>
                <i class="fas fa-robot"></i>
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

        <a href="javascript:history.back()" class="btn-back">
            <i class="fas fa-arrow-left"></i> Quay lại
        </a>
    </div>

    <!-- MESSAGES -->
    <div class="chat-messages" id="chatMessages">

        <div class="info-message">
            <i class="fas fa-lightbulb"></i>
            Xin chào! Tôi là trợ lý AI của bạn. Hãy hỏi tôi bất kỳ câu hỏi nào về kinh doanh!
        </div>

        <div class="info-message">
            <%
                if (acc.getRole_id() == 1 || acc.getRole_id() == 4) {
                    out.println("📊 Tôi sẽ phân tích toàn bộ hệ thống và đưa ra hướng đi cho doanh nghiệp.");
                }
            %>
        </div>

    </div>

    <!-- INPUT -->
    <div class="chat-input-area">
        <div class="input-group">
            <textarea id="messageInput" placeholder="Nhập câu hỏi của bạn..." rows="1"></textarea>

            <button class="btn-send" id="sendBtn" title="Gửi">
                <i class="fas fa-paper-plane"></i>
            </button>
        </div>
    </div>

</div>

<script>
    var contextPath = '${pageContext.request.contextPath}';
</script>

<script src="${pageContext.request.contextPath}/js/chatbot.js"></script>

</body>
</html>