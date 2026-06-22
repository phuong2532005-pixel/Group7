
            (function () {
                // Get context path from current URL
                var contextPath = window.location.pathname.substring(0, window.location.pathname.indexOf('/', 1)) || '';
                
                var table = document.getElementById('cart-table');
                if (!table) {
                    return;
                }
                // Thêm sự kiện click cho sản phẩm
                table.addEventListener('click', function(event) {
                    var target = event.target;
                    var clickable = target.closest('.product-clickable');
                    if (clickable) {
                        var productId = clickable.getAttribute('data-product-id');
                        if (productId) {
                            window.location.href = contextPath + '/product?id=' + productId;
                        }
                    }
                });

                function formatNumber(value) {
                    var amount = Number(value);
                    if (!isFinite(amount)) {
                        amount = 0;
                    }
                    amount = Math.round(amount);
                    var digits = String(Math.abs(amount));
                    var parts = [];
                    while (digits.length > 3) {
                        parts.unshift(digits.slice(-3));
                        digits = digits.slice(0, -3);
                    }
                    if (digits.length) {
                        parts.unshift(digits);
                    }
                    var formatted = parts.join('.');
                    if (!formatted) {
                        formatted = '0';
                    }
                    return (amount < 0 ? '-' : '') + formatted + ' đ';
                }

                function resolveImageUrl(rawPath) {
                    if (!rawPath || String(rawPath).trim() === '') {
                        return '';
                    }
                    var imgPath = String(rawPath).trim();
                    if (imgPath.indexOf('.') === -1) {
                        imgPath = imgPath + '.jpg';
                    }
                    if (imgPath.indexOf('http://') === 0 || imgPath.indexOf('https://') === 0) {
                        return imgPath;
                    }
                    if (imgPath.indexOf('/') === 0) {
                        return contextPath + imgPath;
                    }
                    if (imgPath.indexOf('/') === -1) {
                        return contextPath + '/img/products/' + imgPath;
                    }
                    return contextPath + '/' + imgPath;
                }

                function getRowPrice(row) {
                    var price = parseInt(row.getAttribute('data-price'), 10);
                    return isNaN(price) ? 0 : price;
                }

                function getRowQuantity(row) {
                    var qtyInput = row.querySelector('.qty-input');
                    if (!qtyInput) {
                        return 0;
                    }
                    var quantity = parseInt(qtyInput.value, 10);
                    return isNaN(quantity) || quantity < 1 ? 0 : quantity;
                }

                function renderRowSubtotal(row, quantity) {
                    var cell = row.querySelector('.line-total');
                    if (!cell) {
                        return;
                    }
                    cell.textContent = formatNumber(getRowPrice(row) * quantity);
                }

                function getItemCheckboxes() {
                    return Array.prototype.slice.call(document.querySelectorAll('.item-checkbox'));
                }

                function getCartRows() {
                    return Array.prototype.slice.call(table.querySelectorAll('tr[data-variant]'));
                }

                function getEmptyState() {
                    return document.getElementById('cart-empty-state');
                }

                function getCartSummary() {
                    return document.querySelector('.cart-summary');
                }

                function syncCartVisibility() {
                    var hasItems = getCartRows().length > 0;
                    var emptyState = getEmptyState();
                    var summary = getCartSummary();
                    if (table) {
                        table.style.display = hasItems ? '' : 'none';
                    }
                    if (summary) {
                        summary.style.display = hasItems ? '' : 'none';
                    }
                    if (emptyState) {
                        emptyState.style.display = hasItems ? 'none' : '';
                    }
                    if (!hasItems) {
                        var totalElem = document.getElementById('cart-total');
                        if (totalElem) {
                            totalElem.textContent = formatNumber(0);
                        }
                        var selectAll = document.getElementById('select-all');
                        if (selectAll) {
                            selectAll.checked = false;
                        }
                    }
                }

                function updateSelectAllState() {
                    var selectAll = document.getElementById('select-all');
                    if (!selectAll) {
                        return;
                    }
                    var checkboxes = getItemCheckboxes();
                    var checked = checkboxes.filter(function (checkbox) {
                        return checkbox.checked;
                    }).length;
                    selectAll.checked = checkboxes.length > 0 && checked === checkboxes.length;
                }

                function findGroupHeader(row) {
                    var current = row;
                    while (current) {
                        if (current.classList && current.classList.contains('product-group-row')) {
                            return current;
                        }
                        current = current.previousElementSibling;
                    }
                    return null;
                }

                function cleanupEmptyGroupHeader(groupHeader) {
                    if (!groupHeader || !groupHeader.parentNode) {
                        return;
                    }
                    var current = groupHeader.nextElementSibling;
                    while (current) {
                        if (current.classList && current.classList.contains('product-group-row')) {
                            break;
                        }
                        if (current.hasAttribute && current.hasAttribute('data-variant')) {
                            return;
                        }
                        current = current.nextElementSibling;
                    }
                    groupHeader.remove();
                }

                function getVariantSelect(row) {
                    return row.querySelector('.variant-select');
                }

                function getSizeSelect(row) {
                    return row.querySelector('.size-select');
                }

                function getColorSelect(row) {
                    return row.querySelector('.color-select');
                }

                function getVariantOptions(row) {
                    var select = getVariantSelect(row);
                    if (!select) {
                        return [];
                    }
                    return Array.prototype.slice.call(select.options).map(function (option) {
                        return {
                            id: String(option.value),
                            sizeId: String(option.getAttribute('data-size-id') || ''),
                            sizeName: option.getAttribute('data-size-name') || '',
                            colorId: String(option.getAttribute('data-color-id') || ''),
                            colorName: option.getAttribute('data-color-name') || '',
                            imageUrl: option.getAttribute('data-image-url') || '',
                            price: parseInt(option.getAttribute('data-price') || '0', 10) || 0
                        };
                    });
                }

                function buildSelectOptions(select, items, selectedValue, placeholder) {
                    if (!select) {
                        return;
                    }
                    select.innerHTML = '';
                    if (!items.length) {
                        var emptyOption = document.createElement('option');
                        emptyOption.value = '';
                        emptyOption.textContent = placeholder;
                        select.appendChild(emptyOption);
                        return;
                    }
                    items.forEach(function (item) {
                        var option = document.createElement('option');
                        option.value = item.value;
                        option.textContent = item.label;
                        if (String(item.value) === String(selectedValue)) {
                            option.selected = true;
                        }
                        select.appendChild(option);
                    });
                }

                function buildVariantSelectors(row, selectedVariantId) {
                    var variants = getVariantOptions(row);
                    if (!variants.length) {
                        return;
                    }

                    var selectedVariant = null;
                    variants.forEach(function (variant) {
                        if (String(variant.id) === String(selectedVariantId)) {
                            selectedVariant = variant;
                        }
                    });
                    if (!selectedVariant) {
                        selectedVariant = variants[0];
                    }

                    var uniqueSizes = [];
                    var sizeSeen = {};
                    variants.forEach(function (variant) {
                        if (!sizeSeen[variant.sizeId]) {
                            sizeSeen[variant.sizeId] = true;
                            uniqueSizes.push({value: variant.sizeId, label: variant.sizeName});
                        }
                    });

                    var selectedSizeId = selectedVariant.sizeId;
                    buildSelectOptions(getSizeSelect(row), uniqueSizes, selectedSizeId, 'Chọn size');

                    var availableColors = [];
                    var colorSeen = {};
                    variants.forEach(function (variant) {
                        if (String(variant.sizeId) === String(selectedSizeId) && !colorSeen[variant.colorId]) {
                            colorSeen[variant.colorId] = true;
                            availableColors.push({value: variant.colorId, label: variant.colorName});
                        }
                    });

                    var selectedColorId = selectedVariant.colorId;
                    var colorExists = availableColors.some(function (item) {
                        return String(item.value) === String(selectedColorId);
                    });
                    if (!colorExists && availableColors.length) {
                        selectedColorId = availableColors[0].value;
                    }
                    buildSelectOptions(getColorSelect(row), availableColors, selectedColorId, 'Chọn màu');

                    var hiddenSelect = getVariantSelect(row);
                    if (hiddenSelect) {
                        hiddenSelect.value = String(selectedVariant.id);
                    }
                }

                function syncColorOptionsForSize(row, selectedSizeId, preferredColorId) {
                    var variants = getVariantOptions(row);
                    var availableColors = [];
                    var colorSeen = {};
                    variants.forEach(function (variant) {
                        if (String(variant.sizeId) === String(selectedSizeId) && !colorSeen[variant.colorId]) {
                            colorSeen[variant.colorId] = true;
                            availableColors.push({value: variant.colorId, label: variant.colorName});
                        }
                    });

                    var selectedColorId = preferredColorId;
                    var exists = availableColors.some(function (item) {
                        return String(item.value) === String(selectedColorId);
                    });
                    if (!exists && availableColors.length) {
                        selectedColorId = availableColors[0].value;
                    }
                    buildSelectOptions(getColorSelect(row), availableColors, selectedColorId, 'Chọn màu');
                    return getVariantIdFromSelectors(row);
                }

                function getVariantIdFromSelectors(row) {
                    var sizeSelect = getSizeSelect(row);
                    var colorSelect = getColorSelect(row);
                    if (!sizeSelect || !colorSelect) {
                        return null;
                    }
                    var selectedSizeId = sizeSelect.value;
                    var selectedColorId = colorSelect.value;
                    var variants = getVariantOptions(row);
                    for (var index = 0; index < variants.length; index++) {
                        var variant = variants[index];
                        if (String(variant.sizeId) === String(selectedSizeId)
                                && String(variant.colorId) === String(selectedColorId)) {
                            return variant.id;
                        }
                    }
                    return null;
                }

                function updateRowFromVariantData(row, data) {
                    row.setAttribute('data-variant', data.newVariantId);
                    row.setAttribute('data-product-id', data.productId);
                    row.setAttribute('data-price', data.price);

                    var qtyInput = row.querySelector('.qty-input');
                    if (qtyInput) {
                        qtyInput.value = data.quantity;
                    }

                    var priceCell = row.children[3];
                    if (priceCell) {
                        priceCell.textContent = formatNumber(data.price);
                    }

                    var subtotalCell = row.querySelector('.line-total');
                    if (subtotalCell) {
                        subtotalCell.textContent = formatNumber(data.itemSubtotal);
                    }

                    var checkbox = row.querySelector('.item-checkbox');
                    if (checkbox) {
                        checkbox.setAttribute('data-variant', data.newVariantId);
                    }

                    var select = getVariantSelect(row);
                    if (select) {
                        Array.prototype.slice.call(select.options).forEach(function (option) {
                            option.selected = String(option.value) === String(data.newVariantId);
                        });
                    }

                    buildVariantSelectors(row, data.newVariantId);

                    var image = row.querySelector('.cart-thumb img');
                    if (image && data.imageUrl) {
                        image.src = resolveImageUrl(data.imageUrl);
                        image.alt = data.productName || image.alt;
                    }
                }

                function updateRow(row, quantity) {
                    var variantId = row.getAttribute('data-variant');
                    var body = 'action=update&variantId=' + encodeURIComponent(variantId)
                    + '&quantity=' + encodeURIComponent(quantity        );
                    fetch(contextPath + '/cart', {
                        method: 'POST',
                        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                        body: body
                    })
                            .then(function (res) {
                                return res.json();
                            })
                            .then(function (data) {
                                var ok = typeof data.ok !== 'undefined' ? data.ok : data.success;
                                if (!ok) {
                                    return;
                                }
                                var badge = document.querySelector('.cart-badge');
                                if (badge && typeof data.cartCount !== 'undefined') {
                                    badge.textContent = String(data.cartCount);
                                }
                                if (data.itemSubtotal === 0) {
                                    var groupHeader = findGroupHeader(row);
                                    row.remove();
                                    cleanupEmptyGroupHeader(groupHeader);
                                } else {
                                    var cell = row.querySelector('.line-total');
                                    if (cell) {
                                        cell.textContent = formatNumber(data.itemSubtotal);
                                    }
                                }
                                recalcTotal();
                                updateSelectAllState();
                                syncCartVisibility();
                            });
                }

                // debounce timer map by variant
                var qtyTimers = {};
                table.addEventListener('input', function (event) {
                    if (event.target.classList.contains('qty-input')) {
                        var row = event.target.closest('tr');
                        if (!row) return;
                        var qtyInput = event.target;
                        var variantId = row.getAttribute('data-variant');
                        // schedule update after user stops typing for 400ms
                        clearTimeout(qtyTimers[variantId]);
                        qtyTimers[variantId] = setTimeout(function() {
                            var quantity = parseInt(qtyInput.value, 10);
                            if (!quantity || quantity < 1) {
                                quantity = 1;
                                qtyInput.value = 1;
                            }
                            renderRowSubtotal(row, quantity);
                            recalcTotal();
                            updateRow(row, quantity);
                        }, 400);
                    }
                    if (event.target.classList.contains('item-checkbox')) {
                        recalcTotal();
                    }
                });

                // also listen to change event to catch blur/chosen spinner
                table.addEventListener('change', function (event) {
                    if (event.target.classList.contains('qty-input')) {
                        var row = event.target.closest('tr');
                        if (!row) {
                            return;
                        }
                        var variantId = row.getAttribute('data-variant');
                        clearTimeout(qtyTimers[variantId]);
                        var quantity = parseInt(event.target.value, 10);
                        if (!quantity || quantity < 1) {
                            quantity = 1;
                            event.target.value = 1;
                        }
                        renderRowSubtotal(row, quantity);
                        recalcTotal();
                        updateRow(row, quantity);
                    }
                    if (event.target.classList.contains('size-select')) {
                        var sizeRow = event.target.closest('tr');
                        if (!sizeRow) {
                            return;
                        }
                        var preferredColorId = '';
                        var currentColorSelect = getColorSelect(sizeRow);
                        if (currentColorSelect) {
                            preferredColorId = currentColorSelect.value;
                        }
                        syncColorOptionsForSize(sizeRow, event.target.value, preferredColorId);
                    }
                    if (event.target.classList.contains('size-select') || event.target.classList.contains('color-select')) {
                        var row = event.target.closest('tr');
                        if (!row) {
                            return;
                        }
                        var oldVariantId = row.getAttribute('data-variant');
                        var newVariantId = getVariantIdFromSelectors(row);
                        if (!oldVariantId || !newVariantId || oldVariantId === newVariantId) {
                            return;
                        }
                        var wasChecked = false;
                        var currentCheckbox = row.querySelector('.item-checkbox');
                        if (currentCheckbox) {
                            wasChecked = currentCheckbox.checked;
                        }

                        fetch(contextPath + '/cart', {
                            method: 'POST',
                            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                            body: 'action=changeVariant&variantId=' + encodeURIComponent(oldVariantId) + '&newVariantId=' + encodeURIComponent(newVariantId)
                        })
                                .then(function (res) {
                                    return res.json();
                                })
                                .then(function (data) {
                                    var ok = typeof data.ok !== 'undefined' ? data.ok : data.success;
                                    if (!ok) {
                                        buildVariantSelectors(row, oldVariantId);
                                        return;
                                    }

                                    var targetRow = table.querySelector('tr[data-variant="' + data.newVariantId + '"]');
                                    if (targetRow && targetRow !== row) {
                                        var sourceGroupHeader = findGroupHeader(row);
                                        var targetCheckbox = targetRow.querySelector('.item-checkbox');
                                        var shouldCheck = wasChecked || (targetCheckbox && targetCheckbox.checked);
                                        updateRowFromVariantData(targetRow, data);
                                        if (targetCheckbox) {
                                            targetCheckbox.checked = !!shouldCheck;
                                        }
                                        row.remove();
                                        cleanupEmptyGroupHeader(sourceGroupHeader);
                                    } else {
                                        updateRowFromVariantData(row, data);
                                        if (currentCheckbox) {
                                            currentCheckbox.checked = wasChecked;
                                        }
                                    }

                                    var badge = document.querySelector('.cart-badge');
                                    if (badge && typeof data.cartCount !== 'undefined') {
                                        badge.textContent = String(data.cartCount);
                                    }

                                    recalcTotal();
                                    updateSelectAllState();
                                    syncCartVisibility();
                                });
                    }
                    if (event.target.classList.contains('item-checkbox')) {
                        recalcTotal();
                        updateSelectAllState();
                    }
                });

                table.addEventListener('click', function (event) {
                    if (!event.target.classList.contains('remove-btn')) {
                        return;
                    }
                    var row = event.target.closest('tr');
                    if (!row) {
                        return;
                    }
                    var variantId = row.getAttribute('data-variant');
            var body = 'action=remove&variantId=' + encodeURIComponent(variantId        );
                    fetch(contextPath + '/cart', {
                        method: 'POST',
                        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                        body: body
                    })
                            .then(function (res) {
                                return res.json();
                            })
                            .then(function (data) {
                                var ok = typeof data.ok !== 'undefined' ? data.ok : data.success;
                                if (!ok) {
                                    return;
                                }
                                var badge = document.querySelector('.cart-badge');
                                if (badge && typeof data.cartCount !== 'undefined') {
                                    badge.textContent = String(data.cartCount);
                                }
                                var groupHeader = findGroupHeader(row);
                                row.remove();
                                cleanupEmptyGroupHeader(groupHeader);
                                recalcTotal();
                                updateSelectAllState();
                                syncCartVisibility();
                            });
                });

                // helper: recalc total from checked rows
                function recalcTotal() {
                    var total = 0;
                    document.querySelectorAll('.item-checkbox:checked').forEach(function(cb) {
                        var row = cb.closest('tr');
                        if (!row) return;
                        total += getRowPrice(row) * getRowQuantity(row);
                    });
                    var totalElem = document.getElementById('cart-total');
                    if (totalElem) {
                        totalElem.textContent = formatNumber(total);
                    }
                }

                // Handle "Select All" checkbox
                var selectAllCheckbox = document.getElementById('select-all');
                
                if (selectAllCheckbox) {
                    selectAllCheckbox.addEventListener('change', function() {
                        getItemCheckboxes().forEach(function(checkbox) {
                            checkbox.checked = selectAllCheckbox.checked;
                        });
                        recalcTotal();
                    });
                }

                // Handle checkout button with selected items
                var checkoutBtn = document.getElementById('checkout-btn');
                var removeSelectedBtn = document.getElementById('remove-selected-btn');

                // initial total should be 0
                Array.prototype.slice.call(table.querySelectorAll('tr[data-variant]')).forEach(function (row) {
                    buildVariantSelectors(row, row.getAttribute('data-variant'));
                });
                recalcTotal();
                syncCartVisibility();
                if (checkoutBtn) {
                    checkoutBtn.addEventListener('click', function() {
                        var selectedVariants = [];
                        getItemCheckboxes().forEach(function(checkbox) {
                            if (checkbox.checked) {
                                selectedVariants.push(checkbox.getAttribute('data-variant'));
                            }
                        });

                        if (selectedVariants.length === 0) {
                            alert('Vui lòng chọn ít nhất một sản phẩm để thanh toán.');
                            return;
                        }

                        // Get context path from current location
                        var contextPath = window.location.pathname.substring(0, window.location.pathname.indexOf('/', 1));
                        
                        // Create form and submit with selected variants
                        var form = document.createElement('form');
                        form.method = 'POST';
                        form.action = contextPath + '/checkout';
                        form.style.display = 'none';

                        var input = document.createElement('input');
                        input.type = 'hidden';
                        input.name = 'selectedVariants';
                        input.value = selectedVariants.join(',');

                        form.appendChild(input);
                        document.body.appendChild(form);
                        form.submit();
                    });
                }

                if (removeSelectedBtn) {
                    removeSelectedBtn.addEventListener('click', function () {
                        var selectedVariants = [];
                        getItemCheckboxes().forEach(function (checkbox) {
                            if (checkbox.checked) {
                                selectedVariants.push(checkbox.getAttribute('data-variant'));
                            }
                        });

                        if (!selectedVariants.length) {
                            alert('Vui lòng chọn ít nhất một sản phẩm để xóa.');
                            return;
                        }

                        fetch(contextPath + '/cart', {
                            method: 'POST',
                            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                            body: 'action=removeSelected&variantIds=' + encodeURIComponent(selectedVariants.join(','))
                        })
                                .then(function (res) {
                                    return res.json();
                                })
                                .then(function (data) {
                                    var ok = typeof data.ok !== 'undefined' ? data.ok : data.success;
                                    if (!ok) {
                                        return;
                                    }

                                    selectedVariants.forEach(function (variantId) {
                                        var row = table.querySelector('tr[data-variant="' + variantId + '"]');
                                        if (!row) {
                                            return;
                                        }
                                        var groupHeader = findGroupHeader(row);
                                        row.remove();
                                        cleanupEmptyGroupHeader(groupHeader);
                                    });

                                    var badge = document.querySelector('.cart-badge');
                                    if (badge && typeof data.cartCount !== 'undefined') {
                                        badge.textContent = String(data.cartCount);
                                    }

                                    recalcTotal();
                                    updateSelectAllState();
                                    syncCartVisibility();
                                });
                    });
                }
            })();
            
            
                        // reset select-all and totals on load
            document.addEventListener('DOMContentLoaded', function() {
                const selectAll = document.getElementById('select-all');
                if (selectAll) {
                    selectAll.checked = false;
                }
                // total will be recalculated by cart.js init
            });
        