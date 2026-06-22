
            function openAddVariantModal(productId) {
                document.getElementById("modalProductId").value = productId;
                const modal = new bootstrap.Modal(document.getElementById("addVariantModal"));
                modal.show();
            }

            function openProductForm() {
                var contextPath = window.location.pathname.substring(0, window.location.pathname.indexOf('/', 1)) || '';
                window.location.href = contextPath + "/admin/products?mode=add";
            }

            (function () {
                function isPositiveNumber(value) {
                    var num = Number(value);
                    return Number.isFinite(num) && num > 0;
                }

                function getOrCreatePriceMessage(priceInput) {
                    var msg = priceInput.parentElement.querySelector('.price-invalid-message');
                    if (!msg) {
                        msg = document.createElement('div');
                        msg.className = 'price-invalid-message text-danger small mt-1';
                        msg.style.display = 'none';
                        priceInput.parentElement.appendChild(msg);
                    }
                    return msg;
                }

                function bindUpdatePriceGuard(actionValue, priceInputName) {
                    var forms = document.querySelectorAll('form');
                    forms.forEach(function (form) {
                        var actionInput = form.querySelector('input[name="action"]');
                        if (!actionInput || actionInput.value !== actionValue) {
                            return;
                        }

                        var priceInput = form.querySelector('input[name="' + priceInputName + '"]');
                        var submitBtn = form.querySelector('button[type="submit"]');
                        if (!priceInput || !submitBtn) {
                            return;
                        }
                        var messageEl = getOrCreatePriceMessage(priceInput);

                        // attach state to the form so name validation and price validation can share it
                        if (typeof form._validationState === 'undefined') {
                            form._validationState = {
                                nameValid: false,
                                priceValid: false
                            };
                        }

                        function updateSubmitState() {
                            var state = form._validationState;
                            var ok = state.nameValid && state.priceValid;
                            submitBtn.disabled = !ok;
                        }

                        function refreshPriceState() {
                            var ok = isPositiveNumber(priceInput.value);
                            form._validationState.priceValid = ok;
                            submitBtn.disabled = !(form._validationState.nameValid && ok);
                            priceInput.classList.toggle('is-invalid', !ok);
                            if (!ok) {
                                messageEl.textContent = 'Giá phải lớn hơn 0.';
                                messageEl.style.display = 'block';
                            } else {
                                messageEl.style.display = 'none';
                            }
                        }

                        priceInput.addEventListener('input', refreshPriceState);
                        priceInput.addEventListener('change', refreshPriceState);
                        refreshPriceState();
                    });
                }

                function bindUpdateNameGuard() {
                    var forms = document.querySelectorAll('form');
                    forms.forEach(function (form) {
                        var actionInput = form.querySelector('input[name="action"]');
                        if (!actionInput || actionInput.value !== 'updateProduct') {
                            return;
                        }

                        var nameInput = form.querySelector('input[name="productName"]');
                        var submitBtn = form.querySelector('button[type="submit"]');
                        var messageEl = form.querySelector('.name-validation-message');
                        var productIdInput = form.querySelector('input[name="productId"]');
                        var productId = productIdInput ? productIdInput.value : null;
                        var nameCheckTimeout = null;

                        if (!nameInput || !submitBtn || !messageEl) {
                            return;
                        }

                        // attach state if missing
                        if (typeof form._validationState === 'undefined') {
                            form._validationState = { nameValid: false, priceValid: false };
                        }

                        function updateSubmitState() {
                            var state = form._validationState;
                            submitBtn.disabled = !(state.nameValid && state.priceValid);
                        }

                        function setMessage(text, visible) {
                            messageEl.textContent = text;
                            messageEl.style.display = visible ? 'block' : 'none';
                        }

                        function checkName() {
                            var name = nameInput.value.trim();
                            if (!name) {
                                form._validationState.nameValid = false;
                                updateSubmitState();
                                setMessage('Vui lòng nhập tên sản phẩm.', true);
                                return;
                            }
                            var contextPath = window.location.pathname.substring(0, window.location.pathname.indexOf('/', 1)) || '';
                            fetch(contextPath + '/admin/products?action=checkProductName&productName=' + encodeURIComponent(name) + (productId ? '&productId=' + productId : ''))
                                .then(function (res) { return res.json(); })
                                .then(function (data) {
                                    if (data && data.exists) {
                                        form._validationState.nameValid = false;
                                        setMessage('❌ Tên sản phẩm đã tồn tại trong hệ thống.', true);
                                    } else {
                                        form._validationState.nameValid = true;
                                        setMessage('', false);
                                    }
                                    updateSubmitState();
                                })
                                .catch(function () {
                                    // Do not block submit if check fails
                                    form._validationState.nameValid = true;
                                    setMessage('', false);
                                    updateSubmitState();
                                });
                        }

                        nameInput.addEventListener('input', function () {
                            if (nameCheckTimeout) {
                                clearTimeout(nameCheckTimeout);
                            }
                            nameCheckTimeout = setTimeout(checkName, 250);
                        });
                        nameInput.addEventListener('change', function () {
                            if (nameCheckTimeout) {
                                clearTimeout(nameCheckTimeout);
                            }
                            checkName();
                        });

                        // run once on page load
                        checkName();
                    });
                }

                bindUpdatePriceGuard('updateProduct', 'basePrice');
                bindUpdatePriceGuard('updateVariant', 'price');
                bindUpdateNameGuard();
            })();
        