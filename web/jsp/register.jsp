<%-- 
    Document   : register
    Created on : Jan 30, 2026, 4:45:00 PM
    Author     : asus
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Đăng ký tài khoản</title>

        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/register.css?v=202605307b">
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <meta name="context-path" content="${pageContext.request.contextPath}">
    </head>
    <body class="d-flex flex-column min-vh-100" >

        <div>
            <jsp:include page="/jsp/header.jsp" />
        </div>

        <div class="container flex-grow-1 d-flex align-items-center justify-content-center py-5">
            <div class="login-box-limit" > <div class="card login-card shadow-lg border-0 pt-4 pb-4 px-4">
                    <div class="card-body">

                        <div class="text-center mb-4">
                            <h4 class="fw-bold mb-1 text-uppercase">Đăng ký tài khoản</h4>
                            <p class="text-muted small">Tạo tài khoản mới để trải nghiệm dịch vụ</p>
                        </div>

                        <p class="text-danger text-center small fw-bold">${mess}</p>
                        <p class="text-success text-center small fw-bold">${success}</p>

                        <%
                            List<String> suggestedUsers = (List<String>) request.getAttribute("suggestedUsers");
                            if (suggestedUsers != null && !suggestedUsers.isEmpty()) {
                        %>
                        <div class="alert alert-light border text-center py-2 mb-3" >
                            <i class="fas fa-lightbulb text-warning me-1"></i> Gợi ý Username:
                            <div class="d-flex gap-2 flex-wrap justify-content-center mt-2">
                                <% for (String s : suggestedUsers) { %>
                                <span class="badge bg-secondary fw-normal cursor-pointer" 
                                      onclick="document.getElementById('username').value = '<%= s %>'">
                                    <%= s %>
                                </span>
                                <% } %>
                            </div>
                        </div>
                        <% } %>

                        <form action="${pageContext.request.contextPath}/dispatchcontroller" method="post" id="registerForm">

                            <div class="mb-3">
                                <label class="fw-bold small mb-1">Email</label>
                                <input type="email" name="email" id="register-email" class="form-control form-control-soft" 
                                       value="${sessionScope.EMAIL_REGISTER}" readonly>
                            </div>

                            <div class="mb-3">
                                <label class="fw-bold small mb-1">Tên đăng nhập</label>
                                    <input type="text" name="username" id="username" class="form-control form-control-soft" 
                                        value="${oldUser}" placeholder="Ví dụ: hieunt, ngocanh123" required>
                            </div>

                            <div class="mb-3">
                                <label class="fw-bold small mb-1">Họ và tên</label>
                                    <input type="text" name="fullName" class="form-control form-control-soft" 
                                        value="${oldName}" placeholder="Nhập họ và tên (VD: Nguyễn Văn A)" required>
                            </div>

                            <div class="mb-3">
                                <label class="fw-bold small mb-1">Số điện thoại</label>
                                    <input type="text" name="phone" class="form-control form-control-soft" 
                                        value="${oldPhone}" placeholder="Nhập số điện thoại (VD: 0987654321)" required>
                            </div>

                            <div class="mb-3">
                                <label class="fw-bold small mb-1">Mật khẩu</label>
                                <div class="input-group">
                                     <input type="password" name="password" id="password" class="form-control form-control-soft" 
                                         placeholder="Tạo mật khẩu mạnh (tối thiểu 8 ký tự)" required>
                                    <span class="input-group-text bg-light cursor-pointer" onclick="toggleVisibility('password', this)">
                                        <i class="far fa-eye-slash text-muted"></i>
                                    </span>
                                </div>
                            </div>

                            <div class="mb-3">
                                <label class="fw-bold small mb-1">Nhập lại mật khẩu</label>
                                <div class="input-group">
                                     <input type="password" name="confirm" id="confirm" class="form-control form-control-soft" 
                                         placeholder="Nhập lại mật khẩu" required>
                                    <span class="input-group-text bg-light cursor-pointer" onclick="toggleVisibility('confirm', this)">
                                        <i class="far fa-eye-slash text-muted"></i>
                                    </span>
                                </div>
                            </div>

                            <ul class="validation-list mb-4">
                                <li class="validation-item" id="chk-lower">
                                    <i class="far fa-circle"></i> <span>Ít nhất một kí tự viết thường.</span>
                                </li>
                                <li class="validation-item" id="chk-upper">
                                    <i class="far fa-circle"></i> <span>Ít nhất một kí tự viết hoa.</span>
                                </li>
                                <li class="validation-item" id="chk-number">
                                    <i class="far fa-circle"></i> <span>Ít nhất một số.</span>
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

                            <button class="btn btn-success w-100 fw-bold py-2 text-uppercase" 
                                    type="submit" name="button" value="Register" id="submitBtn" disabled>
                                Đăng ký
                            </button>

                        </form>

                        <div class="text-center mt-3">
                            <a href="${pageContext.request.contextPath}/dispatchcontroller?button=LoginPage" class="text-decoration-none small text-muted">
                                <i class="fas fa-arrow-left me-1"></i> Quay lại đăng nhập
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="mt-auto">
            <jsp:include page="/jsp/footer.jsp" />
        </div>




        <script src="${pageContext.request.contextPath}/js/register.js"></script>
    </body>
</html>
