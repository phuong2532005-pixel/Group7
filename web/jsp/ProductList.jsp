<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Shop Thời Trang</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/fashionStyle.css?v=202060307b">
        <link href="${pageContext.request.contextPath}/css/filter.css?v=202600307b" rel="stylesheet" type="text/css"/>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
        <meta name="context-path" content="${pageContext.request.contextPath}">
    </head>
    <body>

        <jsp:include page="/jsp/header.jsp" />

        <div class="list-wrap">
            <div class="list-layout">
                <div class="check-section">
                    <h2 class="section-title">Sản Phẩm<br> Thời Trang</h2>
                    

                    <aside class="filter-panel">

                        <span class="filter-title"> Bộ lọc tìm kiếm <span class="fa-solid fa-filter"></span></span>

                        <form action="${pageContext.request.contextPath}/dispatchcontroller" method="get">
                            <input type="hidden" name="button" value="ProductList">

                            <div class="filter-group">
                                <label>Từ khóa</label>
                                <input type="text" name="q" value="${q}" placeholder="Ví dụ: quần nam, áo nữ, sneaker unisex...">
                            </div>

                            <div class="filter-group">
                                <label>Danh mục</label>
                                <select name="cate">
                                    <option value="">Tat ca</option>
                                    <c:forEach items="${categories}" var="cat">
                                        <option value="${cat.id}" ${cate == cat.id ? 'selected' : ''}>
                                            <c:forEach begin="1" end="${cat.level}">--</c:forEach>${cat.name}
                                            </option>
                                    </c:forEach>
                                </select>
                            </div>

                            <div class="filter-group">
                                <label>Khoảng giá</label>
                                <div class="range-row">
                                    <input type="number" name="min" value="${min}" placeholder="Min">
                                    <input type="number" name="max" value="${max}" placeholder="Max">
                                </div>
                            </div>

                            <button type="submit" class="filter-btn">Áp dụng</button>
                        </form>
                    </aside>
                </div>

                <section>
                    <c:choose>
                        <c:when test="${empty products}">
                            <div class="empty">Không có sản phẩm phù hợp.</div>
                        </c:when>
                        <c:otherwise>
                            <div class="product-grid">
                                <c:forEach items="${products}" var="p">
                                    <div class="product-card">
                                        <a href="${pageContext.request.contextPath}/product?id=${p.id}">
                                            <c:choose>
                                                <c:when test="${not empty p.thumbnail}">
                                                    <c:set var="imgPath" value="${p.thumbnail}" />
                                                    <c:if test="${not fn:contains(imgPath, '.')}">
                                                        <c:set var="imgPath" value="${imgPath}.jpg" />
                                                    </c:if>
                                                    <c:choose>
                                                        <c:when test="${fn:startsWith(imgPath,'http://') or fn:startsWith(imgPath,'https://')}">
                                                            <img src="${imgPath}" alt="${p.name}">
                                                        </c:when>
                                                        <c:when test="${fn:contains(imgPath,'/')}">
                                                            <c:choose>
                                                                <c:when test="${fn:startsWith(imgPath,'/')}">
                                                                    <img src="${pageContext.request.contextPath}${imgPath}" alt="${p.name}">
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <img src="${pageContext.request.contextPath}/${imgPath}" alt="${p.name}">
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <img src="${pageContext.request.contextPath}/img/products/${imgPath}" alt="${p.name}">
                                                        </c:otherwise>
                                                    </c:choose>
                                                </c:when>
                                                <c:otherwise>
                                                    <div class="noname""></div>
                                                </c:otherwise>
                                            </c:choose>
                                        </a>
                                        <div class="info">
                                            <h4><a href="${pageContext.request.contextPath}/product?id=${p.id}">${p.name}</a></h4>
                                            <div class="price">
                                                <fmt:formatNumber value="${p.basePrice}" pattern="#,##0"/> đ
                                            </div>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                        </c:otherwise>
                    </c:choose>

                    <c:if test="${totalPages > 1}">
                        <div class="paging-container" style="text-align: center; margin-top: 30px;">
                            <div class="paging-info" style="margin-bottom: 10px; color: #666;">
                                Trang <strong>${page}</strong>/<strong>${totalPages}</strong>
                            </div>

                            <div class="paging" style="display: flex; justify-content: center; gap: 8px;">
                                <%-- Nút Previous --%>
                                <c:if test="${page > 1}">
                                    <a class="page-link" href="${pageContext.request.contextPath}/dispatchcontroller?button=ProductList&page=${page - 1}&q=${q}&cate=${cate}&min=${min}&max=${max}">
                                        <i class="fa-solid fa-chevron-left"></i>
                                    </a>
                                </c:if>

                                <c:forEach begin="1" end="${totalPages}" var="i">
                                    <c:choose>
                                        <c:when test="${i == 1 || i == totalPages || (i >= page - 2 && i <= page + 2)}">
                                            <a class="page-link ${i == page ? 'active' : ''}" 
                                               href="${pageContext.request.contextPath}/dispatchcontroller?button=ProductList&page=${i}&q=${q}&cate=${cate}&min=${min}&max=${max}">${i}</a>
                                        </c:when>
                                        <c:when test="${i == page - 3 || i == page + 3}">
                                            <span class="page-dots" >...</span>
                                        </c:when>
                                    </c:choose>
                                </c:forEach>

                                <%-- Nút Next --%>
                                <c:if test="${page < totalPages}">
                                    <a class="page-link" href="${pageContext.request.contextPath}/dispatchcontroller?button=ProductList&page=${page + 1}&q=${q}&cate=${cate}&min=${min}&max=${max}">
                                        <i class="fa-solid fa-chevron-right"></i>
                                    </a>
                                </c:if>
                            </div>
                        </div>
                    </c:if>
                </section>
            </div>
        </div>

        <jsp:include page="/jsp/footer.jsp" />

    </body>
</html>