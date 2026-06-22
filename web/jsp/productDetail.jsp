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
    <title>Chi tiết sản phẩm</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/product_detail.css?v=2">
    <link href="${pageContext.request.contextPath}/css/fashionStyle.css?v=20260307b" rel="stylesheet" type="text/css"/>
    
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    
</head>
<body>
<jsp:include page="/jsp/header.jsp" />

<div class="detail-wrap">
    <c:choose>
        <c:when test="${empty product}">
            <div class="empty">Không tìm thấy sản phẩm.</div>
        </c:when>
        <c:otherwise>
            <div class="detail-grid">
                <!-- Images Section -->
                <div class="product-images-section">
                    <div class="main-image">
                        <c:set var="firstImage" value="" />
                        <c:if test="${not empty allImages and fn:length(allImages) > 0}">
                            <c:set var="firstImage" value="${allImages[0]}" />
                        </c:if>
                        <c:if test="${empty firstImage and not empty product.thumbnail}">
                            <c:set var="firstImage" value="${product.thumbnail}" />
                        </c:if>
                        
                        <c:if test="${not empty firstImage}">
                            <c:set var="mainImg" value="${firstImage}" />
                            <c:if test="${not fn:contains(mainImg, '.')}">
                                <c:set var="mainImg" value="${mainImg}.jpg" />
                            </c:if>
                            <c:choose>
                                <c:when test="${fn:startsWith(mainImg,'http://') or fn:startsWith(mainImg,'https://')}">
                                    <c:set var="mainImageSrc" value="${mainImg}" />
                                </c:when>
                                <c:when test="${fn:contains(mainImg,'/')}">
                                    <c:choose>
                                        <c:when test="${fn:startsWith(mainImg,'/')}">
                                            <c:set var="mainImageSrc" value="${pageContext.request.contextPath}${mainImg}" />
                                        </c:when>
                                        <c:otherwise>
                                            <c:set var="mainImageSrc" value="${pageContext.request.contextPath}/${mainImg}" />
                                        </c:otherwise>
                                    </c:choose>
                                </c:when>
                                <c:otherwise>
                                    <c:set var="mainImageSrc" value="${pageContext.request.contextPath}/img/products/${mainImg}" />
                                </c:otherwise>
                            </c:choose>
                        </c:if>
                        <img id="main-image" src="${mainImageSrc}" alt="${product.name}">
                    </div>
                    
                </div>

                <!-- Product Info Section -->
                <div class="detail-info">
                    <h1>${product.name}</h1>
                    <div>
                        Phân loại: <strong>${categoryPath}</strong>
                    </div>
                    <div class="detail-price" id="detail-price">
                        <fmt:formatNumber value="${product.basePrice}" pattern="#,##0"/> đ
                    </div>
                    <div>
                        Đã bán: <strong><fmt:formatNumber value="${totalSold}" pattern="#,##0"/></strong>
                    </div>

                    <div class="size-color-selectors">
                        <!-- SIZE selector -->
                        <c:if test="${not empty sizes}">
                            <div class="selector-group">
                                <span class="selector-label">SIZE</span>
                                <div class="option-buttons" id="size-options">
                                    <c:forEach items="${sizes}" var="size">
                                        <button type="button" 
                                                class="option-btn size-btn" 
                                                data-size-id="${size.id}"
                                                data-size-name="${size.sizeName}">
                                            ${size.sizeName}
                                        </button>
                                    </c:forEach>
                                </div>
                            </div>
                        </c:if>

                        <!-- COLOR selector -->
                        <c:if test="${not empty colors}">
                            <div class="selector-group">
                                <span class="selector-label">MÀU</span>
                                <div class="option-buttons" id="color-options">
                                    <c:forEach items="${colors}" var="color">
                                        <button type="button" 
                                                class="option-btn color-btn" 
                                                data-color-id="${color.id}"
                                                data-color-name="${color.colorName}">
                                            ${color.colorName}
                                        </button>
                                    </c:forEach>
                                </div>
                            </div>
                        </c:if>
                    </div>

                  

                    <div class="detail-actions">
                        <c:if test="${not empty param.cartError}">
                            <div >
                                <c:choose>
                                    <c:when test="${param.cartError == 'variant'}">Vui lòng chọn size và màu trước khi thêm vào giỏ.</c:when>
                                    <c:when test="${param.cartError == 'add_failed'}">Không thể thêm vào giỏ hàng. Vui lòng thử lại.</c:when>
                                    <c:otherwise>Có lỗi khi thêm vào giỏ hàng. Vui lòng thử lại.</c:otherwise>
                                </c:choose>
                            </div>
                        </c:if>
                        <div id="toast-container" >
                            <div id="toast-message" >
                                <i class="fa-solid fa-check-circle"></i>
                                <span id="toast-text">Thêm vào giỏ hàng thành công!</span>
                            </div>
                        </div>
                        <% if (acc == null && request.getAttribute("variants") != null && !((java.util.List) request.getAttribute("variants")).isEmpty()) { %>
                        <form id="guest-login-form" action="${pageContext.request.contextPath}/dispatchcontroller" method="get" >
                            <input type="hidden" name="button" value="LoginPage">
                            <input type="hidden" name="returnUrl" id="returnUrl" value="">
                            <input type="number" id="qty-input" min="1" value="1" >
                            <button class="btn-solid" type="submit">Thêm vào giỏ</button>
                        </form>
                        <% } else if (acc == null) { %>
                        <div>
                            Sản phẩm hiện đã hết hàng.
                        </div>
                        <% } else if (canPurchase && request.getAttribute("variants") != null && !((java.util.List) request.getAttribute("variants")).isEmpty()) { %>
                        <form id="add-to-cart-form" >
                            <input type="hidden" name="action" value="add">
                            <input type="hidden" name="productId" value="${product.id}">
                            <input type="hidden" name="variantId" id="selected-variant" value="">
                            <input type="number" name="quantity" id="qty-input" min="1" value="1" >
                            <button class="btn-solid" type="button" id="add-to-cart-btn">Thêm vào giỏ</button>
                        </form>
                        <% } else if (canPurchase) { %>
                        <div>
                            Sản phẩm hiện đã hết hàng.
                        </div>
                        <% } else { %>
                        <div >
                            Tài khoản quản trị không thể thêm.
                        </div>
                        <% } %>
                    </div>
                </div>

                <!-- Product Details Section -->
<div class="container mt-5 mb-5">
    <div class="product-details-card">
        <div class="product-details-header">
            CHI TIẾT SẢN PHẨM
        </div>
        
        <div class="product-details-content">
            <div id="raw-description" style="display: none;">
                ${not empty product.description ? product.description : 'Không có thông tin chi tiết'}
            </div>
            
            <div id="formatted-description"></div>
        </div>
    </div>
</div>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<jsp:include page="/jsp/footer.jsp" />

<script>

var contextPath = "${pageContext.request.contextPath}";
var productId = "${product.id}";

var variantsData = [

<c:forEach items="${variants}" var="v" varStatus="status">
{
    id: ${v.id},
    sizeId: ${v.sizeId},
    colorId: ${v.colorId},
    price: ${v.price},
    imageUrl: "${v.imageUrl}"
}
<c:if test="${!status.last}">,</c:if>
</c:forEach>

];

</script>

<script src="${pageContext.request.contextPath}/js/productDetail.js"></script>

</body>
</html>
