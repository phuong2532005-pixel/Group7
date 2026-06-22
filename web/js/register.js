
        document.addEventListener("DOMContentLoaded", function () {
            const pass = document.getElementById('password');
            const confirm = document.getElementById('confirm');
            const btn = document.getElementById('submitBtn');

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
                    icon.className = 'fas fa-check-circle'; // Tích xanh
                } else {
                    item.classList.remove('valid');
                    icon.className = 'far fa-circle'; // Tròn rỗng
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
                
                // 3. Số (Mới thêm cho form đăng ký)
                const isNumber = updateItemStatus(items.number, /[0-9]/.test(val));

                // 4. Ký tự đặc biệt
                const isSpecial = updateItemStatus(items.special, /[^a-zA-Z0-9]/.test(val));

                // 5. Độ dài
                const isLen = updateItemStatus(items.len, val.length >= 8 );

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
    