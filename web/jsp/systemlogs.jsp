<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Lịch sử hệ thống</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/fashionStyle.css?v=202603507b">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/system_logs.css?v=2026035707b">

    </head>
    <body>
        <jsp:include page="header.jsp" />
        <div class="container my-5">
            <h2 class="mb-4"><i class="fas fa-history"></i> Lịch sử hệ thống</h2>
            <c:if test="${not empty logError}">
                <div class="alert alert-danger">
                    <i class="fas fa-exclamation-triangle"></i>
                    Lỗi khi tải log: ${logError}
                    <br>
                    <small>Hãy kiểm tra console hoặc xem file sao lưu để biết nguyên nhân.</small>
                </div>
            </c:if>



            <div class="card">
                <div class="card-body">
                    <form method="get" action="${pageContext.request.contextPath}/admin/systemlogs" class="row g-3">
                        <div class="col-md-4">
                            <label class="form-label">Bộ lọc:</label>
                            <select name="filterType" class="form-select">
                                <option value="all" ${filterType == 'all' ? 'selected' : ''}>Tất cả hoạt động</option>
                                <option value="user" ${filterType == 'user' ? 'selected' : ''}>Hoạt động của tôi</option>
                            </select>
                        </div>
                        <div class="col-md-4 d-flex align-items-end">
                            <button type="submit" class="btn btn-primary">Áp dụng bộ lọc</button>
                        </div>
                    </form>

                    <div class="table-responsive mt-4">
                        <table class="table table-hover">
                            <thead class="table-light">
                                <tr>
                                    <th>#</th>
                                    <th>Người dùng</th>
                                    <th>Hành động</th>
                                    <th>Thời gian</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${logs}" var="log" varStatus="status">
                                    <tr>
                                        <td><strong>${status.count}</strong></td>
                                        <td>
                                            <c:if test="${not empty log.userName}">
                                                ${log.userName}
                                            </c:if>
                                            <c:if test="${empty log.userName}">
                                                <span class="text-muted">User #${log.userId}</span>
                                            </c:if>
                                        </td>
                                        <td>
                                            <span class="badge bg-info">${log.action}</span>
                                        </td>
                                        <td>
                                            <small class="text-muted">${log.logTime}</small>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                    <c:if test="${empty logs}">
                        <div class="alert alert-info mt-4">
                            <i class="fas fa-info-circle"></i> Không có log để hiển thị.
                            <br>
                            <small>Nếu không có bản ghi, hãy xác nhận bảng SystemLogs thực sự chứa dữ liệu.</small>
                        </div>
                    </c:if>
                </div>
            </div>

            <div class="alert alert-secondary mt-4">
                <strong>Thông tin:</strong>
                <ul class="mb-0">
                    <li>Hiển thị tối đa 500 log gần nhất</li>
                    <li>Logs được sắp xếp theo thời gian mới nhất trước</li>
                </ul>
            </div>
        </div>
<jsp:include page="footer.jsp" />
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
