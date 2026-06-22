
            (function () {
                var flash = document.getElementById('flash');
                if (flash) {
                    // Remove ?msg from URL immediately (no reload)
                    try {
                        if (window.history && history.replaceState) {
                            var u = new URL(window.location.href);
                            u.searchParams.delete('msg');
                            history.replaceState(null, '', u.pathname + (u.search ? u.search : '') + (u.hash ? u.hash : ''));
                        }
                    } catch (e) {
                        // ignore URL API errors
                    }

                    // Hide and remove after 3s
                    setTimeout(function () {
                        flash.classList.add('hide');
                        setTimeout(function () {
                            if (flash && flash.remove)
                                flash.remove();
                        }, 600);
                    }, 3000);
                }
            })();
        


            (function () {
                var form = document.getElementById('product-filter-form');
                var section = document.getElementById('product-section');
                if (!form || !section)
                    return;

                function buildUrl(base, params) {
                    var url = new URL(base, window.location.origin);
                    params.forEach(function (value, key) {
                        if (value !== null && value !== '') {
                            url.searchParams.set(key, value);
                        }
                    });
                    return url.toString();
                }

                function loadProductSection(url) {
                    fetch(url, {headers: {'X-Requested-With': 'fetch'}})
                            .then(function (r) {
                                return r.text();
                            })
                            .then(function (html) {
                                var parser = new DOMParser();
                                var doc = parser.parseFromString(html, 'text/html');
                                var nextSection = doc.getElementById('product-section');
                                if (!nextSection)
                                    return;
                                section.innerHTML = nextSection.innerHTML;
                                bindPaging();
                                bindForm();
                            })
                            .catch(function () {
                                // ignore
                            });
                }

                function bindForm() {
                    var nextForm = document.getElementById('product-filter-form');
                    if (!nextForm)
                        return;
                    nextForm.addEventListener('submit', function (e) {
                        e.preventDefault();
                        var data = new FormData(nextForm);
                        var url = buildUrl(nextForm.action, data);
                        if (window.history && history.pushState) {
                            history.pushState(null, '', url);
                        }
                        loadProductSection(url);
                    });
                }

                function bindPaging() {
                    var links = document.querySelectorAll('#product-section .page-link');
                    links.forEach(function (link) {
                        link.addEventListener('click', function (e) {
                            e.preventDefault();
                            var url = link.getAttribute('href');
                            if (!url)
                                return;
                            if (window.history && history.pushState) {
                                history.pushState(null, '', url);
                            }
                            loadProductSection(url);
                        });
                    });
                }

                bindForm();
                bindPaging();

                window.addEventListener('popstate', function () {
                    loadProductSection(window.location.href);
                });
            })();
        


            // Chạy qua tất cả các slider trên trang để khởi tạo riêng biệt
            var swiperContainers = document.querySelectorAll('.slider-container');
            
            swiperContainers.forEach(function(container) {
                var swiperEl = container.querySelector('.mySwiper');
                var nextBtn = container.querySelector('.swiper-button-next');
                var prevBtn = container.querySelector('.swiper-button-prev');

                new Swiper(swiperEl, {
                    slidesPerView: 4, 
                    spaceBetween: 30, 
                    slidesPerGroup: 1, 
                    loop: false, 
                    centerInsufficientSlides: true,
                    navigation: {
                        nextEl: nextBtn,
                        prevEl: prevBtn,
                    },
                    breakpoints: {
                        1024: {slidesPerView: 4},
                        768: {slidesPerView: 2},
                        320: {slidesPerView: 1}
                    }
                });
            });
        