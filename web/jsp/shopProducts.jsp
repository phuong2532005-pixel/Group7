<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Quản lý sản phẩm</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/css/shop_product.css?v=20260347408b" rel="stylesheet" type="text/css"/>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <meta name="context-path" content="${pageContext.request.contextPath}">
</head>
    <body>
        <jsp:include page="/jsp/header.jsp" />

        <div class="list-wrap">
            <div class="list-header">
                <h2 class="section-title-shop">Quản lý sản phẩm</h2>
            </div>
            <div class="list-header-btn">
                <button class="action-btn" onclick="openProductForm()" >
                    <i class="fas fa-plus-circle"></i> Nhập sản phẩm mới
                </button>
            </div>

            <form action="${pageContext.request.contextPath}/admin/products" method="get" class="row g-2 align-items-end mb-3">
                <div class="col-md-5">
                    <label class="form-label mb-1">Tìm kiếm sản phẩm</label>
                    <input type="text" name="keyword" class="form-control" value="${keyword}" placeholder="Nhập tên sản phẩm...">
                </div>
                <div class="col-md-2 d-grid">
                    <button type="submit" class="btn btn-primary">Tìm kiếm</button>
                </div>
                <div class="col-md-2 d-grid">
                    <a href="${pageContext.request.contextPath}/admin/products" class="btn btn-secondary">Xóa lọc</a>
                </div>
            </form>

            <c:if test="${not empty message}">
                <div class="alert alert-success" >${message}</div>
            </c:if>

            <c:if test="${not empty error}">
                <div class="alert alert-danger" >${error}</div>
            </c:if>

            <c:choose>
                <c:when test="${empty productStocks}">
                    <div class="empty">Chưa có sản phẩm nào trong hệ thống.</div>
                </c:when>
                <c:otherwise>
                    <div class="table-responsive" >
                        <table class="table table-bordered table-hover align-middle">
                            <thead>
                                <tr>
                                    <th>Tên sản phẩm</th>
                                    <th>Số lượng chung</th>
                                    <th>Chi tiết</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${productStocks}" var="p">
                                    <tr>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/product?id=${p.productId}">
                                                ${p.productName}
                                            </a>
                                        </td>
                                        <td><strong>${p.totalStock}</strong></td>
                                        <td>
                                            <details>
                                                <summary >Chi tiết</summary>
                                                <div >
                                                    <button type="button" class="btn btn-sm btn-success" onclick="openAddVariantModal('${p.productId}')">
                                                        <i class="fas fa-plus"></i> Thêm biến thể
                                                    </button>
                                                </div>
                                                <div >
                                                    <div class="mb-3 p-2 border rounded bg-light">
                                                        <form action="${pageContext.request.contextPath}/admin/products" method="post" enctype="multipart/form-data" class="row g-2 align-items-end">
                                                            <input type="hidden" name="action" value="updateProduct" />
                                                            <input type="hidden" name="productId" value="${p.productId}" />
                                                            <input type="hidden" name="page" value="${page}" />
                                                            <input type="hidden" name="existingThumbnail" value="${productDetails[p.productId].thumbnail}" />
                                                            <div class="col-md-3">
                                                                <label class="form-label mb-1">Tên sản phẩm</label>
                                                                <input type="text" class="form-control form-control-sm product-name-input" name="productName" value="${p.productName}" required>
                                                                <div class="name-validation-message text-danger small mt-1" style="display:none;"></div>
                                                            </div>
                                                            <div class="col-md-3">
                                                                <label class="form-label mb-1">Mô tả</label>
                                                                <input type="text" class="form-control form-control-sm" name="description" value="${productDetails[p.productId].description}">
                                                            </div>
                                                            <div class="col-md-2">
                                                                <label class="form-label mb-1">Category</label>
                                                                <select class="form-select form-select-sm" name="categoryId" required>
                                                                    <c:forEach items="${categories}" var="cat">
                                                                        <option value="${cat.id}" ${cat.id == productDetails[p.productId].categoryId ? 'selected' : ''}>${cat.name}</option>
                                                                    </c:forEach>
                                                                </select>
                                                            </div>
                                                            <div class="col-md-2">
                                                                <label class="form-label mb-1">Giá cơ bản</label>
                                                                <input type="number" class="form-control form-control-sm" name="basePrice" min="1" value="${productDetails[p.productId].basePrice}" required>
                                                            </div>
                                                            <div class="col-md-2">
                                                                <label class="form-label mb-1">Trạng thái</label>
                                                                <select class="form-select form-select-sm" name="status">
                                                                    <option value="1" ${productDetails[p.productId].status == 1 ? 'selected' : ''}>Hiển thị</option>
                                                                    <option value="0" ${productDetails[p.productId].status == 0 ? 'selected' : ''}>Ẩn</option>
                                                                </select>
                                                            </div>
                                                            <div class="col-md-2">
                                                                <label class="form-label mb-1">Thumbnail mới</label>
                                                                <input type="file" class="form-control form-control-sm" name="thumbnail" accept="image/*">
                                                            </div>
                                                            <div class="col-md-1 d-grid">
                                                                <button type="submit" class="btn btn-sm btn-primary">Lưu</button>
                                                            </div>
                                                        </form>

                                                        <form action="${pageContext.request.contextPath}/admin/products" method="post" class="mt-2" onsubmit="return confirm('Bạn có chắc muốn xóa sản phẩm này?');">
                                                            <input type="hidden" name="action" value="deleteProduct" />
                                                            <input type="hidden" name="productId" value="${p.productId}" />
                                                            <input type="hidden" name="page" value="${page}" />
                                                            <button type="submit" class="btn btn-sm btn-outline-danger">Xóa sản phẩm</button>
                                                        </form>
                                                    </div>

                                                    <table class="table table-sm table-striped table-bordered align-middle" >
                                                        <thead>
                                                            <tr>
                                                                <th>Ảnh biến thể</th>
                                                                <th>Tên biến thể</th>
                                                                <th>Size</th>
                                                                <th>Màu</th>
                                                                <th>Giá</th>
                                                                <th>Số lượng</th>
                                                                <th>Cập nhật biến thể</th>
                                                            </tr>
                                                        </thead>
                                                        <tbody>
                                                            <c:forEach items="${p.variants}" var="v">
                                                                <tr>
                                                                    <td >
                                                                        <c:choose>
                                                                            <c:when test="${not empty v.variantImageUrl}">
                                                                                <c:set var="imgPath" value="${v.variantImageUrl}" />
                                                                                <c:if test="${not fn:contains(imgPath, '.')}">
                                                                                    <c:set var="imgPath" value="${imgPath}.jpg" />
                                                                                </c:if>
                                                                                <c:choose>
                                                                                    <c:when test="${fn:startsWith(imgPath,'http://') or fn:startsWith(imgPath,'https://')}">
                                                                                        <img src="${imgPath}" alt="${v.productName}" style="width:64px;height:64px;object-fit:cover;border-radius:8px;">
                                                                                    </c:when>
                                                                                    <c:when test="${fn:contains(imgPath,'/')}">
                                                                                        <c:choose>
                                                                                            <c:when test="${fn:startsWith(imgPath,'/')}">
                                                                                                <img src="${pageContext.request.contextPath}${imgPath}" alt="${v.productName}" style="width:64px;height:64px;object-fit:cover;border-radius:8px;">
                                                                                            </c:when>
                                                                                            <c:otherwise>
                                                                                                <img src="${pageContext.request.contextPath}/${imgPath}" alt="${v.productName}" style="width:64px;height:64px;object-fit:cover;border-radius:8px;">
                                                                                            </c:otherwise>
                                                                                        </c:choose>
                                                                                    </c:when>
                                                                                    <c:otherwise>
                                                                                        <img src="${pageContext.request.contextPath}/img/products/${imgPath}" alt="${v.productName}" style="width:64px;height:64px;object-fit:cover;border-radius:8px;">
                                                                                    </c:otherwise>
                                                                                </c:choose>
                                                                            </c:when>
                                                                            <c:otherwise>
                                                                                <div ></div>
                                                                            </c:otherwise>
                                                                        </c:choose>
                                                                    </td>
                                                                    <td>${v.productName}</td>
                                                                    <td>${v.sizeName}</td>
                                                                    <td>${v.colorName}</td>
                                                                    <td><strong><fmt:formatNumber value="${v.price}" type="number"/></strong></td>
                                                                    <td><strong>${v.currentStock}</strong></td>
                                                                    <td style="min-width: 420px;">
                                                                        <div style="display:flex; gap:8px; align-items:center;">
                                                                            <form action="${pageContext.request.contextPath}/admin/products" method="post" >
                                                                                <input type="hidden" name="action" value="addStock" />
                                                                                <input type="hidden" name="variantId" value="${v.variantId}" />
                                                                                <input type="hidden" name="page" value="${page}" />
                                                                                <input type="number" name="addQuantity" step="1" class="form-control" placeholder="Nhập + để thêm, - để trừ" required />
                                                                                <button type="submit" class="btn btn-sm btn-primary">Lưu</button>
                                                                            </form>

                                                                            <form action="${pageContext.request.contextPath}/admin/products" method="post" enctype="multipart/form-data">
                                                                                <input type="hidden" name="action" value="updateVariant" />
                                                                                <input type="hidden" name="variantId" value="${v.variantId}" />
                                                                                <input type="hidden" name="page" value="${page}" />
                                                                                <input type="hidden" name="existingImage" value="${v.variantImageUrl}" />
                                                                                <select name="sizeId" class="form-select form-select-sm" required>
                                                                                    <c:forEach items="${allSizes}" var="s">
                                                                                        <option value="${s.id}" ${s.sizeName == v.sizeName ? 'selected' : ''}>${s.sizeName}</option>
                                                                                    </c:forEach>
                                                                                </select>
                                                                                <select name="colorId" class="form-select form-select-sm" required>
                                                                                    <c:forEach items="${allColors}" var="c">
                                                                                        <option value="${c.id}" ${c.colorName == v.colorName ? 'selected' : ''}>${c.colorName}</option>
                                                                                    </c:forEach>
                                                                                </select>
                                                                                <input type="number" name="price" class="form-control form-control-sm" min="1" value="${v.price}" placeholder="Giá mới" required />
                                                                                <input type="file" name="variantImage" class="form-control form-control-sm" accept="image/*" />
                                                                                <button type="submit" class="btn btn-sm btn-outline-secondary">Sửa</button>
                                                                            </form>

                                                                            <form action="${pageContext.request.contextPath}/admin/products" method="post"  onsubmit="return confirm('Bạn có chắc muốn xóa biến thể này?');">
                                                                                <input type="hidden" name="action" value="deleteVariant" />
                                                                                <input type="hidden" name="variantId" value="${v.variantId}" />
                                                                                <input type="hidden" name="page" value="${page}" />
                                                                                <button type="submit" class="btn btn-sm btn-outline-danger">Xóa</button>
                                                                            </form>
                                                                        </div>
                                                                    </td>
                                                                </tr>
                                                            </c:forEach>
                                                        </tbody>
                                                    </table>
                                                </div>
                                            </details>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:otherwise>
            </c:choose>

            <c:if test="${totalPages > 1}">
                <div class="paging-container" >
                    <div class="paging-info" >
                        Trang <strong>${page}</strong>/<strong>${totalPages}</strong>
                    </div>

                    <div class="paging" >
                        <%-- Nút Previous --%>
                        <c:if test="${page > 1}">
                            <c:url var="prevPageUrl" value="/admin/products">
                                <c:param name="page" value="${page - 1}"/>
                                <c:if test="${not empty keyword}">
                                    <c:param name="keyword" value="${keyword}"/>
                                </c:if>
                            </c:url>
                            <a class="page-link" href="${prevPageUrl}">
                                <i class="fa-solid fa-chevron-left"></i>
                            </a>
                        </c:if>

                        <c:forEach begin="1" end="${totalPages}" var="i">
                            <c:choose>
                                <c:when test="${i == 1 || i == totalPages || (i >= page - 2 && i <= page + 2)}">
                                                <c:url var="pageUrl" value="/admin/products">
                                                    <c:param name="page" value="${i}"/>
                                                    <c:if test="${not empty keyword}">
                                                        <c:param name="keyword" value="${keyword}"/>
                                                    </c:if>
                                                </c:url>
                                                <a class="page-link ${i == page ? 'active' : ''}" 
                                                    href="${pageUrl}">
                                        ${i}
                                    </a>
                                </c:when>
                                <c:when test="${i == page - 3 || i == page + 3}">
                                    <span class="page-dots" >...</span>
                                </c:when>
                            </c:choose>
                        </c:forEach>

                        <%-- Nút Next --%>
                        <c:if test="${page < totalPages}">
                            <c:url var="nextPageUrl" value="/admin/products">
                                <c:param name="page" value="${page + 1}"/>
                                <c:if test="${not empty keyword}">
                                    <c:param name="keyword" value="${keyword}"/>
                                </c:if>
                            </c:url>
                            <a class="page-link" href="${nextPageUrl}">
                                <i class="fa-solid fa-chevron-right"></i>
                            </a>
                        </c:if>
                    </div>
                </div>
            </c:if>
        </div>

        <div class="modal fade" id="addVariantModal" tabindex="-1" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered" >
                <div class="modal-content">
                    <form action="${pageContext.request.contextPath}/admin/products" method="post" enctype="multipart/form-data">
                        <div class="modal-header">
                            <h5 class="modal-title">Thêm biến thể mới</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body">
                            <input type="hidden" name="action" value="addVariant" />
                            <input type="hidden" name="productId" id="modalProductId" />
                            <input type="hidden" name="page" value="${page}" />

                            <div class="row g-2">
                                <div class="col-md-6">
                                    <label class="form-label">Size</label>
                                    <select name="sizeId" class="form-select" required>
                                        <option value="">-- Chọn size --</option>
                                        <c:forEach items="${allSizes}" var="s">
                                            <option value="${s.id}">${s.name}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label">Màu</label>
                                    <select name="colorId" class="form-select" required>
                                        <option value="">-- Chọn màu --</option>
                                        <c:forEach items="${allColors}" var="c">
                                            <option value="${c.id}">${c.name}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label">Giá</label>
                                    <input type="number" name="price" class="form-control" min="0" step="1000" required>
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label">Số lượng ban đầu</label>
                                    <input type="number" name="initialStock" class="form-control" min="0" value="0">
                                </div>
                                <div class="col-12">
                                    <label class="form-label">Ảnh biến thể (tùy chọn)</label>
                                    <input type="file" name="variantImage" class="form-control" accept="image/*">
                                </div>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                            <button type="submit" class="btn btn-primary">Lưu biến thể</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <jsp:include page="/jsp/footer.jsp" />
        
        
        
        <script src="${pageContext.request.contextPath}/js/shopProducts.js"></script>
</body>
</html>