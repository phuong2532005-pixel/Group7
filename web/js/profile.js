
    document.addEventListener("DOMContentLoaded", function () {
        const navButtons = document.querySelectorAll('.profile-nav-btn');
        const sections = document.querySelectorAll('.profile-section');
        const toggleAddAddressBtn = document.getElementById('toggleAddAddressBtn');
        const addAddressFormWrap = document.getElementById('addAddressFormWrap');
        const pass = document.getElementById('newPassword');
        const confirm = document.getElementById('confirmPassword');
        const btn = document.getElementById('submitPassBtn');
        
        // Lấy role từ data attribute
        const userRole = parseInt(document.body.getAttribute('data-user-role') || '0');
        const sectionFromRequest = document.body.getAttribute('data-initial-section') || '';

        function activateSection(sectionKey) {
            navButtons.forEach(function (button) {
                button.classList.toggle('active', button.getAttribute('data-section') === sectionKey);
            });
            sections.forEach(function (section) {
                section.classList.toggle('active', section.id === ('section-' + sectionKey));
            });
        }

        navButtons.forEach(function (button) {
            button.addEventListener('click', function () {
                const directUrl = button.getAttribute('data-direct-url');
                if (directUrl) {
                    window.location.href = directUrl;
                    return;
                }
                activateSection(button.getAttribute('data-section'));
            });
        });

        const urlParams = new URLSearchParams(window.location.search);
        const sectionFromUrl = urlParams.get('section');
        const availableSections = userRole === 3
            ? ['info', 'address', 'password', 'orders']
            : ['info', 'password'];
        
        // Xác định section mặc định dựa trên role
        let defaultSection = 'info';  // Mặc định cho Customer
        
        const initialSection = availableSections.indexOf(sectionFromRequest) >= 0
            ? sectionFromRequest
            : (availableSections.indexOf(sectionFromUrl) >= 0 ? sectionFromUrl : defaultSection);
        activateSection(initialSection);

        if (toggleAddAddressBtn && addAddressFormWrap) {
            toggleAddAddressBtn.addEventListener('click', function () {
                const isHidden = addAddressFormWrap.style.display === 'none';
                addAddressFormWrap.style.display = isHidden ? '' : 'none';
            });

            if (initialSection === 'address' && urlParams.get('openAddAddress') === '1') {
                addAddressFormWrap.style.display = '';
            }
        }

        document.querySelectorAll('.btn-edit-address').forEach(function(editBtn) {
            editBtn.addEventListener('click', function () {
                const id = editBtn.getAttribute('data-id');
                const wrap = document.getElementById('editAddressWrap_' + id);
                if (!wrap) return;
                const isHidden = wrap.style.display === 'none';
                wrap.style.display = isHidden ? '' : 'none';
            });
        });

        const addAddressForm = document.getElementById('addAddressForm');
        const addStreet = document.getElementById('addStreet');
        const addCity = document.getElementById('addCity');
        const addDistrict = document.getElementById('addDistrict');
        const addWard = document.getElementById('addWard');

        function resetSelect(selectEl, placeholder) {
            if (!selectEl) return;
            selectEl.innerHTML = '';
            const first = document.createElement('option');
            first.value = '';
            first.textContent = placeholder;
            selectEl.appendChild(first);
            selectEl.value = '';
        }

        function fillSelect(selectEl, items, placeholder) {
            resetSelect(selectEl, placeholder);
            (items || []).forEach(function (it) {
                const opt = document.createElement('option');
                opt.value = it.name;
                opt.dataset.code = it.code;
                opt.textContent = it.name;
                selectEl.appendChild(opt);
            });
        }

        function selectedCode(selectEl) {
            if (!selectEl || selectEl.selectedIndex < 0) return '';
            const opt = selectEl.options[selectEl.selectedIndex];
            return opt ? (opt.dataset.code || '') : '';
        }

        function loadProvinces() {
            if (!addCity) return;
            fetch('https://provinces.open-api.vn/api/p/', {
                method: 'GET',
                headers: { 'Accept': 'application/json' }
            }).then(function (r) {
                if (!r.ok) throw new Error('Cannot load provinces');
                return r.json();
            }).then(function (data) {
                fillSelect(addCity, data, '-- Chọn tỉnh/thành phố --');
            }).catch(function () {
                resetSelect(addCity, '-- Không thể tải tỉnh/thành phố --');
            });
        }

        function loadDistricts(provinceCode) {
            if (!addDistrict || !addWard) return;
            resetSelect(addDistrict, '-- Chọn quận/huyện --');
            addDistrict.disabled = true;
            resetSelect(addWard, '-- Chọn phường/xã --');
            addWard.disabled = true;

            if (!provinceCode) return;
            fetch('https://provinces.open-api.vn/api/p/' + encodeURIComponent(provinceCode) + '?depth=2', {
                method: 'GET',
                headers: { 'Accept': 'application/json' }
            }).then(function (r) {
                if (!r.ok) throw new Error('Cannot load districts');
                return r.json();
            }).then(function (data) {
                fillSelect(addDistrict, (data && data.districts) ? data.districts : [], '-- Chọn quận/huyện --');
                addDistrict.disabled = false;
            }).catch(function () {
                resetSelect(addDistrict, '-- Không thể tải quận/huyện --');
                addDistrict.disabled = true;
            });
        }

        function loadWards(districtCode) {
            if (!addWard) return;
            resetSelect(addWard, '-- Chọn phường/xã --');
            addWard.disabled = true;
            if (!districtCode) return;
            fetch('https://provinces.open-api.vn/api/d/' + encodeURIComponent(districtCode) + '?depth=2', {
                method: 'GET',
                headers: { 'Accept': 'application/json' }
            }).then(function (r) {
                if (!r.ok) throw new Error('Cannot load wards');
                return r.json();
            }).then(function (data) {
                fillSelect(addWard, (data && data.wards) ? data.wards : [], '-- Chọn phường/xã --');
                addWard.disabled = false;
            }).catch(function () {
                resetSelect(addWard, '-- Không thể tải phường/xã --');
                addWard.disabled = true;
            });
        }

        if (addAddressForm && addCity && addDistrict && addWard && addStreet) {
            loadProvinces();

            addCity.addEventListener('change', function () {
                loadDistricts(selectedCode(addCity));
            });

            addDistrict.addEventListener('change', function () {
                loadWards(selectedCode(addDistrict));
            });

            addAddressForm.addEventListener('submit', function (e) {
                if (!addCity.value || !addDistrict.value) {
                    e.preventDefault();
                    alert('Vui lòng chọn tỉnh/thành phố và quận/huyện.');
                    return;
                }

                const ward = (addWard && addWard.value) ? addWard.value.trim() : '';
                const street = addStreet.value ? addStreet.value.trim() : '';
                if (ward && street && street.toLowerCase().indexOf(ward.toLowerCase()) === -1) {
                    addStreet.value = street + ', ' + ward;
                }
            });
        }

        // Nếu không tìm thấy form đổi pass (ví dụ user chưa login) thì thoát
        if (!pass || !confirm || !btn) return;

        // Lấy các dòng quy tắc
        const items = {
            lower: document.getElementById('chk-lower'),
            upper: document.getElementById('chk-upper'),
            number: document.getElementById('chk-number'),
            special: document.getElementById('chk-special'),
            len: document.getElementById('chk-len'),
            match: document.getElementById('chk-match')
        };

        // Hàm cập nhật trạng thái
        function updateItemStatus(item, isValid) {
            const icon = item.querySelector('i');
            if (isValid) {
                item.classList.add('valid');
                icon.className = 'fas fa-check-circle';
            } else {
                item.classList.remove('valid');
                icon.className = 'far fa-circle';
            }
            return isValid;
        }

        function validate() {
            const val = pass.value;
            const confVal = confirm.value;

            // 1. Chữ thường
            const isLower = updateItemStatus(items.lower, /[a-z]/.test(val));

            // 2. Chữ hoa
            const isUpper = updateItemStatus(items.upper, /[A-Z]/.test(val));
            
            // 3. Số
            const isNumber = updateItemStatus(items.number, /[0-9]/.test(val));

            // 4. Ký tự đặc biệt
            const isSpecial = updateItemStatus(items.special, /[^a-zA-Z0-9]/.test(val));

            // 5. Độ dài
            const isLen = updateItemStatus(items.len, val.length >= 8 && val.length <= 16);

            // 6. Trùng khớp
            const isMatch = updateItemStatus(items.match, val.length > 0 && val === confVal);

            // Mở nút khi thỏa mãn TẤT CẢ
            btn.disabled = !(isLower && isUpper && isNumber && isSpecial && isLen && isMatch);
        }

        pass.addEventListener('input', validate);
        confirm.addEventListener('input', validate);

        // Hàm ẩn hiện password
        window.toggleVisibility = function(fieldId, span) {
            const input = document.getElementById(fieldId);
            const icon = span.querySelector('i');
            if (input.type === "password") {
                input.type = "text";
                icon.className = 'far fa-eye text-muted';
            } else {
                input.type = "password";
                icon.className = 'far fa-eye-slash text-muted';
            }
        };
    });


/* * File: profile.js
 * Xử lý ẩn/hiện form Thêm địa chỉ và Cập nhật địa chỉ
 */

document.addEventListener("DOMContentLoaded", function () {
    
    // --- 1. XỬ LÝ NÚT THÊM ĐỊA CHỈ MỚI ---
    const addBtn = document.getElementById('toggleAddAddressBtn');
    const addFormWrap = document.getElementById('addAddressFormWrap');

    if (addBtn && addFormWrap) {
        addBtn.onclick = function (e) {
            e.preventDefault();
            if (addFormWrap.style.display === "none" || addFormWrap.style.display === "") {
                addFormWrap.style.setAttribute("style", "display: block !important");
            } else {
                addFormWrap.style.setAttribute("style", "display: none !important");
            }
        };
    }

// --- 2. XỬ LÝ CHO CÁC NÚT "CẬP NHẬT" TỪNG ĐỊA CHỈ ---
    // Tìm tất cả các nút có class btn-edit-address mà bạn đã đặt trong JSP
    const editButtons = document.querySelectorAll('.btn-edit-address');

    editButtons.forEach(function (btn) {
        btn.addEventListener('click', function (e) {
            e.preventDefault();

            // Lấy ID từ data-id="<%= a.getId() %>" mà bạn đã viết trong JSP
            const addressId = this.getAttribute('data-id');
            
            // Tìm đúng cái div form cập nhật theo ID tương ứng
            const updateForm = document.getElementById('editAddressWrap_' + addressId);

            if (updateForm) {
                // Nếu đang ẩn thì hiện, đang hiện thì thu vào (Toggle)
                if (updateForm.style.display === 'none' || updateForm.style.display === '') {
                    updateForm.style.display = 'block';
                    // Cuộn nhẹ màn hình tới form cho dễ nhìn
                    updateForm.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
                } else {
                    updateForm.style.display = 'none';
                }
            }
        });
    });
});

    
