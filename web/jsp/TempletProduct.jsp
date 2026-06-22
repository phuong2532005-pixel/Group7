<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<div class="product-item">
    
    <div class="p-img-box">
           <span class="badge badge-new">NEW</span>
        
        <a href="${pageContext.request.contextPath}/product?id=${p.id}">
            <c:choose>
                <c:when test="${not empty p.thumbnail}">
                    <c:set var="imgPath" value="${p.thumbnail}" />
                    <c:if test="${not fn:contains(imgPath, '.')}">
                        <c:set var="imgPath" value="${imgPath}.jpg" />
                    </c:if>
                    <c:choose>
                        <c:when test="${fn:startsWith(imgPath,'http://') or fn:startsWith(imgPath,'https://')}">
                            <img src="${imgPath}" alt="${p.name}" class="p-img" onerror="this.src='${pageContext.request.contextPath}/img/products/default.jpg'">
                        </c:when>
                        <c:when test="${fn:contains(imgPath,'/')}">
                            <c:choose>
                                <c:when test="${fn:startsWith(imgPath,'/')}">
                                    <img src="${pageContext.request.contextPath}${imgPath}" alt="${p.name}" class="p-img" onerror="this.src='${pageContext.request.contextPath}/img/products/default.jpg'">
                                </c:when>
                                <c:otherwise>
                                    <img src="${pageContext.request.contextPath}/${imgPath}" alt="${p.name}" class="p-img" onerror="this.src='${pageContext.request.contextPath}/img/products/default.jpg'">
                                </c:otherwise>
                            </c:choose>
                        </c:when>
                        <c:otherwise>
                            <img src="${pageContext.request.contextPath}/img/products/${imgPath}" alt="${p.name}" class="p-img" onerror="this.src='${pageContext.request.contextPath}/img/products/default.jpg'">
                        </c:otherwise>
                    </c:choose>
                </c:when>
                <c:otherwise>
                    <img src="${pageContext.request.contextPath}/img/products/default.jpg" alt="${p.name}" class="p-img">
                </c:otherwise>
            </c:choose>
        </a>
    </div>

    <div class="p-info">
        <div class="p-name">
            <a href="${pageContext.request.contextPath}/product?id=${p.id}">${p.name}</a>
        </div>
        
        <div class="price-box">
            <div class="p-price">
                <fmt:formatNumber value="${p.basePrice}" pattern="#,##0"/> đ
            </div>
        </div>
    </div>
</div>