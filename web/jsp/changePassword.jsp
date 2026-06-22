<%-- 
    Document   : changePassword
    Created on : Feb 9, 2026, 5:21:04 PM
    Author     : asus
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Thiết lập mật khẩu</title>

        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/changepass.css?v=202600307b">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/fashionStyle.css?v=202600307b">
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <meta name="context-path" content="${pageContext.request.contextPath}">
    </head>

    <body class="d-flex flex-column min-vh-100" >

        <div>
            <jsp:include page="/jsp/header.jsp" />
        </div>

        <div class="container flex-grow-1 d-flex align-items-center justify-content-center py-5">
            <div class="login-box-limit">

                <div class="card login-card shadow-lg border-0 pt-4 pb-4 px-4">
                    <div class="card-body">

                        <div class="text-center mb-4">
                            <h4 class="fw-bold mb-1 text-uppercase">Thiết Lập Mật Khẩu</h4>
                            <p class="text-muted small">Tạo mật khẩu mới cho tài khoản của bạn</p>
                        </div>

                        <c:if test="${not empty error}">
                            <div class="alert alert-danger text-center py-2 small fw-bold mb-3">${error}</div>
                        </c:if>
                        <c:if test="${not empty message}">
                            <div class="alert alert-info text-center py-2 small fw-bold mb-3">${message}</div>
                        </c:if>

                        <!-- submit back to the forgot servlet which handles the reset logic -->
                        <form action="${pageContext.request.contextPath}/forgot" method="post" id="resetForm">
                            <input type="hidden" name="action" value="reset">
                            <input type="hidden" name="email" value="${email}">
                            <input type="hidden" name="code" value="${code}">

                            <div class="mb-3">
                                <label class="fw-bold small mb-1">Mật khẩu mới</label>
                                <div class="input-group">
                                    <input type="password" name="newPassword" id="newPass" 
                                           class="form-control form-control-soft" placeholder="••••••••" required>
                                    <span class="input-group-text bg-light cursor-pointer" onclick="toggleVisibility('newPass', this)">
                                        <i class="far fa-eye-slash text-muted"></i>
                                    </span>
                                </div>
                            </div>

                            <div class="mb-3">
                                <label class="fw-bold small mb-1">Nhập lại mật khẩu</label>
                                <div class="input-group">
                                    <!-- name changed to match servlet expectation -->
                                    <input type="password" name="confirm" id="confirmPass" 
                                           class="form-control form-control-soft" placeholder="••••••••" required>
                                    <span class="input-group-text bg-light cursor-pointer" onclick="toggleVisibility('confirmPass', this)">
                                        <i class="far fa-eye-slash text-muted"></i>
                                    </span>
                                </div>
                            </div>

                            <ul class="validation-list">
                                <li class="validation-item" id="chk-lower">
                                    <i class="far fa-circle"></i> <span>Ít nhất một kí tự viết thường.</span>
                                </li>
                                <li class="validation-item" id="chk-upper">
                                    <i class="far fa-circle"></i> <span>Ít nhất một kí tự viết hoa.</span>
                                </li>
                                <li class="validation-item" id="chk-special">
                                    <i class="far fa-circle"></i> <span>Ít nhất một kí tự đặc biệt (!@#...).</span>
                                </li>
                                <li class="validation-item" id="chk-len">
                                    <i class="far fa-circle"></i> <span>Độ dài tối thiểu là 8 kí tự.</span>
                                </li>
                                <li class="validation-item" id="chk-match">
                                    <i class="far fa-circle"></i> <span>Mật khẩu trùng khớp.</span>
                                </li>
                            </ul>

                            <button type="submit" id="submitBtn" class="btn btn-login-orange" disabled>
                                XÁC NHẬN ĐỔI
                            </button>

                        </form>
                    </div>
                </div>
            </div>
        </div>

        <div class="mt-auto">
            <jsp:include page="/jsp/footer.jsp" />
        </div>




        <script src="${pageContext.request.contextPath}/js/changePassword.js"></script>
    </body>
</html>