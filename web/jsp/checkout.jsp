<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Thanh toan</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/checkout.css?v=202603007b">
            <link rel="stylesheet" href="${pageContext.request.contextPath}/css/fashionStyle.css?v=200260c307b">

        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">   
        <meta name="context-path" content="${pageContext.request.contextPath}">
    </head>
    <body>
        <jsp:include page="/jsp/header.jsp" />

        <div class="checkout-wrap">
            <h2 class="section-title">Thanh toán</h2>

            <c:if test="${not empty success}">
                <div class="notice">Đặt hàng thành công. Mã đơn: #${orderId}</div>
            </c:if>
            <c:if test="${not empty error}">
                <div class="error">${error}</div>
            </c:if>

            <div class="checkout-grid">
                <div class="panel">
                    <h3>Thông tin giao hàng</h3>
                    <form action="${pageContext.request.contextPath}/checkout" method="post" id="checkoutForm">
                        <!-- Preserve selected variants from cart -->
                        <c:if test="${not empty selectedVariants}">
                            <input type="hidden" name="selectedVariants" value="${selectedVariants}">
                        </c:if>
                        <c:choose>
                            <c:when test="${empty addresses}">
                                <div class="notice">Bạn chưa có địa chỉ. Vui lòng nhập địa chỉ giao hàng, hệ thống sẽ tự lưu sau khi đặt hàng.</div>
                                <input type="hidden" name="shippingName" value="${defaultShippingName}">
                                <input type="hidden" name="shippingPhone" value="${defaultShippingPhone}">
                                <div class="field">
                                    <label>Họ tên</label>
                                    <input type="text" value="${defaultShippingName}" readonly style="background:#f1f5f9;color:#64748b;cursor:not-allowed;">
                                </div>
                                <div class="field">
                                    <label>Số điện thoại</label>
                                    <input type="text" value="${defaultShippingPhone}" readonly style="background:#f1f5f9;color:#64748b;cursor:not-allowed;">
                                </div>
                                <div class="field">
                                    <label>Địa chỉ chi tiết</label>
                                    <input type="text" name="shippingStreet" id="shippingStreet" value="${defaultShippingStreet}" placeholder="Số nhà, tên đường..." required>
                                </div>
                                <div class="field">
                                    <label>Tỉnh/Thành phố</label>
                                    <select name="shippingCity" id="shippingCity" required>
                                        <option value="">-- Chọn tỉnh/thành phố --</option>
                                    </select>
                                </div>
                                <div class="field">
                                    <label>Quận/Huyện</label>
                                    <select name="shippingDistrict" id="shippingDistrict" required disabled>
                                        <option value="">-- Chọn quận/huyện --</option>
                                    </select>
                                </div>
                                <div class="field">
                                    <label>Phường/Xã</label>
                                    <select id="shippingWard" required disabled>
                                        <option value="">-- Chọn phường/xã --</option>
                                    </select>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <input type="hidden" name="shippingName" value="${defaultShippingName}">
                                <input type="hidden" name="shippingPhone" value="${defaultShippingPhone}">
                                <input type="hidden" name="useNewAddress" id="useNewAddress" value="${empty useNewAddress ? '0' : useNewAddress}">
                                <input type="hidden" name="selectedAddressId" id="selectedAddressId" value="">

                                <div class="checkout-address-box">
                                    <div class="checkout-address-title">
                                        <i class="fas fa-location-dot"></i>
                                        <span>Địa Chỉ Nhận Hàng</span>
                                    </div>

                                    <div class="checkout-address-line">
                                        <span class="checkout-address-contact" id="selectedAddressContact">${defaultShippingName} (${defaultShippingPhone})</span>
                                        <span class="checkout-address-text" id="selectedAddressText"></span>
                                        <span class="checkout-address-badge" id="selectedAddressDefault" >Mặc định</span>
                                        <button type="button" class="checkout-address-change" id="toggleAddressPickerBtn">Thay đổi</button>
                                    </div>

                                </div>

                                <div class="checkout-address-modal" id="addressModal">
                                    <div class="checkout-address-modal-content">
                                        <div class="checkout-address-modal-head">
                                            <span>Địa Chỉ Của Tôi</span>
                                            <button type="button" class="checkout-address-modal-close" id="closeAddressModalBtn">×</button>
                                        </div>
                                        <div class="checkout-address-modal-body" id="addressModalList">
                                            <c:forEach items="${addresses}" var="a" varStatus="st">
                                                <div class="checkout-address-item">
                                                    <div class="checkout-address-item-radio">
                                                        <input type="radio"
                                                               name="addressChoice"
                                                               class="address-choice-radio"
                                                               value="${a.id}"
                                                               data-city="${a.city}"
                                                               data-district="${a.district}"
                                                               data-street="${a.street}"
                                                               data-is-default="${a.isDefault ? '1' : '0'}"
                                                               ${a.isDefault || st.index == 0 ? 'checked' : ''}>
                                                    </div>
                                                    <div class="checkout-address-item-info">
                                                        <div class="checkout-address-item-top">
                                                            <span class="checkout-address-item-name">${defaultShippingName}</span>
                                                            <span class="checkout-address-item-phone">${defaultShippingPhone}</span>
                                                            <c:if test="${a.isDefault}">
                                                                <span class="checkout-address-badge">Mặc định</span>
                                                            </c:if>
                                                        </div>
                                                        <div class="checkout-address-item-line">${a.street}</div>
                                                        <div class="checkout-address-item-line">${a.district}, ${a.city}</div>
                                                    </div>
                                                </div>
                                            </c:forEach>

                                            <div class="checkout-new-address-wrap" id="newAddressWrap">
                                                <div class="field">
                                                    <label>Địa chỉ chi tiết</label>
                                                    <input type="text" name="shippingStreet" id="shippingStreet" value="${defaultShippingStreet}" placeholder="Số nhà, tên đường...">
                                                </div>
                                                <div class="field">
                                                    <label>Tỉnh/Thành phố</label>
                                                    <select name="shippingCity" id="shippingCity">
                                                        <option value="">-- Chọn tỉnh/thành phố --</option>
                                                    </select>
                                                </div>
                                                <div class="field">
                                                    <label>Quận/Huyện</label>
                                                    <select name="shippingDistrict" id="shippingDistrict" disabled>
                                                        <option value="">-- Chọn quận/huyện --</option>
                                                    </select>
                                                </div>
                                                <div class="field">
                                                    <label>Phường/Xã</label>
                                                    <select id="shippingWard" disabled>
                                                        <option value="">-- Chọn phường/xã --</option>
                                                    </select>
                                                </div>
                                                <div class="field" >
                                                    <label >
                                                        <input type="checkbox" name="newAddressIsDefault" id="newAddressIsDefault" value="1" ${newAddressIsDefault == '1' ? 'checked' : ''}>
                                                        <span>Đặt địa chỉ mới làm mặc định</span>
                                                    </label>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="checkout-address-modal-actions">
                                            <button type="button" class="checkout-address-add-btn" id="toggleNewAddressBtn">+ Thêm Địa Chỉ Mới</button>
                                            <button type="button" class="checkout-address-confirm-btn" id="applyAddressBtn">Xác nhận</button>
                                        </div>
                                    </div>
                                </div>
                            </c:otherwise>
                        </c:choose>

                        <div class="field">
                            <label>Nguồn xuất hàng</label>
                            <input type="text" id="suggestedBranch" readonly value="Kho trung tâm">
                        </div>
                        <div class="field">
                            <label>Phương thức thanh toán</label>
                            <select name="paymentMethod">
                                <option value="COD">COD</option>
                                <option value="BANK">Chuyển khoản</option>
                            </select>
                        </div>
                        <button class="checkout-btn" type="submit">Đặt hàng</button>
                    </form>


                </div>

                <div class="panel">
                    <h3>Đơn hàng</h3>
                    <c:forEach items="${cartItems}" var="item">
                        <div class="item-row">
                            <span>${item.productName} (${item.size}/${item.color}) x ${item.quantity}</span>
                            <span><fmt:formatNumber value="${item.subtotal}" pattern="#,##0"/>đ</span>
                        </div>
                    </c:forEach>
                    <div class="summary-total">Tổng: <fmt:formatNumber value="${cartTotal}" pattern="#,##0"/>đ</div>
                </div>
            </div>
        </div>

        <jsp:include page="/jsp/footer.jsp" />
        <script src="${pageContext.request.contextPath}/js/checkout.js"></script>
    </body>
</html>
