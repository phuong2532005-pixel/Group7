<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Xác thực Email</title>

        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/forgot.css?v=202603007b">
            <link rel="stylesheet" href="${pageContext.request.contextPath}/css/fashionStyle.css?v=20260c307b">

        <script src="${pageContext.request.contextPath}/js/main.js?v=999"></script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <meta name="context-path" content="${pageContext.request.contextPath}">
    </head>

    <body class="d-flex flex-column min-vh-100">

        <div id="page-server-state" data-should-start="${not empty success}" class="d-none"></div>

        <div>
            <jsp:include page="/jsp/header.jsp" />
        </div>

        <div class="container flex-grow-1 d-flex align-items-center justify-content-center py-5">
            <div class="login-box-limit"> 

                <div class="card login-card shadow-lg border-0 pt-4 pb-4 px-3 position-relative">

                    <a href="${pageContext.request.contextPath}/dispatchcontroller?button=LoginPage" class="back-arrow-btn">
                        <i class="fas fa-arrow-left"></i>
                    </a>

                    <div class="card-body">

                        <div class="text-center mb-4">
                            <h4 class="fw-bold mb-1">Xác thực Email</h4>
                            <p class="text-muted small">Nhập email để nhận mã xác thực</p>
                        </div>

                        <c:if test="${not empty error}">
                            <div class="error-box">
                                <i class="fas fa-exclamation-triangle me-2"></i> <span>${error}</span>
                            </div>
                        </c:if>

                        <c:if test="${not empty success}">
                            <div class="alert alert-success text-center py-2 small fw-bold mb-3">${success}</div>
                        </c:if>

                        <form id="send-form" action="${pageContext.request.contextPath}/dispatchcontroller" method="post" class="mb-3">
                            <input type="hidden" name="button" value="SendRegisterCode">

                            <label class="fw-bold small mb-1">Email đăng ký</label>

                            <div class="input-group input-email-group">
                                <input type="text" name="email" value="${email}" 
                                       class="form-control ${not empty error ? 'is-invalid' : ''}" 
                                       placeholder="example@gmail.com"
                                       pattern="[a-zA-Z0-9._%+\-]+@[a-zA-Z0-9.\-]+\.[a-zA-Z]{2,}"
                                       title="Email phải có dạng abc@domain.com (ví dụ: abc@gmail.com)"
                                       required>

                                <button type="submit" class="btn-send-code">
                                    <span class="sending fas fa-paper-plane"></span>
                                    <i class="sending">Gửi Mã</i> 
                                </button>
                            </div>
                        </form>

                        <form id="verify-form" action="${pageContext.request.contextPath}/dispatchcontroller" method="post">
                            <input type="hidden" name="button" value="VerifyEmail">
                            <input type="hidden" name="email" value="${email}">

                            <div class="mb-4">
                                <label class="fw-bold small mb-1">Mã xác thực (OTP)</label>
                                <input type="text" name="code" class="form-control" 
                                       placeholder="######" required>
                            </div>

                            <div id="countdown-wrap" class="text-center mb-3 d-none">
                                <small class="text-danger fw-bold">Gửi lại sau <span id="countdown">60</span>s</small>
                            </div>

                            <button type="submit" id="verify-btn" class="btn btn-login-orange">
                                <span class="sending-sub">TIẾP THEO</span>
                            </button>
                        </form>
                    </div>
                </div>
            </div>
        </div>

        <div class="mt-auto">
            <jsp:include page="/jsp/footer.jsp" />
        </div>



    </body>
</html>