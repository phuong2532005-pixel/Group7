<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Don hang gui Giam doc</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/shop_order.css?v=20260474307b">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <meta name="context-path" content="${pageContext.request.contextPath}">
</head>
<body>
<jsp:include page="header.jsp" />
<div class="container mt-4 shop-orders-page">
    <h3>Đơn hàng gửi Giám đốc</h3>

    <c:if test="${not empty error}">
        <div class="alert alert-danger">${error}</div>
    </c:if>

    <form class="row g-2 mb-3" method="get" action="${pageContext.request.contextPath}/admin/orders">
        <div class="col-auto">
            <select name="status" class="form-select">
                <option value="" ${empty statusFilter ? 'selected' : ''}>Tất cả trạng thái</option>
                <option value="PENDING" ${statusFilter=='PENDING' ? 'selected' : ''}>Chờ xác nhận</option>
                <option value="PROCESSING" ${statusFilter=='PROCESSING' ? 'selected' : ''}>Đang xử lý</option>
                <option value="SHIPPED" ${statusFilter=='SHIPPED' ? 'selected' : ''}>Đã gửi</option>
                <option value="DELIVERED" ${statusFilter=='DELIVERED' ? 'selected' : ''}>Đã giao</option>
                <option value="CANCELLED" ${statusFilter=='CANCELLED' ? 'selected' : ''}>Đã hủy</option>
            </select>
        </div>
        <div class="col-auto">
            <select name="sort" class="form-select">
                <option value="" ${empty sort ? 'selected' : ''}>Sắp xếp theo</option>
                <option value="asc" ${sort=='asc' ? 'selected' : ''}>Thời gian tăng dần</option>
                <option value="desc" ${sort=='desc' ? 'selected' : ''}>Thời gian giảm dần</option>
            </select>
        </div>
        <div class="col-auto">
            <button class="btn btn-primary">Lọc</button>
        </div>
    </form>

    <table class="table table-bordered table-hover">
        <thead>
            <tr>
                <th>Mã đơn</th>
                <th>Khách hàng</th>
                <th>Số điện thoại</th>
                <th>Danh sách sản phẩm</th>
                <th>Thời gian</th>
                <th>Tổng tiền</th>
                <th>Trạng thái</th>
                <th>Hành động</th>
            </tr>
        </thead>
        <tbody>
            <c:if test="${empty orders}">
                <tr>
                    <td colspan="8" class="text-center text-muted py-4">Không có đơn hàng cần duyệt.</td>
                </tr>
            </c:if>
            <c:forEach items="${orders}" var="o">
                <tr>
                    <td>#${o.id}</td>
                    <td>${o.shippingName}</td>
                    <td>${o.shippingPhone}</td>
                    <td>
                        <c:set var="orderItems" value="${orderItemsMap[o.id]}" />
                        <c:choose>
                            <c:when test="${empty orderItems}">
                                <span class="text-muted">(Không có sản phẩm)</span>
                            </c:when>
                            <c:otherwise>
                                <ul class="mb-0 ps-3">
                                    <c:forEach items="${orderItems}" var="item">
                                        <li>
                                            ${item.productName}
                                            <c:if test="${not empty item.size}"> - Size ${item.size}</c:if>
                                            <c:if test="${not empty item.color}"> - Màu ${item.color}</c:if>
                                            x${item.quantity}
                                        </li>
                                    </c:forEach>
                                </ul>
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td><fmt:formatDate value="${o.orderDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                    <td><fmt:formatNumber value="${o.totalAmount}" pattern="#,##0"/> đ</td>
                    <td>${o.shippingStatus}</td>
                    <td>
                        <form action="${pageContext.request.contextPath}/order/action" method="post">
                            <input type="hidden" name="orderId" value="${o.id}" />
                            <c:set var="st" value="${fn:toUpperCase(o.shippingStatus)}" />
                            <c:choose>
                                <c:when test="${st == 'PENDING'}">
                                    <button name="action" value="accept" class="btn btn-sm btn-primary">Tiếp nhận đơn</button>
                                </c:when>
                                <c:when test="${st == 'PROCESSING'}">
                                    <button name="action" value="ship" class="btn btn-sm btn-warning">Gửi đơn hàng</button>
                                </c:when>
                                <c:otherwise>
                                    <span>-</span>
                                </c:otherwise>
                            </c:choose>
                        </form>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>

    <c:if test="${totalPages > 1}">
        <div class="paging-container">
            <div class="paging-info">
                Trang <strong>${page}</strong>/<strong>${totalPages}</strong>
            </div>

            <div class="paging">
                <c:if test="${page > 1}">
                    <c:url var="prevUrl" value="/admin/orders">
                        <c:param name="page" value="${page - 1}"/>
                        <c:if test="${not empty statusFilter}"><c:param name="status" value="${statusFilter}"/></c:if>
                        <c:if test="${not empty sort}"><c:param name="sort" value="${sort}"/></c:if>
                    </c:url>
                    <a class="page-link" href="${prevUrl}">
                        <i class="fa-solid fa-chevron-left"></i>
                    </a>
                </c:if>

                <c:set var="startPage" value="${page - 2 > 1 ? page - 2 : 1}" />
                <c:set var="endPage" value="${page + 2 < totalPages ? page + 2 : totalPages}" />

                <c:if test="${startPage > 1}">
                    <c:url var="firstPageUrl" value="/admin/orders">
                        <c:param name="page" value="1"/>
                        <c:if test="${not empty statusFilter}"><c:param name="status" value="${statusFilter}"/></c:if>
                        <c:if test="${not empty sort}"><c:param name="sort" value="${sort}"/></c:if>
                    </c:url>
                    <a class="page-link" href="${firstPageUrl}">1</a>
                    <span class="page-link">...</span>
                </c:if>

                <c:forEach begin="${startPage}" end="${endPage}" var="p">
                    <c:url var="pageUrl" value="/admin/orders">
                        <c:param name="page" value="${p}"/>
                        <c:if test="${not empty statusFilter}"><c:param name="status" value="${statusFilter}"/></c:if>
                        <c:if test="${not empty sort}"><c:param name="sort" value="${sort}"/></c:if>
                    </c:url>
                    <a class="page-link ${p == page ? 'active' : ''}" href="${pageUrl}">
                        ${p}
                    </a>
                </c:forEach>

                <c:if test="${endPage < totalPages}">
                    <span class="page-link">...</span>
                    <c:url var="lastPageUrl" value="/admin/orders">
                        <c:param name="page" value="${totalPages}"/>
                        <c:if test="${not empty statusFilter}"><c:param name="status" value="${statusFilter}"/></c:if>
                        <c:if test="${not empty sort}"><c:param name="sort" value="${sort}"/></c:if>
                    </c:url>
                    <a class="page-link" href="${lastPageUrl}">${totalPages}</a>
                </c:if>

                <c:if test="${page < totalPages}">
                    <c:url var="nextUrl" value="/admin/orders">
                        <c:param name="page" value="${page + 1}"/>
                        <c:if test="${not empty statusFilter}"><c:param name="status" value="${statusFilter}"/></c:if>
                        <c:if test="${not empty sort}"><c:param name="sort" value="${sort}"/></c:if>
                    </c:url>
                    <a class="page-link" href="${nextUrl}">
                        <i class="fa-solid fa-chevron-right"></i>
                    </a>
                </c:if>
            </div>
        </div>
    </c:if>
</div>
</body>
</html>
