<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@page import="model.UsersDTO"%>
<%
        UsersDTO acc = (UsersDTO) session.getAttribute("acc");
        boolean canPurchase = acc != null && acc.getRole_id() == 3;
%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Gio hang</title>
            <link rel="stylesheet" href="${pageContext.request.contextPath}/css/fashionStyle.css?v=20260c307b">

        <link href="${pageContext.request.contextPath}/css/cart.css?v=202603007b" rel="stylesheet" type="text/css"/>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
        
        <meta name="context-path" content="${pageContext.request.contextPath}">
</head>
    <body>
        <jsp:include page="/jsp/header.jsp" />

        <div class="cart-wrap">
            <h2 class="section-title">Giỏ hàng</h2>

            <c:if test="${not empty param.error}">
                <div class="cart-wrap1" >
                    <c:choose>
                        <c:when test="${param.error == 'invalid_variant'}">Vui lòng chọn biến thể (màu/size) trước khi thêm vào giỏ.</c:when>
                        <c:when test="${param.error == 'add_failed'}">Không thể thêm vào giỏ hàng. Vui lòng thử lại.</c:when>
                        <c:otherwise>Có lỗi xảy ra. Vui lòng thử lại.</c:otherwise>
                    </c:choose>
                </div>
            </c:if>
            <c:if test="${param.added == '1'}">
                <div class="cart-wrap2" >
                    Đã thêm sản phẩm vào trong giỏ hàng.
                </div>
            </c:if>

            <c:choose>
                <c:when test="${empty cartItems}">
                    <div class="empty">Giỏ hàng trống.</div>
                </c:when>
                <c:otherwise>
                    <div class="empty" id="cart-empty-state" >Giỏ hàng trống.</div>
                    <table class="cart-table" id="cart-table">
                        <thead>
                            <tr>
                                <th class="cart-table1" ><input type="checkbox" id="select-all"></th>
                                <th>Sản phẩm</th>
                                <th>Size/Màu</th>
                                <th>Giá</th>
                                <th>Số lượng</th>
                                <th>Thành tiền</th>
                                <th></th>
                            </tr>
                        </thead>
                        <tbody>
                            <!-- group items by product name -->
                            <c:set var="prevProduct" value="" />
                            <c:forEach items="${cartItems}" var="item">
                                <c:if test="${prevProduct != item.productName}">
                                    <tr class="product-group-row">
                                        <td colspan="7" >
                                            <strong>${item.productName}</strong>
                                        </td>
                                    </tr>
                                    <c:set var="prevProduct" value="${item.productName}" />
                                </c:if>
                                <tr data-variant="${item.variantId}" data-product-id="${item.productId}" data-price="${item.price}">
                                    <td >
                                        <input type="checkbox" class="item-checkbox" data-variant="${item.variantId}">
                                    </td>
                                    <td>
                                        <div class="cart-item product-clickable" data-product-id="${item.productId}" style="cursor:pointer;">
                                            <div class="cart-thumb">
                                                <c:if test="${not empty item.imageUrl}">
                                                    <c:choose>
                                                        <c:when test="${fn:startsWith(item.imageUrl,'http://') or fn:startsWith(item.imageUrl,'https://')}">
                                                            <img src="${item.imageUrl}" alt="${item.productName}">
                                                        </c:when>
                                                        <c:when test="${fn:startsWith(item.imageUrl,'/')}">
                                                            <c:set var="imgPath" value="${item.imageUrl}" />
                                                            <c:if test="${not fn:contains(imgPath, '.')}">
                                                                <c:set var="imgPath" value="${imgPath}.jpg" />
                                                            </c:if>
                                                            <img src="${pageContext.request.contextPath}${imgPath}" alt="${item.productName}">
                                                        </c:when>
                                                        <c:otherwise>
                                                            <c:set var="imgPath" value="${item.imageUrl}" />
                                                            <c:if test="${not fn:contains(imgPath, '/')}">
                                                                <c:set var="imgPath" value="img/products/${imgPath}" />
                                                            </c:if>
                                                            <c:if test="${not fn:contains(imgPath, '.')}">
                                                                <c:set var="imgPath" value="${imgPath}.jpg" />
                                                            </c:if>
                                                            <img src="${pageContext.request.contextPath}/${imgPath}" alt="${item.productName}">
                                                        </c:otherwise>
                                                    </c:choose>
                                                </c:if>
                                            </div>
                                            <div>
                                                <div>${item.productName}</div>
                                            </div>
                                        </div>
                                    </td>
                                    <td>
                                        <div class="cart-variant-selectors">
                                            <select class="size-select"></select>
                                            <select class="color-select"></select>
                                            <select class="variant-select" >
                                                <c:forEach items="${variantOptionsByProduct[item.productId]}" var="variant">
                                                    <option value="${variant.id}"
                                                            data-price="${variant.price}"
                                                            data-size-id="${variant.sizeId}"
                                                            data-size-name="${variant.sizeName}"
                                                            data-color-id="${variant.colorId}"
                                                            data-color-name="${variant.colorName}"
                                                            data-image-url="${variant.imageUrl}"
                                                            <c:if test="${variant.id == item.variantId}">selected</c:if>>${variant.sizeName} / ${variant.colorName}</option>
                                                </c:forEach>
                                            </select>
                                        </div>
                                    </td>
                                    <td><fmt:formatNumber value="${item.price}" pattern="#,##0"/> đ</td>
                                    <td>
                                        <input class="qty-input" type="number" min="1" value="${item.quantity}">
                                    </td>
                                    <td class="line-total"><fmt:formatNumber value="${item.subtotal}" pattern="#,##0"/> đ</td>
                                    <td>
                                        <button type="button" class="remove-btn">Xóa</button>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>

                    <div class="cart-summary">
                        <div class="total">
                            Tổng: <strong class="total-number" id="cart-total">0 đ</strong>
                        </div>
                        <button type="button" class="remove-btn" id="remove-selected-btn">Xóa đã chọn</button>
                        <% if (canPurchase) { %>
                        <button type="button" class="cart-checkout-btn" id="checkout-btn">Mua hàng</button>
                        <% } else { %>
                        <span >Tài khoản quản trị không thể mua hàng.</span>
                        <% } %>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>

        <jsp:include page="/jsp/footer.jsp" />

        
        <script src="${pageContext.request.contextPath}/js/cart.js"></script>
       
</body>
</html>
