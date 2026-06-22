<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Thêm sản phẩm mới</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/fashionStyle.css?v=202603507b">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <link href="${pageContext.request.contextPath}/css/shopProductFormNew.css?v=202603507b" rel="stylesheet" type="text/css"/>
    <meta name="context-path" content="${pageContext.request.contextPath}">
</head>
<body>
    <jsp:include page="/jsp/header.jsp" />

    <div class="container mt-5" >
        <div class="card shadow">
            <div class="card-body">
                <h4 class="mb-4"><i class="fas fa-plus-circle"></i> Thêm sản phẩm mới</h4>

                <c:if test="${not empty error}">
                    <div class="alert alert-danger">${error}</div>
                </c:if>

                <form action="${pageContext.request.contextPath}/admin/products" method="post" enctype="multipart/form-data" id="productForm">
                    <input type="hidden" name="action" value="createProduct" />

                    <!-- Thông tin sản phẩm chung -->
                    <div class="form-section">
                        <h5><i class="fas fa-info-circle"></i> Thông tin sản phẩm</h5>
                        
                        <div class="mb-3">
                            <label class="form-label">Tên sản phẩm <span class="text-danger">*</span></label>
                            <input type="text" name="productName" class="form-control" placeholder="Nhập tên sản phẩm" value="${productName}" required />
                            <c:if test="${not empty nameError}">
                                <div id="nameValidationMessage" class="text-danger small mt-1">${nameError}</div>
                            </c:if>
                            <c:if test="${empty nameError}">
                                <div id="nameValidationMessage" class="text-danger small mt-1" style="display:none;"></div>
                            </c:if>
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Mô tả</label>
                            <textarea name="description" class="form-control" rows="3" placeholder="Nhập mô tả sản phẩm">${description}</textarea>
                        </div>

                        <div class="row">
                            <div class="col-md-4 mb-3">
                                <label class="form-label">Category <span class="text-danger">*</span></label>
                                <select name="categoryId" id="categorySelect" class="form-control" required>
                                    <option value="">-- Chọn category --</option>
                                    <c:forEach items="${categories}" var="cat">
                                        <option value="${cat.id}" ${cat.id == categoryId ? 'selected' : ''}>${cat.name}</option>
                                    </c:forEach>
                                </select>
                            </div>

                            <div class="col-md-4 mb-3">
                                <label class="form-label">Giá cơ bản (base_price) <span class="text-danger">*</span></label>
                                <input type="number" name="basePrice" class="form-control" min="0" step="1000" placeholder="0" value="${basePrice}" required />
                            </div>

                            <div class="col-md-4 mb-3">
                                <label class="form-label">Ảnh đại diện (Thumbnail)</label>
                                <input type="file" name="thumbnail" class="form-control" accept="image/*" />
                            </div>
                        </div>
                    </div>

                    <!-- Biến thể sản phẩm -->
                    <div class="form-section">
                        <h5><i class="fas fa-cube"></i> Biến thể sản phẩm</h5>
                        <p class="text-muted">Thêm ít nhất 1 biến thể</p>

                        <div id="variantsContainer">
                            <div class="variant-item">
                                <div class="row">
                                    <div class="col-md-4 mb-3">
                                        <label class="form-label">Size <span class="text-danger">*</span></label>
                                        <div class="input-group">
                                            <select name="variantSize" class="form-control variantSize" required>
                                                <option value="">-- Chọn size --</option>
                                                <c:forEach items="${allSizes}" var="size">
                                                    <option value="${size.id}">${size.name}</option>
                                                </c:forEach>
                                            </select>
                                            <button type="button" class="btn btn-outline-primary" onclick="openAddSizeModal()">
                                                <i class="fas fa-plus"></i>
                                            </button>
                                        </div>
                                    </div>

                                    <div class="col-md-4 mb-3">
                                        <label class="form-label">Màu <span class="text-danger">*</span></label>
                                        <div class="input-group">
                                            <select name="variantColor" class="form-control variantColor" required>
                                                <option value="">-- Chọn màu --</option>
                                                <c:forEach items="${allColors}" var="color">
                                                    <option value="${color.id}">${color.name}</option>
                                                </c:forEach>
                                            </select>
                                            <button type="button" class="btn btn-outline-primary" onclick="openAddColorModal()">
                                                <i class="fas fa-plus"></i>
                                            </button>
                                        </div>
                                    </div>

                                    <div class="col-md-4 mb-3">
                                        <label class="form-label">Giá <span class="text-danger">*</span></label>
                                        <input type="number" name="variantPrice" class="form-control" min="0" step="1000" placeholder="0" required />
                                    </div>
                                </div>

                                <div class="row">
                                    <div class="col-md-4 mb-3">
                                        <label class="form-label">Ảnh biến thể</label>
                                        <input type="file" name="variantImage" class="form-control" accept="image/*" />
                                    </div>

                                    <div class="col-md-4 mb-3">
                                        <label class="form-label">Số lượng ban đầu</label>
                                        <input type="number" name="initialStock" class="form-control" min="0" value="0" />
                                    </div>
                                </div>

                                <button type="button" class="btn btn-outline-danger btn-sm" onclick="removeVariant(this)">
                                    <i class="fas fa-trash"></i> Xóa biến thể
                                </button>
                            </div>
                        </div>

                        <button type="button" class="btn btn-outline-success mt-3" onclick="addVariant()">
                            <i class="fas fa-plus-circle"></i> Thêm biến thể khác
                        </button>
                    </div>

                    <div class="text-end form-actions">
                        <a href="${pageContext.request.contextPath}/admin/products" class="btn btn-secondary">Hủy</a>
                        <button type="submit" class="btn btn-primary">
                            <i class="fas fa-save"></i> Lưu sản phẩm
                        </button>
                        <div id="priceValidationMessage" class="text-danger small mt-2" style="display:none;">
                            Giá phải &gt;= 0 cho sản phẩm và tất cả biến thể.
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <!-- Modal: Add Size -->
    <div class="modal fade" id="addSizeModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Thêm Size mới</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <input type="text" id="newSizeName" class="form-control" placeholder="Tên size (VD: S, M, L, XL...)" />
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                    <button type="button" class="btn btn-primary" onclick="saveSize()">Lưu</button>
                </div>
            </div>
        </div>
    </div>

    <!-- Modal: Add Color -->
    <div class="modal fade" id="addColorModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Thêm Color mới</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <input type="text" id="newColorName" class="form-control" placeholder="Tên màu (VD: Đỏ, Xanh, Trắng...)" />
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                    <button type="button" class="btn btn-primary" onclick="saveColor()">Lưu</button>
                </div>
            </div>
        </div>
    </div>
   
      
    <div class="modal fade" id="addColorModal" tabindex="-1">
        </div>
    
    <template id="variant-template">
        <div class="variant-item border-top pt-3 mt-3">
            <div class="row">
                <div class="col-md-4 mb-3">
                    <label class="form-label">Size <span class="text-danger">*</span></label>
                    <div class="input-group">
                        <select name="variantSize" class="form-control variantSize" required>
                            </select>
                        <button type="button" class="btn btn-outline-primary" onclick="openAddSizeModal()">
                            <i class="fas fa-plus"></i>
                        </button>
                    </div>
                </div>

                <div class="col-md-4 mb-3">
                    <label class="form-label">Màu <span class="text-danger">*</span></label>
                    <div class="input-group">
                        <select name="variantColor" class="form-control variantColor" required>
                            </select>
                        <button type="button" class="btn btn-outline-primary" onclick="openAddColorModal()">
                            <i class="fas fa-plus"></i>
                        </button>
                    </div>
                </div>

                <div class="col-md-4 mb-3">
                    <label class="form-label">Giá <span class="text-danger">*</span></label>
                    <input type="number" name="variantPrice" class="form-control" min="0" step="1000" placeholder="0" required />
                </div>
            </div>

            <div class="row">
                <div class="col-md-4 mb-3">
                    <label class="form-label">Ảnh biến thể</label>
                    <input type="file" name="variantImage" class="form-control" accept="image/*" />
                </div>

                <div class="col-md-4 mb-3">
                    <label class="form-label">Số lượng ban đầu</label>
                    <input type="number" name="initialStock" class="form-control" min="0" value="0" />
                </div>
            </div>

            <button type="button" class="btn btn-outline-danger btn-sm" onclick="removeVariant(this)">
                <i class="fas fa-trash"></i> Xóa biến thể
            </button>
        </div>
    </template>
    <jsp:include page="/jsp/footer.jsp" />
    
    <script src="${pageContext.request.contextPath}/js/shopProductFormNew.js"></script>
</body>
</html>
