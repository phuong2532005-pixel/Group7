(function () {
    document.addEventListener('click', function (event) {
        var clickable = event.target.closest('.order-item-clickable');
        if (!clickable) {
            return;
        }

        var productId = clickable.getAttribute('data-product-id');
        if (!productId) {
            return;
        }

        var contextPath = window.location.pathname.substring(0, window.location.pathname.indexOf('/', 1)) || '';
        window.location.href = contextPath + '/product?id=' + encodeURIComponent(productId);
    });
})();
