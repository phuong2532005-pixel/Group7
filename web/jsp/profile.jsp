<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="model.AddressDTO"%>
<%@page import="model.UsersDTO"%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Hồ sơ cá nhân</title>

        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/css/profile.css?v=5655495259v" rel="stylesheet" type="text/css"/>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/fashionStyle.css?v=20260c307b">

        <meta name="context-path" content="${pageContext.request.contextPath}">
    </head>
    <%
        UsersDTO acc = (UsersDTO) session.getAttribute("acc");
        System.out.println("[PROFILE PAGE] Loaded - acc: " + (acc != null ? acc.getUsername() + " (role:" + acc.getRole_id() + ")" : "NULL"));
        String activeSection = (String) request.getAttribute("activeSection");
        String oldPasswordValue = (String) request.getAttribute("oldPasswordValue");
        String newPasswordValue = (String) request.getAttribute("newPasswordValue");
        String confirmPasswordValue = (String) request.getAttribute("confirmPasswordValue");
    %>
    <body class="d-flex flex-column min-vh-100"  data-user-role="<%= acc != null ? acc.getRole_id() : 0 %>" data-initial-section="<%= activeSection != null ? activeSection : "" %>">

        <jsp:include page="/jsp/header.jsp" />

        <%
            String message = (String) request.getAttribute("message");
            String error = (String) request.getAttribute("error");
            if (message == null || message.trim().isEmpty()) {
                message = request.getParameter("message");
            }
            if (error == null || error.trim().isEmpty()) {
                error = request.getParameter("error");
            }
    
            // Các biến session cho logic xác thực (Verify)
            String verifyMode = (String) session.getAttribute("VERIFY_MODE"); 
            String pendingEmail = (String) session.getAttribute("PENDING_EMAIL");
            List<AddressDTO> addresses = (List<AddressDTO>) request.getAttribute("addresses");
    

        %>

        <div class="container flex-grow-1 py-5">
            <div class="row justify-content-center">
                <div class="col-md-11 col-lg-10 col-xl-9">

                    <div class="card shadow-lg border-0 profile-shell">
                        <div class="card-body p-4 p-lg-5">
                            <h4 class="main-profile-title mb-4">Hồ sơ cá nhân</h4>

                            <% if (acc == null) { %>
                            <div class="text-center py-5">
                                <p>Vui lòng đăng nhập để xem hồ sơ.</p>
                                <a href="${pageContext.request.contextPath}/dispatchcontroller?button=LoginPage" class="btn btn-primary">Đăng nhập ngay</a>
                            </div>
                            <% } else { %>

                            <div class="profile-layout">
                                <div class="profile-sidebar">
                                    <button type="button" class="profile-nav-btn active" data-section="info"><i class="fas fa-user-edit me-2"></i>Thông tin chung</button>
                                    <% if (acc.getRole_id() == 3) { %>
                                    <button type="button" class="profile-nav-btn" data-section="address"><i class="fas fa-location-dot me-2"></i>Địa chỉ giao hàng</button>
                                    <button type="button" class="profile-nav-btn" data-section="orders" data-direct-url="${pageContext.request.contextPath}/orders"><i class="fas fa-receipt me-2"></i>Đơn đã mua</button>
                                    <% } %>
                                    <button type="button" class="profile-nav-btn" data-section="password"><i class="fas fa-key me-2"></i>Đổi mật khẩu</button>
                                </div>

                                <div class="profile-main">
                                    <% if (error != null) { %>
                                    <div class="profile-inline-alert profile-inline-alert-error"><%= error %></div>
                                    <% } %>
                                    <% if (message != null) { %>
                                    <div class="profile-inline-alert profile-inline-alert-success"><%= message %></div>
                                    <% } %>

                                    <div class="profile-section active" id="section-info">

                                        <form action="${pageContext.request.contextPath}/dispatchcontroller" method="post" class="profile-form mb-4">

                                            <input type="hidden" name="button" value="Profile">
                                            <input type="hidden" name="action" value="updateInfo">

                                            <h6 class="profile-title">
                                                <i class="fas fa-user-edit me-2"></i> Thông tin chung
                                            </h6>

                                            <div class="profile-grid">

                                                <div class="profile-field">
                                                    <label>Họ và tên</label>
                                                    <input type="text" name="fullName" class="form-control form-control-soft"
                                                           value="${not empty oldFullName ? oldFullName : acc.full_name}" required>
                                                </div>

                                                <div class="profile-field">
                                                    <label>Số điện thoại</label>
                                                    <input type="text" name="phone" class="form-control form-control-soft"
                                                           value="${not empty oldPhone ? oldPhone : acc.phone}" required
                                                           pattern="\d{10}" title="Chỉ gồm 10 chữ số">
                                                </div>

                                                <div class="profile-field">
                                                    <label>Email</label>
                                                    <input type="email" name="email" class="form-control form-control-soft"
                                                           value="${not empty oldEmail ? oldEmail : acc.email}" required>
                                                </div>

                                            </div>

                                            <button class="btn-pro btn-primary profile-save-btn" type="submit">
                                                <i class="fas fa-save me-1"></i> Cập nhật thông tin
                                            </button>

                                            </form>
                                            <% if ("changeEmail".equals(verifyMode) || "changeInfo".equals(verifyMode)) { %>
                                            <div class="password-verify-card">
                                                <h6 class="password-verify-title">
                                                    <i class="fas fa-shield-alt me-2"></i> Xác thực yêu cầu
                                                </h6>

                                                <div class="password-verify-text">
                                                    <% if ("changeInfo".equals(verifyMode)) { %>
                                                    Hệ thống đã gửi mã xác nhận tới email hiện tại của bạn để xác nhận thay đổi thông tin cá nhân.
                                                    <% } else { %>
                                                    Hệ thống đã gửi mã đến email mới: <b><%= pendingEmail %></b>
                                                    <% } %>
                                                </div>

                                                <form action="${pageContext.request.contextPath}/dispatchcontroller" method="post" class="password-verify-form">
                                                    <input type="hidden" name="button" value="Profile">
                                                    <input type="hidden" name="action" value="verifyCode">
                                                    <input type="hidden" name="oldPassword" value="<%= oldPasswordValue == null ? "" : oldPasswordValue %>">
                                                    <input type="hidden" name="newPassword" value="<%= newPasswordValue == null ? "" : newPasswordValue %>">
                                                    <input type="hidden" name="confirmPassword" value="<%= confirmPasswordValue == null ? "" : confirmPasswordValue %>">

                                                    <div class="password-verify-row">
                                                        <input type="text" name="code" class="form-control form-control-soft password-otp-input" placeholder="Nhập mã xác nhận (OTP)" required>
                                                        <button class="btn-pro password-confirm-btn" type="submit">Xác nhận</button>
                                                    </div>
                                                </form>
                                            </div>
                                            <% } %>
                                        </form>

                                    </div>

                                    <% if (acc.getRole_id() == 3) { %>
                                    <div class="profile-section" id="section-address">
                                        <div class="pt-2 mb-3">
                                            <div class="address-header border-bottom pb-2 mb-2">
                                                <h6 class="fw-bold text-muted mb-0">
                                                    <i class="fas fa-location-dot me-2"></i> Địa chỉ của tôi
                                                </h6>
                                                <button type="button" class="address-add-btn" id="toggleAddAddressBtn">
                                                    <i class="fas fa-plus me-1"></i> Thêm địa chỉ mới
                                                </button>
                                            </div>

                                            <div id="addAddressFormWrap" class="mb-3" style="display: none;">
                                                <form action="${pageContext.request.contextPath}/dispatchcontroller" method="post" class="border rounded p-3 bg-light" id="addAddressForm">
                                                    <input type="hidden" name="button" value="Profile">
                                                    <input type="hidden" name="action" value="addAddress">

                                                    <div class="mb-2">
                                                        <label class="fw-bold small mb-1">Địa chỉ chi tiết</label>
                                                        <input type="text" name="street" id="addStreet" class="form-control form-control-soft" placeholder="Số nhà, tên đường..." required>
                                                    </div>
                                                    <div class="mb-2">
                                                        <label class="fw-bold small mb-1">Tỉnh/Thành phố</label>
                                                        <select name="city" id="addCity" class="form-control form-control-soft" required>
                                                            <option value="">-- Chọn tỉnh/thành phố --</option>
                                                        </select>
                                                    </div>
                                                    <div class="mb-2">
                                                        <label class="fw-bold small mb-1">Quận/Huyện</label>
                                                        <select name="district" id="addDistrict" class="form-control form-control-soft" required disabled>
                                                            <option value="">-- Chọn quận/huyện --</option>
                                                        </select>
                                                    </div>
                                                    <div class="mb-2">
                                                        <label class="fw-bold small mb-1">Phường/Xã</label>
                                                        <select id="addWard" class="form-control form-control-soft" disabled>
                                                            <option value="">-- Chọn phường/xã --</option>
                                                        </select>
                                                    </div>
                                                    <div class="form-check mb-3">
                                                        <input class="form-check-input" type="checkbox" name="isDefault" id="isDefaultAddress" value="1">
                                                        <label class="form-check-label small" for="isDefaultAddress">Đặt làm địa chỉ mặc định</label>
                                                    </div>
                                                    <button class="btn-pro btn-primary w-100 fw-bold" type="submit">
                                                        <i class="fas fa-plus me-1"></i> Thêm địa chỉ
                                                    </button>
                                                </form>
                                            </div>

                                            <div>
                                                <% if (addresses == null || addresses.isEmpty()) { %>
                                                <div class="small text-muted py-3">Bạn chưa có địa chỉ giao hàng nào.</div>
                                                <% } else { %>
                                                <% for (AddressDTO a : addresses) { %>
                                                <div class="address-item">
                                                    <div class="address-left">
                                                        <div class="address-contact">
                                                            <strong><%= acc.getFull_name() %></strong>
                                                            <span class="address-phone">| <%= acc.getPhone() == null ? "" : acc.getPhone() %></span>
                                                        </div>
                                                        <div class="address-line"><%= a.getStreet() %></div>
                                                        <div class="address-line"><%= a.getDistrict() %>, <%= a.getCity() %></div>
                                                        <% if (a.isIsDefault()) { %>
                                                        <span class="address-default">Mặc định</span>
                                                        <% } %>

                                                        <div class="address-edit-wrap update-form-wrap mt-3" id="editAddressWrap_<%= a.getId() %>" style="display: none;">
                                                            <form action="${pageContext.request.contextPath}/dispatchcontroller" method="post" class="mb-2">
                                                                <input type="hidden" name="button" value="Profile">
                                                                <input type="hidden" name="action" value="updateAddress">
                                                                <input type="hidden" name="addressId" value="<%= a.getId() %>">

                                                                <div class="mb-1">
                                                                    <input type="text" name="street" class="form-control form-control-sm" value="<%= a.getStreet() %>" required>
                                                                </div>
                                                                <div class="mb-1">
                                                                    <input type="text" name="district" class="form-control form-control-sm" value="<%= a.getDistrict() %>" required>
                                                                </div>
                                                                <div class="mb-1">
                                                                    <input type="text" name="city" class="form-control form-control-sm" value="<%= a.getCity() %>" required>
                                                                </div>
                                                                <div class="form-check mb-2">
                                                                    <input class="form-check-input" type="checkbox" name="isDefault" value="1" id="default_<%= a.getId() %>" <%= a.isIsDefault() ? "checked" : "" %>>
                                                                    <label class="form-check-label" for="default_<%= a.getId() %>">Đặt làm mặc định</label>
                                                                </div>
                                                                <button class="btn btn-sm btn-outline-primary" type="submit">Lưu sửa</button>
                                                            </form>
                                                        </div>
                                                    </div>

                                                    <div class="address-actions">
                                                        <div class="address-links">
                                                            <button type="button" class="address-link-btn btn-edit-address" data-id="<%= a.getId() %>">Cập nhật</button>
                                                            <form action="${pageContext.request.contextPath}/dispatchcontroller" method="post" onsubmit="return confirm('Xóa địa chỉ này?');" >
                                                                <input type="hidden" name="button" value="Profile">
                                                                <input type="hidden" name="action" value="deleteAddress">
                                                                <input type="hidden" name="addressId" value="<%= a.getId() %>">
                                                                <button class="address-link-btn" type="submit">Xóa</button>
                                                            </form>
                                                        </div>

                                                        <form action="${pageContext.request.contextPath}/dispatchcontroller" method="post" >
                                                            <input type="hidden" name="button" value="Profile">
                                                            <input type="hidden" name="action" value="updateAddress">
                                                            <input type="hidden" name="addressId" value="<%= a.getId() %>">
                                                            <input type="hidden" name="street" value="<%= a.getStreet() %>">
                                                            <input type="hidden" name="district" value="<%= a.getDistrict() %>">
                                                            <input type="hidden" name="city" value="<%= a.getCity() %>">
                                                            <input type="hidden" name="isDefault" value="1">
                                                            <button class="address-default-btn" type="submit" <%= a.isIsDefault() ? "disabled" : "" %>>Thiết lập mặc định</button>
                                                        </form>
                                                    </div>
                                                </div>
                                                <% } %>
                                                <% } %>
                                            </div>
                                        </div>
                                    </div>
                                    <% } %>

                                    <div class="profile-section" id="section-password">
                                        <div class="password-panel">
                                            <h6 class="profile-title password-title">
                                                <i class="fas fa-key me-2"></i> Đổi mật khẩu
                                            </h6>

                                            <form action="${pageContext.request.contextPath}/dispatchcontroller" method="post" id="changePassForm" class="password-request-form">
                                                <input type="hidden" name="button" value="Profile">
                                                <input type="hidden" name="action" value="requestPasswordChange">

                                                <div class="password-form-grid">
                                                    <div class="password-field-group">
                                                        <label class="fw-bold small mb-1">Mật khẩu hiện tại</label>
                                                        <div class="input-group password-input-group">
                                                        <input type="password" name="oldPassword" id="oldPassword" 
                                                               class="form-control form-control-soft" placeholder="Nhập mật khẩu hiện tại" value="<%= oldPasswordValue == null ? "" : oldPasswordValue %>" required>
                                                        <span class="input-group-text bg-light cursor-pointer" onclick="toggleVisibility('oldPassword', this)">
                                                            <i class="far fa-eye-slash text-muted"></i>
                                                        </span>
                                                    </div>
                                                    </div>

                                                    <div class="password-field-group">
                                                        <label class="fw-bold small mb-1">Mật khẩu mới</label>
                                                        <div class="input-group password-input-group">
                                                        <input type="password" name="newPassword" id="newPassword" 
                                                               class="form-control form-control-soft" placeholder="Nhập mật khẩu mới" value="<%= newPasswordValue == null ? "" : newPasswordValue %>" required>
                                                        <span class="input-group-text bg-light cursor-pointer" onclick="toggleVisibility('newPassword', this)">
                                                            <i class="far fa-eye-slash text-muted"></i>
                                                        </span>
                                                    </div>
                                                    </div>

                                                    <div class="password-field-group">
                                                        <label class="fw-bold small mb-1">Xác nhận mật khẩu</label>
                                                        <div class="input-group password-input-group">
                                                        <input type="password" name="confirmPassword" id="confirmPassword" 
                                                               class="form-control form-control-soft" placeholder="Nhập lại mật khẩu" value="<%= confirmPasswordValue == null ? "" : confirmPasswordValue %>" required>
                                                        <span class="input-group-text bg-light cursor-pointer" onclick="toggleVisibility('confirmPassword', this)">
                                                            <i class="far fa-eye-slash text-muted"></i>
                                                        </span>
                                                    </div>
                                                    </div>
                                                </div>

                                                <ul class="validation-list password-validation-list mb-3">
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
                                                        <i class="far fa-circle"></i> <span>Độ dài 8-16 kí tự.</span>
                                                    </li>
                                                    <li class="validation-item" id="chk-match">
                                                        <i class="far fa-circle"></i> <span>Mật khẩu trùng khớp.</span>
                                                    </li>
                                                </ul>

                                                <button class="btn-pro password-submit-btn w-100 fw-bold text-white" type="submit" id="submitPassBtn" disabled>
                                                    <i class="fas fa-envelope me-1"></i> Gửi mã đổi mật khẩu
                                                </button>
                                            </form>
                                        </div>

                                        <% if ("changePassword".equals(verifyMode)) { %>
                                        <div class="password-verify-card">
                                            <h6 class="password-verify-title">
                                                <i class="fas fa-shield-alt me-2"></i> Xác thực yêu cầu
                                            </h6>

                                            <div class="password-verify-text">
                                                Hệ thống đã gửi mã xác nhận đến email hiện tại của bạn.
                                            </div>

                                            <form action="${pageContext.request.contextPath}/dispatchcontroller" method="post" class="password-verify-form">
                                                <input type="hidden" name="button" value="Profile">
                                                <input type="hidden" name="action" value="verifyCode">
                                                <input type="hidden" name="oldPassword" value="<%= oldPasswordValue == null ? "" : oldPasswordValue %>">
                                                <input type="hidden" name="newPassword" value="<%= newPasswordValue == null ? "" : newPasswordValue %>">
                                                <input type="hidden" name="confirmPassword" value="<%= confirmPasswordValue == null ? "" : confirmPasswordValue %>">

                                                <div class="password-verify-row">
                                                    <input type="text" name="code" class="form-control form-control-soft password-otp-input" placeholder="Nhập mã xác nhận (OTP)" required>
                                                    <button class="btn-pro password-confirm-btn" type="submit">Xác nhận</button>
                                                </div>
                                            </form>
                                        </div>
                                        <% } %>
                                    </div>

                                    <% if (acc.getRole_id() == 3) { %>
                                    <div class="profile-section" id="section-orders">
                                        <h6 class="fw-bold text-muted mb-3 border-bottom pb-2">
                                            <i class="fas fa-receipt me-2"></i> Đơn đã mua
                                        </h6>
                                        <a href="${pageContext.request.contextPath}/orders" class="btn-pro btn-outline-dark w-100 fw-bold d-inline-block text-center" >
                                            <i class="fas fa-receipt me-1"></i> Xem các đơn đã mua
                                        </a>
                                    </div>
                                    <% } %>
                                </div>
                            </div>

                            <% } %> </div>
                    </div>
                </div>
            </div>
        </div>

        <jsp:include page="/jsp/footer.jsp" />
        <script src="${pageContext.request.contextPath}/js/profile.js"></script>
