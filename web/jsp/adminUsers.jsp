<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c"
           uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Quản lý User</title>

        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/adminUsers.css?v=2002060307b">
            <link rel="stylesheet" href="${pageContext.request.contextPath}/css/fashionStyle.css?v=20260c307b">

        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
        <script defer src="${pageContext.request.contextPath}/js/adminUsers.js"></script>
        <meta name="context-path" content="${pageContext.request.contextPath}">
        <meta name="viewport" content="width=device-width, initial-scale=1">

    </head>

    <body class="admin-users-page">

        <jsp:include page="/jsp/header.jsp"/>

        <div class="container my-5 admin-users-shell">

                    <!-- MESSAGE -->
            <c:if test="${not empty adminMessage}">
                <div class="success-message">
                    ${adminMessage}
                </div>
                <c:remove var="adminMessage" scope="session"/>
            </c:if>

            <!-- FILTER + USER TABLE (COMBINED) -->
            <div class="card table-card combined-users-card">
                <div class="section-heading compact-heading">
                    <div>
                        <span class="section-kicker">Filter</span>
                        <h2>Lọc danh sách</h2>
                    </div>
                </div>
                <form method="get" class="filter-inline-form"
                      action="${pageContext.request.contextPath}/dispatchcontroller">

                    <input type="hidden" name="button" value="AdminUsers">

                    <label>Role:</label>

                    <select name="role">
                        <option value="">Tất cả</option>
                        <option value="1"
                                <c:if test="${roleFilter == 1}">selected</c:if>>
                                    Admin
                                </option>

                                <option value="3"
                                <c:if test="${roleFilter == 3}">selected</c:if>>
                                    Customer
                                </option>

                                <option value="4"
                                <c:if test="${roleFilter == 4}">selected</c:if>>
                                    Director
                                </option>
                        </select>

                        <select name="status">
                            <option value="">Tất cả trạng thái</option>
                            <option value="1" <c:if test="${statusFilter == 1}">selected</c:if>>Đang hoạt động</option>
                        <option value="0" <c:if test="${statusFilter == 0}">selected</c:if>>Đã khóa</option>
                        </select>

                        <button class="btn-filter">Lọc</button>
                </form>

                <hr class="combined-separator">

                <!-- USER TABLE -->
                    <div class="section-heading compact-heading">
                        <div>
                            <span class="section-kicker">Listing</span>
                            <h2>Danh sách User</h2>
                        </div>
                    </div>
                    <div class="table-responsive">

                        <table class="table table-hover user-table">

                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Username</th>
                                    <th>Role</th>
                                    <th>Trạng thái</th>
                                </tr>
                            </thead>

                            <tbody>

                            <c:choose>

                                <c:when test="${empty users}">
                                    <tr>
                                        <td colspan="4" class="empty">
                                            Không có dữ liệu
                                        </td>
                                    </tr>
                                </c:when>

                                <c:otherwise>
                                    <c:forEach var="u" items="${users}">
                                        <tr>
                                            <td>${u.id}</td>
                                            <td>${u.username}</td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${u.role_id == 1}">Admin</c:when>
                                                    <c:when test="${u.role_id == 3}">Customer</c:when>
                                                    <c:when test="${u.role_id == 4}">Director</c:when>
                                                    <c:otherwise>${u.role_id}</c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <div class="status-cell">
                                                    <span class="status-chip ${u.status == 1 ? 'status-active' : 'status-inactive'}">
                                                        ${u.status == 1 ? 'Hoạt động' : 'Đã khóa'}
                                                    </span>
                                                    <c:choose>
                                                        <c:when test="${sessionScope.acc != null and sessionScope.acc.id == u.id}">
                                                            <span class="status-self-note">Tài khoản hiện tại</span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <form method="post" action="${pageContext.request.contextPath}/dispatchcontroller" class="status-action-form">
                                                                <input type="hidden" name="button" value="AdminUsers">
                                                                <input type="hidden" name="action" value="toggleStatus">
                                                                <input type="hidden" name="userId" value="${u.id}">
                                                                <button type="submit" class="status-action-btn ${u.status == 1 ? 'btn-ban' : 'btn-unban'}">
                                                                    ${u.status == 1 ? 'Khóa tài khoản' : 'Mở khóa'}
                                                                </button>
                                                            </form>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:otherwise>

                            </c:choose>

                        </tbody>
                    </table>
                </div>
            </div>


            <!-- PAGINATION -->
            <div class="pagination">

                <c:forEach begin="1" end="${totalPages}" var="i">

                    <a class="${i == page ? 'active' : ''}"
                       href="${pageContext.request.contextPath}/dispatchcontroller?button=AdminUsers&page=${i}${roleFilter != null ? '&role=' : ''}${roleFilter != null ? roleFilter : ''}${statusFilter != null ? '&status=' : ''}${statusFilter != null ? statusFilter : ''}">
                        ${i}
                    </a>

                </c:forEach>

            </div>

        </div>



        <jsp:include page="/jsp/footer.jsp"/>

    </body>
</html>
