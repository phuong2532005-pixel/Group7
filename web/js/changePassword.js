
            document.addEventListener("DOMContentLoaded", function () {
                const pass = document.getElementById('newPass');
                const confirm = document.getElementById('confirmPass');
                const btn = document.getElementById('submitBtn');

                // Lấy 5 dòng quy tắc
                const items = {
                    lower: document.getElementById('chk-lower'),
                    upper: document.getElementById('chk-upper'),
                    special: document.getElementById('chk-special'), // <-- Mới thêm
                    len: document.getElementById('chk-len'),
                    match: document.getElementById('chk-match')
                };

                // Hàm cập nhật giao diện (Xanh/Xám)
                function updateItemStatus(item, isValid) {
                    const icon = item.querySelector('i');
                    if (isValid) {
                        item.classList.add('valid');
                        icon.className = 'fas fa-check-circle'; // Dấu tích xanh
                    } else {
                        item.classList.remove('valid');
                        icon.className = 'far fa-circle'; // Hình tròn rỗng
                    }
                    return isValid;
                }

                // Hàm kiểm tra chính
                function validate() {
                    const val = pass.value;
                    const confVal = confirm.value;

                    // 1. Kiểm tra chữ thường
                    const isLower = updateItemStatus(items.lower, /[a-z]/.test(val));

                    // 2. Kiểm tra chữ hoa
                    const isUpper = updateItemStatus(items.upper, /[A-Z]/.test(val));
                    
                    // 3. Kiểm tra KÝ TỰ ĐẶC BIỆT (Mới thêm)
                    // Regex: /[^a-zA-Z0-9]/ nghĩa là "không phải chữ cũng không phải số"
                    const isSpecial = updateItemStatus(items.special, /[^a-zA-Z0-9]/.test(val));

                    // 4. Kiểm tra độ dài (8)
                    const isLen = updateItemStatus(items.len, val.length >= 8);

                    // 5. Kiểm tra trùng khớp
                    const isMatch = updateItemStatus(items.match, val.length > 0 && val === confVal);

                    // Nút chỉ mở khi ĐỦ CẢ 5 ĐIỀU KIỆN
                    btn.disabled = !(isLower && isUpper && isSpecial && isLen && isMatch);
                }

                // Gắn sự kiện nhập liệu
                pass.addEventListener('input', validate);
                confirm.addEventListener('input', validate);
                
                // Hàm ẩn/hiện mật khẩu
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
        