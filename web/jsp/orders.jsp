<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Lịch sử đơn hàng</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/fashionStyle.css?v=202602307b">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link href="${pageContext.request.contextPath}/css/orders.css?v=1545454b" rel="stylesheet" type="text/css"/>
    <meta name="context-path" content="${pageContext.request.contextPath}">
</head>
<body>
<jsp:include page="header.jsp" />
<div class="list-wrap">
    <div class="orders-beauty-wrap">
    <h2 class="section-title">Lịch sử đơn hàng</h2>

    <!-- Status Filter Navbar -->
    <div class="order-navbar">
        <a href="${pageContext.request.contextPath}/orders" 
           class="order-nav-tab ${empty currentStatus ? 'active' : ''}">
            Tất cả
        </a>
        <a href="${pageContext.request.contextPath}/orders?status=PENDING" 
           class="order-nav-tab ${currentStatus == 'PENDING' ? 'active' : ''}">
            Đang chờ xử lý
        </a>
        <a href="${pageContext.request.contextPath}/orders?status=PROCESSING" 
           class="order-nav-tab ${currentStatus == 'PROCESSING' ? 'active' : ''}">
            Đang xử lý
        </a>
        <a href="${pageContext.request.contextPath}/orders?status=SHIPPED" 
           class="order-nav-tab ${currentStatus == 'SHIPPED' ? 'active' : ''}">
            Đã gửi hàng
        </a>
        <a href="${pageContext.request.contextPath}/orders?status=DELIVERED" 
           class="order-nav-tab ${currentStatus == 'DELIVERED' ? 'active' : ''}">
            Đã nhận hàng
        </a>
        <a href="${pageContext.request.contextPath}/orders?status=CANCELLED" 
           class="order-nav-tab ${currentStatus == 'CANCELLED' ? 'active' : ''}">
            Đã hủy
        </a>
    </div>

    <c:choose>
        <c:when test="${empty orders}">
            <div class="empty">Chưa có đơn hàng nào.</div>
        </c:when>
        <c:otherwise>
            <c:forEach items="${orders}" var="o">
                <div class="order-card">
                    <div class="order-head">
                        <div class="order-meta">
                            <span><strong>Đơn #${o.id}</strong></span>
                            <span><i class="fa-regular fa-clock"></i> <fmt:formatDate value="${o.orderDate}" pattern="yyyy-MM-dd HH:mm"/></span>
                            <span><i class="fa-regular fa-credit-card"></i> ${o.paymentMethod}</span>
                            <span><i class="fa-solid fa-map-marker-alt"></i> ${o.shippingStreet}, ${o.shippingDistrict}, ${o.shippingCity}</span>
                        </div>
                        <div class="order-status">${o.shippingStatus}</div>
                    </div>

                    <c:choose>
                        <c:when test="${empty orderItemsMap[o.id]}">
                            <div class="order-item-row">
                                <div></div>
                                <div class="text-muted">Không tìm thấy chi tiết sản phẩm của đơn này.</div>
                                <div></div>
                            </div>
                        </c:when>
                        <c:otherwise>
                    <c:forEach items="${orderItemsMap[o.id]}" var="item">
                        <c:set var="imgPath" value="${item.imageUrl}" />
                        <c:if test="${empty imgPath}">
                            <c:set var="imgPath" value="img/products/default-product.jpg" />
                        </c:if>
                        <c:if test="${not fn:contains(imgPath, '.')}">
                            <c:set var="imgPath" value="${imgPath}.jpg" />
                        </c:if>
                        <c:choose>
                            <c:when test="${fn:startsWith(imgPath,'http://') or fn:startsWith(imgPath,'https://')}">
                                <c:set var="imgSrc" value="${imgPath}" />
                            </c:when>
                            <c:when test="${fn:contains(imgPath,'/')}">
                                <c:choose>
                                    <c:when test="${fn:startsWith(imgPath,'/')}">
                                        <c:set var="imgSrc" value="${pageContext.request.contextPath}${imgPath}" />
                                    </c:when>
                                    <c:otherwise>
                                        <c:set var="imgSrc" value="${pageContext.request.contextPath}/${imgPath}" />
                                    </c:otherwise>
                                </c:choose>
                            </c:when>
                            <c:otherwise>
                                <c:set var="imgSrc" value="${pageContext.request.contextPath}/img/products/${imgPath}" />
                            </c:otherwise>
                        </c:choose>

                        <div class="order-item-row order-item-clickable" data-product-id="${item.productId}">
                            <img class="order-thumb" src="${imgSrc}" alt="${item.productName}" onerror="this.src='${pageContext.request.contextPath}/img/products/default-product.jpg'">
                            <div>
                                <p class="order-title">${item.productName}</p>
                                <p class="order-variant">Phân loại hàng: ${empty item.color ? 'Mặc định' : item.color}<c:if test="${not empty item.size}">, ${item.size}</c:if></p>
                                <p class="order-qty">x${item.quantity}</p>
                            </div>
                            <div class="order-price-box">
                                <c:if test="${item.basePrice > item.priceAtPurchase}">
                                    <span class="old-price"><fmt:formatNumber value="${item.basePrice}" pattern="#,##0"/>đ</span>
                                </c:if>
                                <span class="new-price"><fmt:formatNumber value="${item.priceAtPurchase}" pattern="#,##0"/>đ</span>
                            </div>
                        </div>
                    </c:forEach>
                        </c:otherwise>
                    </c:choose>

                    <div class="order-foot">
                        <div class="order-actions">
                            <form action="${pageContext.request.contextPath}/order/action" method="post" style="display:inline">
                                <input type="hidden" name="orderId" value="${o.id}" />
                                <c:set var="st" value="${fn:toUpperCase(fn:trim(o.shippingStatus))}" />
                                <c:choose>
                                    <c:when test="${st == 'PENDING'}">
                                        <button name="action" value="cancel" class="btn btn-sm btn-outline-danger">Hủy đơn</button>
                                        <button name="action" value="received" class="btn btn-sm btn-outline-success" disabled>Đã nhận</button>
                                    </c:when>
                                    <c:when test="${st == 'PROCESSING'}">
                                        <button name="action" value="cancel" class="btn btn-sm btn-outline-danger" disabled>Hủy đơn</button>
                                        <button name="action" value="received" class="btn btn-sm btn-outline-success" disabled>Đã nhận</button>
                                    </c:when>
                                    <c:when test="${st == 'SHIPPED'}">
                                        <button name="action" value="received" class="btn btn-sm btn-outline-success">Đã nhận</button>
                                        <button name="action" value="cancel" class="btn btn-sm btn-outline-danger" disabled>Hủy đơn</button>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="text-muted">-</span>
                                    </c:otherwise>
                                </c:choose>
                            </form>
                        </div>
                        <div class="order-total"><strong>Thành tiền:</strong> <fmt:formatNumber value="${o.totalAmount}" pattern="#,##0"/>đ</div>
                    </div>
                </div>
            </c:forEach>
        </c:otherwise>
    </c:choose>

    <c:if test="${totalPages > 1}">
        <nav>
            <ul class="pagination">
                <c:forEach begin="1" end="${totalPages}" var="p">
                    <li class="page-item ${p == page ? 'active' : ''}">
                        <a class="page-link" href="${pageContext.request.contextPath}/orders?page=${p}${not empty currentStatus ? '&status='.concat(currentStatus) : ''}">${p}</a>
                    </li>
                </c:forEach>
            </ul>
        </nav>
    </c:if>
    </div>
</div>
<script src="${pageContext.request.contextPath}/js/orders.js"></script>
</body>
</html>