
    (function () {
        var contextPath = window.location.pathname.substring(0, window.location.pathname.indexOf('/', 1)) || '';
        
        var tabs = document.querySelectorAll('.tab-btn');
        tabs.forEach(function (btn) {
            btn.addEventListener('click', function () {
                tabs.forEach(function (b) { b.classList.remove('active'); });
                btn.classList.add('active');
                var tab = btn.getAttribute('data-tab');
                document.getElementById('tab-pending').classList.toggle('active', tab === 'pending');
                document.getElementById('tab-approved').classList.toggle('active', tab === 'approved');
            });
        });
        // Thêm sự kiện click cho tên sản phẩm
        document.querySelectorAll('.admin-product-clickable').forEach(function(elem) {
            elem.addEventListener('click', function() {
                var productId = elem.getAttribute('data-product-id');
                var contextPath = window.location.pathname.substring(0, window.location.pathname.indexOf('/', 1)) || '';
                if (productId) {
                    window.location.href = contextPath + '/product?id=' + productId;
                }
            });
        });
    })();

    function updateStatus(productId, action) {
        var contextPath = window.location.pathname.substring(0, window.location.pathname.indexOf('/', 1)) || '';
        var body = 'action=' + encodeURIComponent(action) + '&productId=' + encodeURIComponent(productId);
        fetch(contextPath + '/admin/products', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: body
        })
        .then(function (res) { return res.json(); })
        .then(function (data) {
            if (!data.ok) {
                alert(data.message || 'Co loi xay ra');
                return;
            }
            var row = document.querySelector('tr[data-id="' + productId + '"]');
            if (!row) {
                return;
            }
            if (data.status === 'Approved') {
                moveToApproved(row, data.status);
            } else {
                row.remove();
            }
        })
        .catch(function () {
            alert('Khong the cap nhat trang thai');
        });
    }

    function moveToApproved(row, status) {
        var approvedTable = document.querySelector('#approved-table tbody');
        if (!approvedTable) {
            row.remove();
            return;
        }
        var newRow = document.createElement('tr');
        var id = row.getAttribute('data-id');
        var name = row.getAttribute('data-name');
        var category = row.getAttribute('data-category');
        var price = row.getAttribute('data-price');

        newRow.innerHTML =
            '<td>' + id + '</td>' +
            '<td>' + name + '</td>' +
            '<td>' + category + '</td>' +
            '<td>' + price + '</td>' +
            '<td><span class="badge-status">' + status + '</span></td>';
        approvedTable.appendChild(newRow);
        row.remove();
    }
