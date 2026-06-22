// Sự kiện click cho sản phẩm trong lịch sử đơn hàng
document.addEventListener('DOMContentLoaded', function() {
    document.querySelectorAll('.order-item-clickable').forEach(function(elem) {
        elem.addEventListener('click', function() {
            var productId = elem.getAttribute('data-product-id');
            var contextPath = window.location.pathname.substring(0, window.location.pathname.indexOf('/', 1)) || '';
            if (productId) {
                window.location.href = contextPath + '/product?id=' + productId;
            }
        });
    });
});
/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */

// 1. XÁC NHẬN EMAIL

document.addEventListener("DOMContentLoaded", function () {
    console.log("--- BẮT ĐẦU JS ---");

    // 1. Kiểm tra phần tử bao ngoài
    const countdownWrap = document.getElementById('countdown-wrap');
    if (!countdownWrap) {
        console.log("Không tìm thấy countdown-wrap -> Thoát");
        return;
    }

    // 2. Lấy các phần tử con
    const countdownEl = document.getElementById('countdown');
    const verifyBtn = document.getElementById('verify-btn');
    const serverStateEl = document.getElementById('page-server-state');

    // 3. Đọc tín hiệu từ Server
    // Nếu có biến success thì attribute sẽ là "true", ngược lại là "false"
    const shouldStart = serverStateEl ? serverStateEl.getAttribute('data-should-start') === 'true' : false;
    
    console.log("Trạng thái Server cho phép đếm:", shouldStart);

    // 4. XỬ LÝ LOGIC
    if (!shouldStart) {
        // Nếu chưa gửi mã thành công -> Khóa nút, ẩn đếm ngược
        if (verifyBtn) verifyBtn.disabled = true;
        countdownWrap.classList.add('d-none'); // Đảm bảo ẩn đi
        return;
    }

    // NẾU ĐƯỢC PHÉP CHẠY:
    console.log("Bắt đầu đếm ngược...");
    
    // Mở khóa nút
    if (verifyBtn) verifyBtn.disabled = false;
    
    // Hiện khung đếm ngược (Xóa class d-none của Bootstrap)
    countdownWrap.classList.remove('d-none');

    // Bộ đếm
    let timeLeft = 60;
    
    // Cập nhật ngay lập tức lần đầu tiên để user không phải đợi 1s
    if (countdownEl) countdownEl.innerText = timeLeft;

    // Thiết lập vòng lặp 1 giây
    const timerId = setInterval(function() {
        timeLeft--;
        console.log("Giây:", timeLeft); // Log ra console để check

        if (countdownEl) countdownEl.innerText = timeLeft;

        if (timeLeft <= 0) {
            clearInterval(timerId); // Dừng vòng lặp
            
            // Xử lý khi hết giờ
            if (verifyBtn) verifyBtn.disabled = true;
            countdownWrap.innerHTML = '<span class="text-danger small fw-bold">Mã đã hết hạn. Vui lòng gửi lại.</span>';
        }
    }, 1000);
});
//2. ĐĂNG NHẬP
/* web/js/login.js */

// 1. Hàm ẩn/hiện mật khẩu
function togglePwd() {
    const pass = document.getElementById('password-field');
    const icon = document.getElementById('eye-icon');
    if (pass.type === "password") {
        pass.type = "text";
        icon.classList.remove('fa-eye');
        icon.classList.add('fa-eye-slash');
    } else {
        pass.type = "password";
        icon.classList.remove('fa-eye-slash');
        icon.classList.add('fa-eye');
    }
}

// 2. Tự động kiểm tra lỗi khi tải trang
document.addEventListener("DOMContentLoaded", function() {
    const errorBox = document.querySelector('.error-box');
    const errorSpan = document.querySelector('.error-box span');

    // Nếu tìm thấy hộp lỗi VÀ bên trong span có chữ (không rỗng)
    if (errorBox && errorSpan && errorSpan.innerText.trim() !== "") {
        errorBox.style.display = 'flex'; // Hoặc 'block' tùy CSS của bạn
    }
});


