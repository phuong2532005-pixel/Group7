<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Quản lý danh mục</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/fashionStyle.css?v=202060307b">
        <meta name="context-path" content="${pageContext.request.contextPath}">
    </head>
    <body>
        <jsp:include page="/jsp/header.jsp" />

        <div class="container mt-5 mb-5">
            <h3 class="mb-3">Quản lý danh mục</h3>

            <c:if test="${not empty adminCategoryMessage}">
                <div class="alert alert-info">${adminCategoryMessage}</div>
            </c:if>

            <div class="row g-3">
                <div class="col-lg-4">
                    <div class="card">
                        <div class="card-body">
                            <h5 class="card-title">Thêm / cập nhật danh mục</h5>
                            <form action="${pageContext.request.contextPath}/admin/categories" method="post">
                                <input type="hidden" name="action" id="formAction" value="create">
                                <input type="hidden" name="categoryId" id="categoryId" value="">

                                <div class="mb-3">
                                    <label class="form-label">Tên danh mục</label>
                                    <input type="text" class="form-control" name="name" id="categoryName" required>
                                    <div id="categoryNameValidation" class="text-danger small mt-1" style="display:none;"></div>
                                </div>

                                <div class="mb-3">
                                    <label class="form-label">Danh mục cha (tuỳ chọn)</label>
                                    <select class="form-select" name="parentId" id="parentId">
                                        <option value="">-- Không có --</option>
                                        <c:forEach items="${categories}" var="cat">
                                            <option value="${cat.id}">${cat.name}</option>
                                        </c:forEach>
                                    </select>
                                </div>

                                <div class="d-flex gap-2">
                                    <button type="submit" id="submitButton" class="btn btn-primary">Tạo mới</button>
                                    <button type="button" class="btn btn-secondary" onclick="resetCategoryForm()">Đặt lại</button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>

                <div class="col-lg-8">
                    <div class="card">
                        <div class="card-body">
                            <h5 class="card-title">Danh sách danh mục</h5>
                            <div class="table-responsive">
                                <table class="table table-bordered table-hover align-middle">
                                    <thead>
                                        <tr>
                                            <th>ID</th>
                                            <th>Tên</th>
                                            <th>Parent ID</th>
                                            <th>Thao tác</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach items="${categories}" var="cat">
                                            <tr>
                                                <td>${cat.id}</td>
                                                <td>${cat.name}</td>
                                                <td>${cat.parentId}</td>
                                                <td>
                                                    <button type="button"
                                                            class="btn btn-sm btn-outline-primary"
                                                            data-id="${cat.id}"
                                                            data-name="${cat.name}"
                                                            data-parent="${cat.parentId}"
                                                            onclick="editCategory(this)">
                                                        <i class="fa-solid fa-pen"></i>
                                                    </button>
                                                    <form action="${pageContext.request.contextPath}/admin/categories"
                                                          method="post"
                                                          class="d-inline"
                                                          onsubmit="return confirm('Bạn có chắc muốn xóa danh mục này?');">
                                                        <input type="hidden" name="action" value="delete">
                                                        <input type="hidden" name="categoryId" value="${cat.id}">
                                                        <button type="submit" class="btn btn-sm btn-outline-danger">
                                                            <i class="fa-solid fa-trash"></i>
                                                        </button>
                                                    </form>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                                <c:if test="${empty categories}">
                                    <div class="alert alert-info mt-3">Chưa có danh mục nào.</div>
                                </c:if>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <jsp:include page="/jsp/footer.jsp" />

        <script>
            var categoryNameCheckTimeout = null;
            var categoryNameValid = false;

            function editCategory(button) {
                var id = button.getAttribute('data-id');
                var name = button.getAttribute('data-name');
                var parentId = button.getAttribute('data-parent');

                document.getElementById('formAction').value = 'update';
                document.getElementById('categoryId').value = id;
                document.getElementById('categoryName').value = name;
                document.getElementById('parentId').value = parentId || '';
                document.getElementById('submitButton').innerText = 'Cập nhật';
                document.getElementById('categoryName').focus();
                validateCategoryName();
            }

            function resetCategoryForm() {
                document.getElementById('formAction').value = 'create';
                document.getElementById('categoryId').value = '';
                document.getElementById('categoryName').value = '';
                document.getElementById('parentId').value = '';
                document.getElementById('submitButton').innerText = 'Tạo mới';
                validateCategoryName();
            }

            function validateCategoryName() {
                var nameInput = document.getElementById('categoryName');
                var submitBtn = document.getElementById('submitButton');
                var messageEl = document.getElementById('categoryNameValidation');
                var categoryId = document.getElementById('categoryId').value;
                var parentId = document.getElementById('parentId').value;
                var name = nameInput.value.trim();

                function showMessage(msg) {
                    messageEl.textContent = msg;
                    messageEl.style.display = msg ? 'block' : 'none';
                }

                if (!name) {
                    showMessage('Vui lòng nhập tên danh mục.');
                    categoryNameValid = false;
                    submitBtn.disabled = true;
                    return Promise.resolve(false);
                }

                var contextPath = document.querySelector('meta[name="context-path"]').getAttribute('content');
                return fetch(contextPath + '/admin/categories?action=checkCategoryName&name=' + encodeURIComponent(name)
                    + (categoryId ? '&categoryId=' + encodeURIComponent(categoryId) : '')
                    + (parentId ? '&parentId=' + encodeURIComponent(parentId) : ''))
                    .then(function (res) { return res.json(); })
                    .then(function (data) {
                        if (data && data.exists) {
                            showMessage('Tên danh mục đã tồn tại trong cùng danh mục cha.');
                            categoryNameValid = false;
                            submitBtn.disabled = true;
                            return false;
                        } else {
                            showMessage('');
                            categoryNameValid = true;
                            submitBtn.disabled = false;
                            return true;
                        }
                    })
                    .catch(function () {
                        // Nếu không kiểm tra được, vẫn cho phép submit nhưng không hiển thị lỗi
                        showMessage('');
                        categoryNameValid = true;
                        submitBtn.disabled = false;
                        return true;
                    });
            }

            document.getElementById('categoryName').addEventListener('input', function () {
                if (categoryNameCheckTimeout) {
                    clearTimeout(categoryNameCheckTimeout);
                }
                categoryNameCheckTimeout = setTimeout(validateCategoryName, 250);
            });

            document.getElementById('parentId').addEventListener('change', function () {
                validateCategoryName();
            });

            document.getElementById('categoryName').addEventListener('keydown', function (e) {
                if (e.key === 'Enter') {
                    e.preventDefault();
                    validateCategoryName();
                }
            });

            document.querySelector('form[action$="/admin/categories"]').addEventListener('submit', function (e) {
                e.preventDefault();
                validateCategoryName().then(function (ok) {
                    if (ok && categoryNameValid) {
                        e.target.submit();
                    }
                });
            });

            // Validate on initial load
            validateCategoryName();
        </script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
