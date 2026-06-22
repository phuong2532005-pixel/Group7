document.addEventListener("DOMContentLoaded", function() {
    const path = window.contextPath || '';
    const labels = ['Tháng 1','Tháng 2','Tháng 3','Tháng 4','Tháng 5','Tháng 6','Tháng 7','Tháng 8','Tháng 9','Tháng 10','Tháng 11','Tháng 12'];

    // --- KHỞI TẠO BIỂU ĐỒ ---
    const revenueChart = new Chart(document.getElementById('revenueChart'), {
        type: 'bar',
        data: { labels: labels, datasets: [{ label: 'Doanh thu (VNĐ)', data: new Array(12).fill(0), backgroundColor: 'rgba(54,162,235,0.7)'}] }
    });

    const ordersChart = new Chart(document.getElementById('ordersChart'), {
        type: 'line',
        data: { labels: labels, datasets: [{ label: 'Số đơn', data: new Array(12).fill(0), borderColor: 'rgba(182,127,53,1)', fill: false}] }
    });

    // --- HÀM LẤY DỮ LIỆU ---
    function fetchRevenue() {
        const year = document.getElementById('yearInput').value;
        const url = `${path}/admin/revenue-data?year=${year}`;

        fetch(url).then(res => res.json()).then(data => {
            revenueChart.data.datasets[0].data = data.months || [];
            revenueChart.update();
            ordersChart.data.datasets[0].data = data.orderCounts || [];
            ordersChart.update();
            
            // Cập nhật số tổng trên Card
            const totalRevenue = (data.months || []).reduce((a, b) => a + b, 0);
            const revenueText = document.querySelector('.text-bg-info .card-text');
            if (revenueText) revenueText.innerText = totalRevenue.toLocaleString() + " VNĐ";
        }).catch(err => console.error("Lỗi Doanh thu:", err));
    }

    // --- SỰ KIỆN ---
    document.getElementById('btnShowShopReport').addEventListener('click', fetchRevenue);

    // Tự động load khi vào trang
    fetchRevenue();
});