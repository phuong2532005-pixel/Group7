<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="model.ProductDTO"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Duyet san pham</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/fashionStyle.css?v=20260c307b">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin_pro.css?v=20260c307b">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <meta name="context-path" content="${pageContext.request.contextPath}">
</head>
<body>
<jsp:include page="/jsp/header.jsp" />

<div class="admin-wrap">
    <h2>Duyet san pham</h2>
    <p class="note">Chi san pham da duyet moi duoc hien thi tren trang chu.</p>

    <div class="tab-row">
        <button class="tab-btn active" data-tab="pending">Cho duyet</button>
        <button class="tab-btn" data-tab="approved">Da duyet</button>
    </div>

    <div class="tab-panel active" id="tab-pending">
        <table class="admin-table" id="pending-table">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Ten san pham</th>
                    <th>Category</th>
                    <th>Gia co ban</th>
                    <th>Trang thai</th>
                    <th>Hanh dong</th>
                </tr>
            </thead>
            <tbody>
                <%
                    List<ProductDTO> pending = (List<ProductDTO>) request.getAttribute("pendingProducts");
                    if (pending == null || pending.isEmpty()) {
                %>
                <tr><td colspan="6">Khong co san pham cho duyet.</td></tr>
                <% } else {
                    for (ProductDTO p : pending) {
                %>
                <tr data-id="<%= p.getId() %>" data-name="<%= p.getName() %>" data-category="<%= p.getCategoryId() %>" data-price="<%= p.getBasePrice() %>">
                    <td><%= p.getId() %></td>
                    <td>
                        <span class="admin-product-clickable" data-product-id="<%= p.getId() %>" style="cursor:pointer;color:#007bff;text-decoration:underline;"> <%= p.getName() %> </span>
                    </td>
                    <td><%= p.getCategoryId() %></td>
                    <td><%= p.getBasePrice() %></td>
                    <td><span class="badge-status">Pending</span></td>
                    <td>
                        <div class="row-actions">
                            <button type="button" class="btn-approve" onclick="updateStatus(<%= p.getId() %>, 'approve')">Approve</button>
                            <button type="button" class="btn-reject" onclick="updateStatus(<%= p.getId() %>, 'reject')">Reject</button>
                        </div>
                    </td>
                </tr>
                <%
                    }
                }
                %>
            </tbody>
        </table>
    </div>

    <div class="tab-panel" id="tab-approved">
        <table class="admin-table" id="approved-table">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Ten san pham</th>
                    <th>Category</th>
                    <th>Gia co ban</th>
                    <th>Trang thai</th>
                </tr>
            </thead>
            <tbody>
                <%
                    List<ProductDTO> approved = (List<ProductDTO>) request.getAttribute("approvedProducts");
                    if (approved == null || approved.isEmpty()) {
                %>
                <tr><td colspan="5">Chua co san pham da duyet.</td></tr>
                <% } else {
                    for (ProductDTO p : approved) {
                %>
                <tr>
                    <td><%= p.getId() %></td>
                    <td>
                        <span class="admin-product-clickable" data-product-id="<%= p.getId() %>" style="cursor:pointer;color:#007bff;text-decoration:underline;"> <%= p.getName() %> </span>
                    </td>
                    <td><%= p.getCategoryId() %></td>
                    <td><%= p.getBasePrice() %></td>
                    <td><span class="badge-status">Approved</span></td>
                </tr>
                <%
                    }
                }
                %>
            </tbody>
        </table>
    </div>
</div>

<jsp:include page="/jsp/footer.jsp" />


    <script src="${pageContext.request.contextPath}/js/adminProducts.js"></script>
</body>
</html>
