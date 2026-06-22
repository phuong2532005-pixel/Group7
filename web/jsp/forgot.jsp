<%-- 
    Document   : forgot
    Created on : Jan 30, 2026, 4:45:25 PM
    Author     : asus
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Quên mật khẩu</title>

        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/forgot.css?v=202600307b">
            <link rel="stylesheet" href="${pageContext.request.contextPath}/css/fashionStyle.css?v=20260c307b">

        <script src="${pageContext.request.contextPath}/js/forgot.js?v=5"></script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <meta name="context-path" content="${pageContext.request.contextPath}">
    </head>

    <body class="d-flex flex-column min-vh-100">

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
                            <h4 class="fw-bold mb-1">Quên mật khẩu</h4>
                            <p class="text-muted small">Điền thông tin để đặt lại mật khẩu mới</p>
                        </div>

                        <div class="text-danger text-center mb-2 fw-bold small">${error}</div>
                        <div class="text-success text-center mb-2 fw-bold small">${success}</div>

                        <div id="code-timer" class="text-center text-danger fw-bold mb-2 small" style="min-height: 20px;"></div>

                        <form action="${pageContext.request.contextPath}/forgot" method="post">

                            <input type="hidden" id="sentAt" value="${sentAt}">

                            <div class="mb-3">
                                <label class="fw-bold small mb-1">Email đăng ký</label>
                                <div class="input-group input-email-group">
                                    <input type="email" name="email" class="form-control" 
                                           placeholder="example@gmail.com" value="${email}" required>

                                    <button type="submit" id="send-code-btn" name="action" value="send" 
                                            class="btn-send-code" formnovalidate>
                                        <i class="fas fa-paper-plane"></i> Gửi Mã
                                    </button>
                                </div>
                            </div>

                            <div class="mb-3">
                                <label class="fw-bold small mb-1">Mã xác thực (OTP)</label>
                                <input type="text" name="code" class="form-control" placeholder="######" value="${code}">
                            </div>



                            <!-- when clicking xác nhận, we want to trigger reset logic in servlet -->
                            <button type="submit" id="verify-btn" name="action" value="reset" class="btn btn-login-orange">
                                Xác Nhận
                            </button>

                            <div class="text-center mt-3">
                                <a href="${pageContext.request.contextPath}/dispatchcontroller?button=LoginPage" class="text-decoration-none small text-muted">Quay lại đăng nhập</a>
                            </div>

                        </form> 

                    </div>
                </div>
            </div>
        </div>

        <div class="mt-auto">
            <jsp:include page="/jsp/footer.jsp" />
        </div>






        <script src="${pageContext.request.contextPath}/js/forgot.js"></script>
    </body>
</html>

