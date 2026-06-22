
function changeMainImage(src, thumb) {

    var mainImage = document.getElementById('main-image');

    if(mainImage){
        mainImage.src = src;
    }

    var thumbs = document.querySelectorAll('.gallery-thumb');

    thumbs.forEach(function(t){
        t.classList.remove('active');
    });

    if(thumb){
        thumb.classList.add('active');
    }

}

(function () {
    var sizeButtons = document.querySelectorAll('.size-btn');
    var colorButtons = document.querySelectorAll('.color-btn');
    var galleryThumbs = document.querySelectorAll('.gallery-thumb');
    var priceEl = document.getElementById('detail-price');
    var imageEl = document.getElementById('main-image');
    var qtyInput = document.getElementById('qty-input');
    var guestForm = document.getElementById('guest-login-form');
    var returnUrlEl = document.getElementById('returnUrl');
    var selectedVariantInput = document.getElementById('selected-variant');

    var selectedSize = null;
    var selectedColor = null;

    // Normalize variantsData in case JSP generated string values.
    // This ensures numeric comparisons work reliably when selecting variants.
    var variantsDataSafe = Array.isArray(variantsData) ? variantsData.map(function (v) {
        return {
            id: Number(v.id),
            sizeId: Number(v.sizeId),
            colorId: Number(v.colorId),
            price: Number(v.price),
            imageUrl: v.imageUrl || ''
        };
    }) : [];

    function formatPrice(value) {
        try {
            return new Intl.NumberFormat('vi-VN').format(value) + ' đ';
        } catch (err) {
            return value + ' đ';
        }
    }

  function resolveImageUrl(rawPath) {
    if (!rawPath || rawPath.trim() === '') {
        return '';
    }
    var imgPath = rawPath.trim();
    
    // Thêm đuôi .jpg nếu chưa có
    if (imgPath.indexOf('.') === -1) {
        imgPath = imgPath + '.jpg';
    }
    
    // Trả về luôn nếu là link tuyệt đối
    if (imgPath.startsWith('http://') || imgPath.startsWith('https://')) {
        return imgPath;
    }
    
    // Sử dụng biến contextPath của JavaScript thay vì thẻ JSP
    var base = typeof contextPath !== 'undefined' ? contextPath : '';
    
    if (imgPath.startsWith('/')) {
        return base + imgPath;
    }
    if (imgPath.indexOf('/') === -1) {
        return base + '/img/products/' + imgPath;
    }
    return base + '/' + imgPath;
}
    function normalizeUrl(url) {
        var anchor = document.createElement('a');
        anchor.href = url;
        return anchor.href;
    }

function focusThumbnailByUrl(imageUrl) {

    if (!imageUrl) return;

    var matched = null;

    galleryThumbs.forEach(function (thumb) {

        if (thumb.src.includes(imageUrl)) {
            matched = thumb;
        }

    });

    if (matched) {

        // dùng chính ảnh variant làm ảnh lớn
        changeMainImage(imageUrl, matched);

    } else {

        changeMainImage(imageUrl);

    }

}

function updateSelectedVariant() {
        if (selectedSize == null || selectedColor == null || Number.isNaN(selectedSize) || Number.isNaN(selectedColor)) {
            if (selectedVariantInput) {
                selectedVariantInput.value = '';
            }
            return;
        }

        // Find matching variant
        var variant = variantsDataSafe.find(function (v) {
            return v.sizeId === selectedSize && v.colorId === selectedColor;
        });

        if (variant) {
            // Update price
            if (priceEl) {
                priceEl.textContent = formatPrice(variant.price);
            }

            // Update hidden input
            if (selectedVariantInput) {
                selectedVariantInput.value = variant.id;
            }

            // TỰ ĐỘNG CẬP NHẬT ẢNH CHO ĐÚNG VARIANT
            if (variant.imageUrl) {
                var resolvedImage = resolveImageUrl(variant.imageUrl);
                focusThumbnailByUrl(resolvedImage);
            }

        } else if (selectedVariantInput) {
            // No valid combination selected
            selectedVariantInput.value = '';
        }
    }

    function setActiveById(buttons, attrName, idValue) {
        buttons.forEach(function (b) {
            var val = parseInt(b.getAttribute(attrName), 10);
            b.classList.toggle('active', val === idValue);
        });
    }

    function refreshAvailability() {
        sizeButtons.forEach(function (btn) {
            var sid = parseInt(btn.getAttribute('data-size-id'), 10);
            var enabled = selectedColor == null || Number.isNaN(selectedColor) || variantsDataSafe.some(function (v) {
                return v.sizeId === sid && v.colorId === selectedColor;
            });
            btn.disabled = !enabled;
        });

        colorButtons.forEach(function (btn) {
            var cid = parseInt(btn.getAttribute('data-color-id'), 10);
            var enabled = selectedSize == null || Number.isNaN(selectedSize) || variantsDataSafe.some(function (v) {
                return v.sizeId === selectedSize && v.colorId === cid;
            });
            btn.disabled = !enabled;
        });
    }

    function applySelectionState() {
        setActiveById(sizeButtons, 'data-size-id', selectedSize);
        setActiveById(colorButtons, 'data-color-id', selectedColor);
        refreshAvailability();
        updateSelectedVariant();
    }

    sizeButtons.forEach(function (btn) {
        btn.addEventListener('click', function () {
            if (btn.disabled) {
                return;
            }
            var parsedSize = parseInt(btn.getAttribute('data-size-id'), 10);
            if (Number.isNaN(parsedSize)) {
                selectedSize = null;
            } else {
                selectedSize = (selectedSize === parsedSize) ? null : parsedSize;
            }
            applySelectionState();
        });
    });

colorButtons.forEach(function (btn) {
    btn.addEventListener('click', function () {

        if (btn.disabled) return;

        var parsedColor = parseInt(btn.getAttribute('data-color-id'), 10);
        if (Number.isNaN(parsedColor)) {
            selectedColor = null;
        } else {
            selectedColor = (selectedColor === parsedColor) ? null : parsedColor;
        }

        // đổi ảnh theo màu ngay lập tức khi đang chọn màu
        if (selectedColor != null) {
            var variantByColor = variantsDataSafe.find(function(v){
                return v.colorId === selectedColor;
            });

            if(variantByColor && variantByColor.imageUrl){
                var resolvedImage = resolveImageUrl(variantByColor.imageUrl);
                focusThumbnailByUrl(resolvedImage);
            }
        }

        applySelectionState();

    });
});

    // Initialize without any pre-selected size/color.
    applySelectionState();

    if (guestForm && returnUrlEl) {
        guestForm.addEventListener('submit', function () {
            var qtyVal = qtyInput ? qtyInput.value : '1';
            var returnUrl = '/product?id=' + productId;
            if (selectedVariantInput && selectedVariantInput.value) {
                returnUrl += '&variantId=' + encodeURIComponent(selectedVariantInput.value);
            }
            if (qtyVal) {
                returnUrl += '&quantity=' + encodeURIComponent(qtyVal);
            }
            returnUrlEl.value = returnUrl;
        });
    }

    // Add to cart AJAX handler
    var addToCartBtn = document.getElementById('add-to-cart-btn');
    if (addToCartBtn) {
        addToCartBtn.addEventListener('click', function (e) {
            e.preventDefault();
            var variantId = selectedVariantInput ? selectedVariantInput.value : '';
            var quantity = qtyInput ? qtyInput.value : '1';

            if (variantId == null || String(variantId).trim() === '') {
                alert('Vui lòng chọn size và màu trước khi thêm vào giỏ.');
                return;
            }

            var body = new URLSearchParams();
            body.append('action', 'add');
            body.append('productId', productId);
            body.append('variantId', variantId);
            body.append('quantity', quantity);

            fetch(contextPath + '/cart', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8',
                    'X-Requested-With': 'XMLHttpRequest'
                },
                body: body.toString()
            })
                    .then(r => r.json())
                    .then(data => {
                        if (data.success) {
                            var toastContainer = document.getElementById('toast-container');
                            var toastText = document.getElementById('toast-text');
                            if (toastContainer && toastText) {
                                toastText.textContent = data.message || 'Thêm vào giỏ hàng thành công!';
                                toastContainer.style.display = 'block';
                                setTimeout(function () {
                                    toastContainer.style.display = 'none';
                                }, 3000);
                            }
                            // Update cart badge
                            var badge = document.querySelector('.cart-badge');
                            if (badge && typeof data.cartCount !== 'undefined') {
                                badge.textContent = String(data.cartCount);
                            }
                        } else {
                            alert(data.message || 'Có lỗi xảy ra.');
                        }
                    })
                    .catch(e => {
                        console.error('Add to cart error:', e);
                        alert('Không thể thêm vào giỏ hàng. Vui lòng thử lại.');
                    });
        });
    }
})();


document.addEventListener("DOMContentLoaded", function() {
    var rawDiv = document.getElementById('raw-description');
    var formatDiv = document.getElementById('formatted-description');
    if (!rawDiv || !formatDiv) return;

    var rawText = rawDiv.innerHTML.trim();
    if (rawText === 'Không có thông tin chi tiết' || rawText === '') {
        formatDiv.innerHTML = rawText;
        return;
    }

    var html = '';
    // Tách chuỗi theo !
    var exclamParts = rawText.split('!');
    exclamParts.forEach(function(part, idx) {
        if (part.trim() === '') return;
        // Nếu là phần đầu tiên, tách tiếp theo $
        if (idx === 0) {
            var dollarParts = part.split('$');
            dollarParts.forEach(function(dpart) {
                if (dpart.trim() === '') return;
                html += '<div style="margin-left:20px;margin-top:16px;">' + dpart.trim() + '</div>';
            });
        } else {
            // Kiểm tra nếu phần này có ký hiệu $ (ví dụ: Hướng dẫn chọn size)
            if (part.includes('$')) {
                var dollarParts = part.split('$');
                dollarParts.forEach(function(dpart) {
                    if (dpart.trim() === '') return;
                    html += '<div style="margin-left:20px;margin-top:16px;">' + dpart.trim() + '</div>';
                });
            } else {
                // !: xuống 2 dòng và lùi vào 1 ô
                html += '<div style="margin-left:20px;margin-top:16px;">' + part.trim() + '</div>';
            }
        }
    });
    formatDiv.innerHTML = html;
});


