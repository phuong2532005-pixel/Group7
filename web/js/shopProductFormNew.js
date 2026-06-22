
let variantCount = 1;
let nameValid = true;
let nameCheckTimeout = null;

function setNameValidationMessage(message, visible) {
    const msg = document.getElementById('nameValidationMessage');
    if (!msg) {
        return;
    }
    if (visible) {
        msg.textContent = message;
        msg.style.display = 'block';
    } else {
        msg.textContent = '';
        msg.style.display = 'none';
    }
}

function checkProductName(name) {
    const meta = document.querySelector('meta[name="context-path"]');
    const contextPath = meta ? meta.getAttribute('content') : '';
    if (!name || !name.trim()) {
        nameValid = false;
        setNameValidationMessage('Vui lòng nhập tên sản phẩm.', true);
        refreshCreateProductSubmitState();
        return Promise.resolve(false);
    }
    return fetch(contextPath + '/admin/products?action=checkProductName&productName=' + encodeURIComponent(name.trim()))
            .then(res => res.json())
            .then(data => {
                if (data && data.exists) {
                    nameValid = false;
                    setNameValidationMessage('❌ Tên sản phẩm đã tồn tại trong hệ thống.', true);
                } else {
                    nameValid = true;
                    setNameValidationMessage('', false);
                }
                refreshCreateProductSubmitState();
                return nameValid;
            })
            .catch(() => {
                // In case of error, keep validation from blocking form submit.
                nameValid = true;
                setNameValidationMessage('', false);
                refreshCreateProductSubmitState();
                return true;
            });
}

function addVariant() {
    variantCount++; // (Nếu bạn có dùng biến đếm)

    // 1. Lấy dữ liệu danh sách Size và Màu hiện có trên màn hình
    const sizeSelect = document.querySelector('.variantSize');
    const colorSelect = document.querySelector('.variantColor');
    const sizeOptionsHtml = sizeSelect ? sizeSelect.innerHTML : '<option value="">-- Chọn size --</option>';
    const colorOptionsHtml = colorSelect ? colorSelect.innerHTML : '<option value="">-- Chọn màu --</option>';

    // 2. Lôi cái khuôn đúc tàng hình ra
    const template = document.getElementById('variant-template');

    // 3. Nhân bản (photocopy) toàn bộ ruột của cái khuôn đó
    const newVariant = template.content.cloneNode(true);

    // 4. Bơm danh sách Size và Màu vào bản sao chép
    newVariant.querySelector('.variantSize').innerHTML = sizeOptionsHtml;
    newVariant.querySelector('.variantColor').innerHTML = colorOptionsHtml;

    // 5. In bản sao đó lên màn hình
    document.getElementById('variantsContainer').appendChild(newVariant);

    // 6. Cập nhật trạng thái nút submit (nếu có)
    if (typeof refreshCreateProductSubmitState === 'function') {
        refreshCreateProductSubmitState();
    }
}

function removeVariant(btn) {
    btn.closest('.variant-item').remove();
    refreshCreateProductSubmitState();
}

function refreshCreateProductSubmitState() {
    const form = document.getElementById('productForm');
    if (!form) {
        return;
    }
    const submitBtn = form.querySelector('button[type="submit"]');
    const msg = document.getElementById('priceValidationMessage');
    if (!submitBtn) {
        return;
    }

    const basePriceInput = form.querySelector('input[name="basePrice"]');
    const variantPriceInputs = form.querySelectorAll('input[name="variantPrice"]');

    const isNonNegative = (input) => {
        if (!input || input.value === '') {
            return false;
        }
        const v = Number(input.value);
        return Number.isFinite(v) && v >= 0;
    };

    let ok = isNonNegative(basePriceInput);
    let hasEmptyPrice = false;
    let hasNegativePrice = false;

    if (!basePriceInput || basePriceInput.value === '') {
        hasEmptyPrice = true;
    } else if (Number(basePriceInput.value) < 0) {
        hasNegativePrice = true;
    }

    variantPriceInputs.forEach((input) => {
        if (!isNonNegative(input)) {
            ok = false;
        }
        if (!input || input.value === '') {
            hasEmptyPrice = true;
        } else if (Number(input.value) < 0) {
            hasNegativePrice = true;
        }
    });

    const nameInput = form.querySelector('input[name="productName"]');
    if (nameInput) {
        ok = ok && nameValid;
    }

    submitBtn.disabled = !ok;
    if (msg) {
        if (ok) {
            msg.style.display = 'none';
        } else {
            msg.style.display = 'block';
            if (hasNegativePrice) {
                msg.textContent = 'Giá không được nhỏ hơn 0.';
            } else if (hasEmptyPrice) {
                msg.textContent = 'Vui lòng nhập giá cho sản phẩm và tất cả biến thể.';
            } else {
                msg.textContent = 'Giá phải >= 0 cho sản phẩm và tất cả biến thể.';
            }
        }
    }
}

function openAddCategoryModal() {
    new bootstrap.Modal(document.getElementById('addCategoryModal')).show();
}

function openAddSizeModal() {
    new bootstrap.Modal(document.getElementById('addSizeModal')).show();
}

function openAddColorModal() {
    new bootstrap.Modal(document.getElementById('addColorModal')).show();
}

function saveCategory() {
    var meta = document.querySelector('meta[name="context-path"]');
    var contextPath = meta ? meta.getAttribute('content') : '';
    const name = document.getElementById('newCategoryName').value.trim();
    if (!name) {
        alert('Vui lòng nhập tên category');
        return;
    }

    const formData = new FormData();
    formData.append('action', 'addCategory');
    formData.append('categoryName', name);

    fetch(contextPath + '/admin/products', {
        method: 'POST',
        body: formData
    })
            .then(res => res.json())
            .then(data => {
                if (data.success && data.categoryId) {
                    // Add to dropdown
                    const select = document.getElementById('categorySelect');
                    const option = document.createElement('option');
                    option.value = data.categoryId;
                    option.textContent = name;
                    select.appendChild(option);
                    select.value = data.categoryId;

                    // Clear and close modal
                    document.getElementById('newCategoryName').value = '';
                    bootstrap.Modal.getInstance(document.getElementById('addCategoryModal')).hide();
                } else {
                    alert('Thêm category thất bại: ' + (data.error || 'Unknown error'));
                }
            })
            .catch(err => {
                console.error('Error:', err);
                alert('Lỗi khi thêm category');
            });
}

function saveSize() {
    var meta = document.querySelector('meta[name="context-path"]');
    var contextPath = meta ? meta.getAttribute('content') : '';
    const name = document.getElementById('newSizeName').value.trim();
    if (!name) {
        alert('Vui lòng nhập tên size');
        return;
    }

    const formData = new FormData();
    formData.append('action', 'addSize');
    formData.append('sizeName', name);

    fetch(contextPath + '/admin/products', {
        method: 'POST',
        body: formData
    })
            .then(res => res.json())
            .then(data => {
                if (data.success && data.sizeId) {
                    // Add to all size dropdowns
                    const selects = document.querySelectorAll('.variantSize');
                    selects.forEach(select => {
                        const option = document.createElement('option');
                        option.value = data.sizeId;
                        option.textContent = name;
                        select.appendChild(option);
                    });

                    // Clear and close modal
                    document.getElementById('newSizeName').value = '';
                    bootstrap.Modal.getInstance(document.getElementById('addSizeModal')).hide();
                } else {
                    alert('Thêm size thất bại: ' + (data.error || 'Unknown error'));
                }
            })
            .catch(err => {
                console.error('Error:', err);
                alert('Lỗi khi thêm size');
            });
}

function saveColor() {
    var meta = document.querySelector('meta[name="context-path"]');
    var contextPath = meta ? meta.getAttribute('content') : '';
    const name = document.getElementById('newColorName').value.trim();
    if (!name) {
        alert('Vui lòng nhập tên màu');
        return;
    }

    const formData = new FormData();
    formData.append('action', 'addColor');
    formData.append('colorName', name);

    fetch(contextPath + '/admin/products', {
        method: 'POST',
        body: formData
    })
            .then(res => res.json())
            .then(data => {
                if (data.success && data.colorId) {
                    // Add to all color dropdowns
                    const selects = document.querySelectorAll('.variantColor');
                    selects.forEach(select => {
                        const option = document.createElement('option');
                        option.value = data.colorId;
                        option.textContent = name;
                        select.appendChild(option);
                    });

                    // Clear and close modal
                    document.getElementById('newColorName').value = '';
                    bootstrap.Modal.getInstance(document.getElementById('addColorModal')).hide();
                } else {
                    alert('Thêm color thất bại: ' + (data.error || 'Unknown error'));
                }
            })
            .catch(err => {
                console.error('Error:', err);
                alert('Lỗi khi thêm color');
            });
}

document.addEventListener('DOMContentLoaded', function () {
    const form = document.getElementById('productForm');
    if (!form) {
        return;
    }

    const nameInput = form.querySelector('input[name="productName"]');
    if (nameInput) {
        const initialMessage = document.getElementById('nameValidationMessage');
        if (initialMessage && initialMessage.textContent.trim()) {
            nameValid = false;
        } else if (!nameInput.value || !nameInput.value.trim()) {
            nameValid = false;
        }

        function scheduleNameCheck() {
            if (nameCheckTimeout) {
                clearTimeout(nameCheckTimeout);
            }
            nameCheckTimeout = setTimeout(function () {
                checkProductName(nameInput.value);
            }, 250);
        }

        nameInput.addEventListener('input', scheduleNameCheck);
        nameInput.addEventListener('change', scheduleNameCheck);
        nameInput.addEventListener('keydown', function (e) {
            if (e.key === 'Enter') {
                e.preventDefault();
                checkProductName(nameInput.value);
            }
        });
    }

    form.addEventListener('input', function (e) {
        if (e.target && (e.target.name === 'basePrice' || e.target.name === 'variantPrice')) {
            refreshCreateProductSubmitState();
        }
    });

    form.addEventListener('change', function (e) {
        if (e.target && (e.target.name === 'basePrice' || e.target.name === 'variantPrice')) {
            refreshCreateProductSubmitState();
        }
    });

    // Guard submit (including Enter key submit) so duplicate-name error is shown inline without reload.
    form.addEventListener('submit', function (e) {
        e.preventDefault();
        const rawName = nameInput ? nameInput.value : '';
        checkProductName(rawName).then(function (ok) {
            refreshCreateProductSubmitState();
            if (ok) {
                form.submit();
            }
        });
    });

    refreshCreateProductSubmitState();
});
    