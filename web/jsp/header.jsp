<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@page import="model.UsersDTO"%>
<%
    UsersDTO acc = (UsersDTO) session.getAttribute("acc");
    Integer roleId = acc != null ? acc.getRole_id() : null;
    String displayName = acc != null && acc.getFull_name() != null ? acc.getFull_name() : null;
    Integer cartCount = (Integer) request.getAttribute("cartCount");
    if (cartCount == null) {
        cartCount = 0;
    }
%>
<fmt:setLocale value="vi_VN" scope="session"/>
<header class="header">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/fashionStyle.css?v=202060307b">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css?v=202603070b">

    <div class="header-left">
        <a href="${pageContext.request.contextPath}/" class="logo">FASHION</a>
        <ul class="nav-menu">
            <% if (roleId != null && roleId == 3) { %>
            <li class="nav-item">
                <a href="${pageContext.request.contextPath}/orders" class="nav-link">Đơn Hàng</a>
            </li>
            <% } %>
            <% if (roleId != null && roleId == 4) { %>
            <li class="nav-item">
                <a href="${pageContext.request.contextPath}/dispatchcontroller?button=AdminDashboard" class="nav-link">Báo cáo doanh thu</a>
            </li>
            <li class="nav-item">
                <a href="${pageContext.request.contextPath}/dispatchcontroller?button=AdminProducts" class="nav-link">Quản lý sản phẩm</a>
            </li>
            <li class="nav-item">
                <a href="${pageContext.request.contextPath}/admin/orders" class="nav-link">Duyệt đơn hàng</a>
            </li>
            <% } %>
            <% if (roleId != null && roleId == 1) { %>
            <li class="nav-item">
                <a href="${pageContext.request.contextPath}/dispatchcontroller?button=AdminUsers" class="nav-link">Quản lý user</a>
            </li>
            <li class="nav-item">
                <a href="${pageContext.request.contextPath}/admin/systemlogs" class="nav-link">Lịch sử hệ thống</a>
            </li>
            <% } %>
            <li class="nav-item">
                <a class="nav-link">
                    Danh mục <i class="fa-solid fa-chevron-down" ></i>
                </a>
                <ul class="sub-menu">
                    <c:choose>
                        <c:when test="${not empty categories}">
                            <c:forEach items="${categories}" var="parent">
                                <c:if test="${empty parent.parentId}">
                                    <c:url var="parentUrl" value="/dispatchcontroller">
                                        <c:param name="button" value="ProductList"/>
                                        <c:param name="catName" value="${parent.name}"/>
                                    </c:url>
                                    <li class="has-child">
                                        <a href="${parentUrl}">
                                            ${parent.name}
                                            <i class="fa-solid fa-chevron-right arrow-right"></i>
                                        </a>
                                        <ul class="sub-menu-level2">
                                            <c:forEach items="${categories}" var="child">
                                                <c:if test="${child.parentId == parent.id}">
                                                    <c:url var="childUrl" value="/dispatchcontroller">
                                                        <c:param name="button" value="ProductList"/>
                                                        <c:param name="catName" value="${child.name}"/>
                                                    </c:url>
                                                    <li><a href="${childUrl}">${child.name}</a></li>
                                                </c:if>
                                            </c:forEach>
                                        </ul>
                                    </li>
                                </c:if>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <li><a href="${pageContext.request.contextPath}/dispatchcontroller?button=ProductList">Xem sản phẩm</a></li>
                        </c:otherwise>
                    </c:choose>
                </ul>
            </li>
        </ul>
    </div>

    <div class="header-center">
        <form action="${pageContext.request.contextPath}/dispatchcontroller" class="search-box-large">
            <input type="text" name="q" placeholder="Tìm theo tên sản phẩm, category (vd: quần nam)...">
            <button class="search-btn-large" type="submit" name="button" value="ProductList">
                <i class="fa-solid fa-magnifying-glass"></i>
            </button>
        </form>
    </div>

    <div class="header-right">
        <% if (acc != null) { %>
        <span class="header-user">Xin chào, <%= displayName != null ? displayName : acc.getUsername() %></span>
        <% if (roleId != null && (roleId == 1 || roleId == 4)) { %>
        <a href="${pageContext.request.contextPath}/jsp/chatbot.jsp" class="icon-btn" title="AI Trợ lý" style="color: #667eea; font-weight: bold;">
            <i class="fas fa-robot"></i>
        </a>
        <% } %>
        <a href="${pageContext.request.contextPath}/dispatchcontroller?button=Profile" class="icon-btn" title="Tai khoan">
            <i class="fa-regular fa-user"></i>
        </a>
        <a href="${pageContext.request.contextPath}/dispatchcontroller?button=Logout" class="icon-btn" title="Dang xuat">
            <i class="fa-solid fa-right-from-bracket"></i>
        </a>
        <% } else { %>
        <a href="${pageContext.request.contextPath}/dispatchcontroller?button=LoginPage" class="icon-btn" title="Dang nhap">
            <i class="fa-regular fa-user"></i>
        </a>
        <a href="${pageContext.request.contextPath}/dispatchcontroller?button=RegisterEmail" class="nav-pill">Đăng ký</a>
        <% } %>
        <% if (roleId == null || roleId == 3) { %>
        <a href="${pageContext.request.contextPath}/cart" class="icon-btn" title="Gio hang">
            <i class="fa-solid fa-bag-shopping"></i>
            <span class="cart-badge"><%= cartCount %></span>
        </a>
        <% } %>
    </div>
</header>