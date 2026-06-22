<%-- 
    Document   : login
    Created on : Jan 30, 2026, 4:42:55 PM
    Author     : asus
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Đăng nhập hệ thống</title>

        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/login.css?v=202600307b">
        <link href="${pageContext.request.contextPath}/css/fashionStyle.css?v=200260307b" rel="stylesheet" type="text/css"/>
        <script src="${pageContext.request.contextPath}/js/main.js?=v3"></script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <meta name="context-path" content="${pageContext.request.contextPath}">
    </head>

    <body class="d-flex flex-column min-vh-100">

        <div>
            <jsp:include page="/jsp/header.jsp" />
        </div>

        <div class="container flex-grow-1 d-flex align-items-center justify-content-center py-5">

            <div class="login-box-limit"> <div class="card login-card shadow-lg border-0 p-4">
                    <div class="card-body">

                        <div class="text-center mb-4">
                            <h3 class="fw-bold text-dark">ĐĂNG NHẬP</h3>
                            <p class="text-muted small">Chào mừng bạn quay trở lại!</p>
                        </div>

                        <div id="login-container">

                            <div class="error-box">
                                <i class="fas fa-exclamation-triangle me-2"></i> 
                                <span>${mess}</span>
                            </div>

                            <c:if test="${not empty success}">
                                <div class="alert alert-success text-center py-2 mb-3">${success}</div>
                            </c:if>

                            <form id="login-form" action="${pageContext.request.contextPath}/dispatchcontroller" method="post">
                                <input type="hidden" name="returnUrl" value="${not empty param.returnUrl ? param.returnUrl : returnUrl}">

                                <div class="mb-3 input-group">
                                    <span class="input-group-text bg-white"><i class="fas fa-user-tag text-muted"></i></span>
                                    <select name="role" class="form-select" required>
                                        <option value="3" selected>Khách hàng</option>
                                        <option value="4">Giám Đốc</option>
                                        <option value="1">Admin</option>
                                    </select>
                                </div>

                                <div class="mb-3 input-group">
                                    <span class="input-group-text bg-white custom-icon"><i class="fas fa-user text-muted"></i></span>
                                    <input type="text" name="username" class="form-control" value="${oldUser}" placeholder="Username / Email / Số điện thoại" required>
                                </div>

                                <div class="mb-3 input-group">
                                    <span class="input-group-text bg-white"><i class="fas fa-lock text-muted"></i></span>
                                    <input type="password" name="password" id="password-field" class="form-control" value="${oldPass}" placeholder="Mật khẩu" required>
                                    <span class="input-group-text bg-white" style="cursor: pointer;" onclick="togglePwd()">
                                        <i class="far fa-eye" id="eye-icon"></i>
                                    </span>
                                </div>

                                <div class="form-check mb-3">
                                    <input class="form-check-input" type="checkbox" value="on" id="rememberMe" name="rememberMe" ${not empty oldUser ? 'checked' : ''}>
                                    <label class="form-check-label" for="rememberMe">
                                        Ghi nhớ đăng nhập
                                    </label>
                                </div>
                                <div class="d-flex justify-content-between mb-4 small">
                                    <a href="${pageContext.request.contextPath}/dispatchcontroller?button=ForgotPage" class="text-decoration-none fw-bold">Quên mật khẩu?- Hãy ấn vào đây</a>
                                    <a href="${pageContext.request.contextPath}/dispatchcontroller?button=RegisterEmail" class="text-decoration-none fw-bold">Đăng ký</a>
                                </div>

                                <button type="submit" name="button" value="Login" class="btn btn-login-orange w-100 fw-bold py-2 text-uppercase">
                                    Đăng nhập
                                </button>
                            </form>
                        </div>
                    </div>
                </div>

            </div>
        </div>

        <div class="mt-auto">
            <jsp:include page="/jsp/footer.jsp" />
        </div>



    </body>
</html>
