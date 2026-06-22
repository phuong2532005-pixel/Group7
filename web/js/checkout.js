
                (function () {
                    var ctx = window.location.pathname.substring(0, window.location.pathname.indexOf('/', 1)) || '';

                    var addressEl = document.getElementById('selectedAddressId');
                    var shippingStreetEl = document.getElementById('shippingStreet');
                    var shippingDistrictEl = document.getElementById('shippingDistrict');
                    var shippingCityEl = document.getElementById('shippingCity');
                    var shippingWardEl = document.getElementById('shippingWard');
                    var suggestedBranchEl = document.getElementById('suggestedBranch');
                    var selectedAddressTextEl = document.getElementById('selectedAddressText');
                    var selectedAddressDefaultEl = document.getElementById('selectedAddressDefault');
                    var toggleAddressPickerBtn = document.getElementById('toggleAddressPickerBtn');
                    var addressModalEl = document.getElementById('addressModal');
                    var closeAddressModalBtn = document.getElementById('closeAddressModalBtn');
                    var applyAddressBtn = document.getElementById('applyAddressBtn');
                    var toggleNewAddressBtn = document.getElementById('toggleNewAddressBtn');
                    var useNewAddressEl = document.getElementById('useNewAddress');
                    var newAddressWrapEl = document.getElementById('newAddressWrap');
                    var defaultCity = '${defaultShippingCity}';
                    var defaultDistrict = '${defaultShippingDistrict}';

                    function trimOrEmpty(v) {
                        return (v || '').toString().trim();
                    }

                    function resetSelect(selectEl, placeholder) {
                        if (!selectEl) return;
                        selectEl.innerHTML = '';
                        var first = document.createElement('option');
                        first.value = '';
                        first.textContent = placeholder;
                        selectEl.appendChild(first);
                        selectEl.value = '';
                    }

                    function fillSelect(selectEl, items, placeholder, selectedName) {
                        resetSelect(selectEl, placeholder);
                        var target = trimOrEmpty(selectedName).toLowerCase();
                        (items || []).forEach(function (it) {
                            var opt = document.createElement('option');
                            opt.value = it.name;
                            opt.dataset.code = it.code;
                            opt.textContent = it.name;
                            if (target && it.name && it.name.toLowerCase() === target) {
                                opt.selected = true;
                            }
                            selectEl.appendChild(opt);
                        });
                    }

                    function selectedCode(selectEl) {
                        if (!selectEl || selectEl.selectedIndex < 0) return '';
                        var opt = selectEl.options[selectEl.selectedIndex];
                        return opt ? (opt.dataset.code || '') : '';
                    }

                    function getCheckedAddressRadio() {
                        return document.querySelector('.address-choice-radio:checked');
                    }

                    function getAddressDataById(addressId) {
                        if (!addressId) {
                            return null;
                        }
                        var radios = document.querySelectorAll('.address-choice-radio');
                        for (var i = 0; i < radios.length; i++) {
                            if (String(radios[i].value) === String(addressId)) {
                                return radios[i];
                            }
                        }
                        return null;
                    }

                    function isManualAddressMode() {
                        return useNewAddressEl && useNewAddressEl.value === '1';
                    }

                    function setManualAddressMode(enable) {
                        if (!useNewAddressEl) {
                            return;
                        }
                        useNewAddressEl.value = enable ? '1' : '0';

                        if (newAddressWrapEl) {
                            newAddressWrapEl.style.display = enable ? 'block' : 'none';
                        }
                        if (toggleNewAddressBtn) {
                            toggleNewAddressBtn.style.display = enable ? 'none' : 'inline-block';
                        }
                        if (shippingStreetEl) {
                            shippingStreetEl.required = enable;
                        }
                        if (shippingCityEl) {
                            shippingCityEl.required = enable;
                        }
                        if (shippingDistrictEl) {
                            shippingDistrictEl.required = enable;
                        }
                        if (shippingWardEl) {
                            shippingWardEl.required = enable;
                        }
                        if (addressEl) {
                            addressEl.required = !enable;
                        }
                    }

                    function openAddressModal() {
                        if (!addressModalEl) return;
                        if (addressEl && addressEl.value) {
                            var radios = document.querySelectorAll('.address-choice-radio');
                            radios.forEach(function (radio) {
                                radio.checked = radio.value === addressEl.value;
                            });
                        }
                        addressModalEl.style.display = 'flex';
                    }

                    function closeAddressModal() {
                        if (!addressModalEl) return;
                        addressModalEl.style.display = 'none';
                    }

                    function applySelectedAddressToSummary() {
                        var source = null;
                        if (addressEl && addressEl.value) {
                            source = getAddressDataById(addressEl.value);
                        }
                        if (!source) {
                            source = getCheckedAddressRadio();
                            if (source && addressEl) {
                                addressEl.value = source.value;
                            }
                        }
                        if (!source) {
                            clearRequestedShop();
                            return;
                        }

                        var city = trimOrEmpty(source.getAttribute('data-city'));
                        var district = trimOrEmpty(source.getAttribute('data-district'));
                        var street = trimOrEmpty(source.getAttribute('data-street'));

                        if (selectedAddressTextEl) {
                            selectedAddressTextEl.textContent = street + ', ' + district + ', ' + city;
                        }
                        if (selectedAddressDefaultEl) {
                            var isDefault = source.getAttribute('data-is-default') === '1';
                            selectedAddressDefaultEl.style.display = isDefault ? 'inline-block' : 'none';
                        }

                        suggestBranchByValues(city, district, street);
                    }

                    function clearRequestedShop() {
                        if (suggestedBranchEl) {
                            suggestedBranchEl.value = 'Kho trung tâm';
                        }
                    }

                    function suggestBranchByValues(city, district, street) {
                        var safeCity = trimOrEmpty(city);
                        var safeDistrict = trimOrEmpty(district);
                        var safeStreet = trimOrEmpty(street);
                        if (!safeCity || !safeDistrict || !safeStreet) {
                            clearRequestedShop();
                            return;
                        }
                        if (suggestedBranchEl) {
                            suggestedBranchEl.value = 'Kho trung tâm';
                        }
                    }

                    function suggestBranchByAddress() {
                        applySelectedAddressToSummary();
                    }

                    function loadProvinces() {
                        if (!shippingCityEl) return;
                        fetch('https://provinces.open-api.vn/api/p/', {
                            method: 'GET',
                            headers: { 'Accept': 'application/json' }
                        }).then(function (r) {
                            if (!r.ok) throw new Error('Cannot load provinces');
                            return r.json();
                        }).then(function (data) {
                            fillSelect(shippingCityEl, data, '-- Chọn tỉnh/thành phố --', defaultCity);
                            if (shippingCityEl.value) {
                                loadDistricts(selectedCode(shippingCityEl), defaultDistrict);
                            }
                        }).catch(function () {
                            resetSelect(shippingCityEl, '-- Không thể tải tỉnh/thành phố --');
                        });
                    }

                    function loadDistricts(provinceCode, selectedDistrictName) {
                        if (!shippingDistrictEl || !shippingWardEl) return;
                        resetSelect(shippingDistrictEl, '-- Chọn quận/huyện --');
                        shippingDistrictEl.disabled = true;
                        resetSelect(shippingWardEl, '-- Chọn phường/xã --');
                        shippingWardEl.disabled = true;

                        if (!provinceCode) return;

                        fetch('https://provinces.open-api.vn/api/p/' + encodeURIComponent(provinceCode) + '?depth=2', {
                            method: 'GET',
                            headers: { 'Accept': 'application/json' }
                        }).then(function (r) {
                            if (!r.ok) throw new Error('Cannot load districts');
                            return r.json();
                        }).then(function (data) {
                            fillSelect(shippingDistrictEl, (data && data.districts) ? data.districts : [], '-- Chọn quận/huyện --', selectedDistrictName);
                            shippingDistrictEl.disabled = false;
                        }).catch(function () {
                            resetSelect(shippingDistrictEl, '-- Không thể tải quận/huyện --');
                            shippingDistrictEl.disabled = true;
                        });
                    }

                    function loadWards(districtCode) {
                        if (!shippingWardEl) return;
                        resetSelect(shippingWardEl, '-- Chọn phường/xã --');
                        shippingWardEl.disabled = true;
                        if (!districtCode) return;

                        fetch('https://provinces.open-api.vn/api/d/' + encodeURIComponent(districtCode) + '?depth=2', {
                            method: 'GET',
                            headers: { 'Accept': 'application/json' }
                        }).then(function (r) {
                            if (!r.ok) throw new Error('Cannot load wards');
                            return r.json();
                        }).then(function (data) {
                            fillSelect(shippingWardEl, (data && data.wards) ? data.wards : [], '-- Chọn phường/xã --', '');
                            shippingWardEl.disabled = false;
                        }).catch(function () {
                            resetSelect(shippingWardEl, '-- Không thể tải phường/xã --');
                            shippingWardEl.disabled = true;
                        });
                    }

                    if (addressEl) {
                        if (!addressEl.value) {
                            var checkedOnLoad = getCheckedAddressRadio();
                            if (checkedOnLoad) {
                                addressEl.value = checkedOnLoad.value;
                            }
                        }
                        if (addressEl.value) {
                            suggestBranchByAddress();
                        }
                    }

                    if (toggleAddressPickerBtn && addressModalEl) {
                        toggleAddressPickerBtn.addEventListener('click', function () {
                            setManualAddressMode(false);
                            openAddressModal();
                        });
                    }

                    if (closeAddressModalBtn) {
                        closeAddressModalBtn.addEventListener('click', closeAddressModal);
                    }

                    if (addressModalEl) {
                        addressModalEl.addEventListener('click', function (e) {
                            if (e.target === addressModalEl) {
                                closeAddressModal();
                            }
                        });
                    }

                    if (toggleNewAddressBtn) {
                        toggleNewAddressBtn.addEventListener('click', function () {
                            var enable = !isManualAddressMode();
                            setManualAddressMode(enable);
                            if (enable && shippingStreetEl) {
                                shippingStreetEl.focus();
                            }
                        });
                    }

                    if (applyAddressBtn) {
                        applyAddressBtn.addEventListener('click', function () {
                            if (isManualAddressMode()) {
                                if (!shippingStreetEl || !shippingCityEl || !shippingDistrictEl || !shippingWardEl) {
                                    return;
                                }
                                if (!trimOrEmpty(shippingStreetEl.value)
                                        || !trimOrEmpty(shippingCityEl.value)
                                        || !trimOrEmpty(shippingDistrictEl.value)
                                        || !trimOrEmpty(shippingWardEl.value)) {
                                    alert('Vui lòng nhập đầy đủ địa chỉ mới.');
                                    return;
                                }

                                var ward = trimOrEmpty(shippingWardEl.value);
                                var street = trimOrEmpty(shippingStreetEl.value);
                                if (ward && street && street.toLowerCase().indexOf(ward.toLowerCase()) === -1) {
                                    shippingStreetEl.value = street + ', ' + ward;
                                }

                                var defaultCheckbox = document.getElementById('newAddressIsDefault');
                                var body = new URLSearchParams();
                                body.append('action', 'saveAddress');
                                body.append('shippingStreet', shippingStreetEl.value);
                                body.append('shippingDistrict', shippingDistrictEl.value);
                                body.append('shippingCity', shippingCityEl.value);
                                body.append('newAddressIsDefault', defaultCheckbox && defaultCheckbox.checked ? '1' : '0');

                                fetch(ctx + '/checkout', {
                                    method: 'POST',
                                    headers: {
                                        'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8',
                                        'Accept': 'application/json'
                                    },
                                    body: body.toString()
                                }).then(function (r) {
                                    if (!r.ok) throw new Error('Save address failed');
                                    return r.json();
                                }).then(function (data) {
                                    if (!data || !data.success) {
                                        alert((data && data.message) ? data.message : 'Không thể lưu địa chỉ mới.');
                                        return;
                                    }
                                    if (addressEl) {
                                        addressEl.value = String(data.addressId);
                                    }

                                    var list = document.getElementById('addressModalList');
                                    if (list) {
                                        var oldRadios = list.querySelectorAll('.address-choice-radio');
                                        oldRadios.forEach(function (oldRadio) {
                                            oldRadio.checked = false;
                                        });

                                        var item = document.createElement('div');
                                        item.className = 'checkout-address-item';
                                        var checkedFlag = data.isDefault ? '<span class="checkout-address-badge">Mặc định</span>' : '';
                                        item.innerHTML = ''
                                                + '<div class="checkout-address-item-radio">'
                                                + '  <input type="radio" name="addressChoice" class="address-choice-radio" checked '
                                                + '         value="' + String(data.addressId) + '" '
                                                + '         data-city="' + (data.city || '') + '" '
                                                + '         data-district="' + (data.district || '') + '" '
                                                + '         data-street="' + (data.street || '') + '" '
                                                + '         data-is-default="' + (data.isDefault ? '1' : '0') + '">'
                                                + '</div>'
                                                + '<div class="checkout-address-item-info">'
                                                + '  <div class="checkout-address-item-top">'
                                                + '    <span class="checkout-address-item-name">${defaultShippingName}</span>'
                                                + '    <span class="checkout-address-item-phone">${defaultShippingPhone}</span>'
                                                +      checkedFlag
                                                + '  </div>'
                                                + '  <div class="checkout-address-item-line">' + (data.street || '') + '</div>'
                                                + '  <div class="checkout-address-item-line">' + (data.district || '') + ', ' + (data.city || '') + '</div>'
                                                + '</div>';

                                        var newAddressBox = document.getElementById('newAddressWrap');
                                        if (newAddressBox) {
                                            list.insertBefore(item, newAddressBox);
                                        } else {
                                            list.appendChild(item);
                                        }
                                    }

                                    setManualAddressMode(false);
                                    suggestBranchByAddress();
                                    closeAddressModal();
                                }).catch(function () {
                                    alert('Không thể lưu địa chỉ mới. Vui lòng thử lại.');
                                });
                                return;
                            }

                            var checked = getCheckedAddressRadio();
                            if (!checked || !addressEl) {
                                return;
                            }
                            addressEl.value = checked.value;
                            setManualAddressMode(false);
                            suggestBranchByAddress();
                            closeAddressModal();
                        });
                    }

                    if (shippingStreetEl && shippingDistrictEl && shippingCityEl) {
                        function suggestBranchByManualAddress() {
                            suggestBranchByValues(shippingCityEl.value, shippingDistrictEl.value, shippingStreetEl.value);
                        }

                        loadProvinces();

                        shippingCityEl.addEventListener('change', function () {
                            defaultDistrict = '';
                            loadDistricts(selectedCode(shippingCityEl), '');
                            suggestBranchByManualAddress();
                        });

                        shippingDistrictEl.addEventListener('change', function () {
                            loadWards(selectedCode(shippingDistrictEl));
                            suggestBranchByManualAddress();
                        });

                        if (shippingWardEl) {
                            shippingWardEl.addEventListener('change', suggestBranchByManualAddress);
                        }

                        shippingStreetEl.addEventListener('input', suggestBranchByManualAddress);

                        var checkoutForm = document.getElementById('checkoutForm');
                        if (checkoutForm) {
                            checkoutForm.addEventListener('submit', function (e) {
                                var needManualValidation = !addressEl || isManualAddressMode();
                                if (needManualValidation) {
                                    if (!trimOrEmpty(shippingCityEl.value) || !trimOrEmpty(shippingDistrictEl.value) || !shippingWardEl || !trimOrEmpty(shippingWardEl.value)) {
                                        e.preventDefault();
                                        alert('Vui lòng chọn đầy đủ Tỉnh/Thành phố, Quận/Huyện và Phường/Xã.');
                                        return;
                                    }

                                    var ward = trimOrEmpty(shippingWardEl.value);
                                    var street = trimOrEmpty(shippingStreetEl.value);
                                    if (ward && street && street.toLowerCase().indexOf(ward.toLowerCase()) === -1) {
                                        shippingStreetEl.value = street + ', ' + ward;
                                    }
                                }
                            });
                        }

                        if (trimOrEmpty(shippingStreetEl.value) && trimOrEmpty(shippingDistrictEl.value) && trimOrEmpty(shippingCityEl.value) && isManualAddressMode()) {
                            suggestBranchByManualAddress();
                        }
                    }

                    if (useNewAddressEl && useNewAddressEl.value === '1') {
                        setManualAddressMode(true);
                    }
                })();
            