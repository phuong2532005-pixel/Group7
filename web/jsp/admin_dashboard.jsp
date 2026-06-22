<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Dashboard Quản Trị</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin_dashboard.css?v=2026550307b">
        <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>

    </head>
    <body class="admin-dashboard-page">
        <jsp:include page="header.jsp" />
        <div class="container mt-4">
            <div class="d-flex align-items-center justify-content-between mb-3">
                <h3>Dashboard Quản trị</h3>
                <c:if test="${roleId == 4}">
                    <div class="d-flex gap-2">
                        <a href="${pageContext.request.contextPath}/admin/orders" class="btn btn-primary">
                            <i class="fa-solid fa-box-open me-2"></i>Duyệt đơn hàng
                        </a>
                        <a href="${pageContext.request.contextPath}/dispatchcontroller?button=AdminCategories" class="btn btn-outline-primary">
                            <i class="fa-solid fa-tags me-2"></i>Quản lý danh mục
                        </a>
                    </div>
                </c:if>
            </div>
            <div class="row mb-4">
                <div class="col-md-4">
                    <div class="card metric-card text-bg-primary mb-3">
                        <div class="card-body">
                            <h5 class="card-title">Tổng người</h5>
                            <p class="card-text fs-3">${stats.totalUsers}</p>
                        </div>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="card metric-card text-bg-success mb-3">
                        <div class="card-body">
                            <h5 class="card-title">Tổng đơn </h5>
                            <p class="card-text fs-3">${stats.totalOrders}</p>
                        </div>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="card metric-card text-bg-info mb-3">
                        <div class="card-body">
                            <h5 class="card-title">Tổng doanh thu</h5>
                            <p class="card-text fs-3">${stats.totalRevenue}</p>
                        </div>
                    </div>
                </div>
            </div>
            <div class="card">
                <div class="card-body">
                    <h5 class="card-title">Doanh thu theo tháng</h5>
                    <div class="mb-3 row">
                        <label class="col-sm-1 col-form-label">Nam</label>
                        <div class="col-sm-2">
                            <input id="yearInput" type="number" class="form-control" value="<%= java.time.Year.now().getValue() %>" />
                        </div>
                        <div class="col-sm-3">
                            <button id="btnShowShopReport" class="btn btn-primary">Hiển thị báo cáo</button>
                        </div>
                    </div>
                    <canvas id="revenueChart" height="80"></canvas>
                </div>
            </div>

            <div class="card mt-3">
                <div class="card-body">
                    <h5 class="card-title">Số đơn theo tháng</h5>
                    <canvas id="ordersChart" height="80"></canvas>
                </div>
            </div>

            <div class="card mt-3">
                <div class="card-body">
                    <h5 class="card-title">Tổng hợp đơn hàng</h5>
                    <table class="table table-striped">
                        <thead>
                            <tr>
                                <th>Hệ thống</th>
                                <th class="text-end">Tổng số đơn</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${orderCountsByShop}" var="r">
                                <tr>
                                    <td>${r.branchName}</td>
                                    <td class="text-end">${r.totalOrders}</td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
                        <jsp:include page="footer.jsp" />
 <script>
            window.contextPath = '<%= request.getContextPath() %>'; 
        </script>
        <script src="${pageContext.request.contextPath}/js/admin_dashboard.js"></script>
    </body>
</html>