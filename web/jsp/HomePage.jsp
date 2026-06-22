<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>FASHION STORE - 2026</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/fashionStyle.css?v=2026034507b">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/banner.css?v=202603407b">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/home.css?v=2026030407b">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/filter.css?v=2026034507b">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/swiper@11/swiper-bundle.min.css" />

        <script src="https://cdn.jsdelivr.net/npm/swiper@11/swiper-bundle.min.js"></script>
        <meta name="context-path" content="${pageContext.request.contextPath}">

    </head>
    <body class="home">
        <jsp:include page="header.jsp" />

        <c:if test="${not empty param.msg}">
            <div id="flash" class="flash">
                <c:choose>
                    <c:when test="${param.msg == 'logout_success'}">Bạn đã đăng xuất.</c:when>
                    <c:otherwise><c:out value="${param.msg}"/></c:otherwise>
                </c:choose>
            </div>
        </c:if>

        <div class="simple-banner">

            <input type="radio" name="slide" id="s-1" checked hidden>
            <input type="radio" name="slide" id="s-2" hidden>
            <input type="radio" name="slide" id="s-3" hidden>
            <input type="radio" name="slide" id="s-4" hidden>
            <input type="radio" name="slide" id="s-5" hidden>

            <div class="slide-track">

                <div class="slide-item">
                    <img src="${pageContext.request.contextPath}/img/products/1.jpg" alt="Banner 1">
                    <div class="nav-arrows">
                        <label for="s-5" class="prev"><i class="fa-solid fa-chevron-left"></i></label>
                        <label for="s-2" class="next"><i class="fa-solid fa-chevron-right"></i></label>
                    </div>
                </div>

                <div class="slide-item">
                    <img src="${pageContext.request.contextPath}/img/products/2.jpg" alt="Banner 2">
                    <div class="nav-arrows">
                        <label for="s-1" class="prev"><i class="fa-solid fa-chevron-left"></i></label>
                        <label for="s-3" class="next"><i class="fa-solid fa-chevron-right"></i></label>
                    </div>
                </div>

                <div class="slide-item">
                    <img src="${pageContext.request.contextPath}/img/products/3.jpg" alt="Banner 3">
                    <div class="nav-arrows">
                        <label for="s-2" class="prev"><i class="fa-solid fa-chevron-left"></i></label>
                        <label for="s-4" class="next"><i class="fa-solid fa-chevron-right"></i></label>
                    </div>
                </div>

                <div class="slide-item">
                    <img src="${pageContext.request.contextPath}/img/products/4.jpg" alt="Banner 4">
                    <div class="nav-arrows">
                        <label for="s-3" class="prev"><i class="fa-solid fa-chevron-left"></i></label>
                        <label for="s-5" class="next"><i class="fa-solid fa-chevron-right"></i></label>
                    </div>
                </div>

                <div class="slide-item">
                    <img src="${pageContext.request.contextPath}/img/products/5.jpg" alt="Banner 5">
                    <div class="nav-arrows">
                        <label for="s-4" class="prev"><i class="fa-solid fa-chevron-left"></i></label>
                        <label for="s-1" class="next"><i class="fa-solid fa-chevron-right"></i></label>
                    </div>
                </div>

            </div>

            <div class="dots-container">
                <label for="s-1" class="dot"></label>
                <label for="s-2" class="dot"></label>
                <label for="s-3" class="dot"></label>
                <label for="s-4" class="dot"></label>
                <label for="s-5" class="dot"></label>
            </div>

        </div>

        <div class="home-shell">
            <section class="home-section">
                <div class="home-heading">
                    <div>
                        <div class="home-title">Bán Chạy</div>
                        <div class="home-sub">Sản phẩm được mua nhiều nhất gần đây</div>
                    </div>
                    <span class="home-badge" ><i class="fa-solid fa-fire"></i> Hot</span>
                </div>
                <c:choose>
                    <c:when test="${empty listBest}">
                        <div class="home-empty">Chưa có sản phẩm bán chạy</div>
                    </c:when>
                    <c:otherwise>
                        <div class="swiper-container-wrapper" >

                            <div class="slider-container">
                                <div class="swiper mySwiper">
                                    <div class="home-grid swiper-wrapper">
                                        <c:forEach items="${listBest}" var="p">
                                            <div class="home-card swiper-slide">
                                                <a href="${pageContext.request.contextPath}/product?id=${p.id}">
                                                    <c:choose>
                                                        <c:when test="${not empty p.thumbnail}">
                                                            <c:set var="imgPath" value="${p.thumbnail}" />
                                                            <c:if test="${not fn:contains(imgPath, '.')}">
                                                                <c:set var="imgPath" value="${imgPath}.jpg" />
                                                            </c:if>
                                                            <img src="${pageContext.request.contextPath}/img/products/${imgPath}" 
                                                                 alt="${p.name}" >
                                                        </c:when>
                                                        <c:otherwise>
                                                            <div ></div>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </a>
                                                <div class="info">
                                                    <div class="name">${p.name}</div>
                                                    <div class="price">
                                                        <fmt:formatNumber value="${p.basePrice}" pattern="#,##0"/> đ
                                                    </div>
                                                </div>
                                            </div>
                                        </c:forEach>
                                    </div>
                                </div>

                                <div class="swiper-button-next"></div>
                                <div class="swiper-button-prev"></div>
                            </div>
                        </c:otherwise>
                    </c:choose>
            </section>

            <section class="home-section" id="section-nam">
                <div class="home-heading">
                    <div>
                        <div class="home-title">Thời Trang Nam</div>
                        <div class="home-sub">Phong cách lịch lãm, năng động</div>
                    </div>
                </div>
                <div class="swiper-container-wrapper">
                    <div class="slider-container" ">
                        <div class="swiper mySwiper">
                            <div class="home-grid swiper-wrapper">
                                <c:forEach items="${listNam}" var="p">
                                    <div class="home-card swiper-slide">
                                        <a href="${pageContext.request.contextPath}/product?id=${p.id}">
                                            <c:choose>
                                                <c:when test="${not empty p.thumbnail}">
                                                    <c:set var="imgPath" value="${p.thumbnail}" />
                                                    <c:if test="${not fn:contains(imgPath, '.')}">
                                                        <c:set var="imgPath" value="${imgPath}.jpg" />
                                                    </c:if>
                                                    <c:choose>
                                                        <c:when test="${fn:startsWith(imgPath,'http://') or fn:startsWith(imgPath,'https://')}">
                                                            <img src="${imgPath}" alt="${p.name}" >
                                                        </c:when>
                                                        <c:when test="${fn:contains(imgPath,'/')}">
                                                            <c:choose>
                                                                <c:when test="${fn:startsWith(imgPath,'/')}">
                                                                    <img src="${pageContext.request.contextPath}${imgPath}" alt="${p.name}" >
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <img src="${pageContext.request.contextPath}/${imgPath}" alt="${p.name}">
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <img src="${pageContext.request.contextPath}/img/products/${imgPath}" alt="${p.name}" >
                                                        </c:otherwise>
                                                    </c:choose>
                                                </c:when>
                                                <c:otherwise>
                                                    <div ></div>
                                                </c:otherwise>
                                            </c:choose>
                                        </a>
                                        <div class="info">
                                            <div class="name">${p.name}</div>
                                            <div class="price"><fmt:formatNumber value="${p.basePrice}" pattern="#,##0"/> đ</div>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                        </div>
                        <div class="swiper-button-next"></div>
                        <div class="swiper-button-prev"></div>
                    </div>
                </div>
            </section>

            <section class="home-section" id="section-nu">
                <div class="home-heading">
                    <div>
                        <div class="home-title">Thời Trang Nữ</div>
                        <div class="home-sub">Tinh tế, Nhẹ Nhàng, Thời thượng</div>
                    </div>
                </div>
                <div class="swiper-container-wrapper">
                    <div class="slider-container">
                        <div class="swiper mySwiper">
                            <div class="home-grid swiper-wrapper">
                                <c:forEach items="${listNu}" var="p">
                                    <div class="home-card swiper-slide">
                                        <a href="${pageContext.request.contextPath}/product?id=${p.id}">
                                            <c:choose>
                                                <c:when test="${not empty p.thumbnail}">
                                                    <c:set var="imgPath" value="${p.thumbnail}" />
                                                    <c:if test="${not fn:contains(imgPath, '.')}">
                                                        <c:set var="imgPath" value="${imgPath}.jpg" />
                                                    </c:if>
                                                    <c:choose>
                                                        <c:when test="${fn:startsWith(imgPath,'http://') or fn:startsWith(imgPath,'https://')}">
                                                            <img src="${imgPath}" alt="${p.name}" >
                                                        </c:when>
                                                        <c:when test="${fn:contains(imgPath,'/')}">
                                                            <c:choose>
                                                                <c:when test="${fn:startsWith(imgPath,'/')}">
                                                                    <img src="${pageContext.request.contextPath}${imgPath}" alt="${p.name}" >
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <img src="${pageContext.request.contextPath}/${imgPath}" alt="${p.name}" >
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <img src="${pageContext.request.contextPath}/img/products/${imgPath}" alt="${p.name}" >
                                                        </c:otherwise>
                                                    </c:choose>
                                                </c:when>
                                                <c:otherwise>
                                                    <div ></div>
                                                </c:otherwise>
                                            </c:choose>
                                        </a>
                                        <div class="info">
                                            <div class="name">${p.name}</div>
                                            <div class="price"><fmt:formatNumber value="${p.basePrice}" pattern="#,##0"/> đ</div>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                        </div>
                        <div class="swiper-button-next"></div>
                        <div class="swiper-button-prev"></div>
                    </div>
                </div>
            </section>

            <section class="home-section" id="section-unisex">
                <div class="home-heading">
                    <div>
                        <div class="home-title">Unisex và Phụ Kiện</div>
                        <div class="home-sub">Tối giản, dễ phối, mọi giới tính</div>
                    </div>
                </div>
                <div class="swiper-container-wrapper">
                    <div class="slider-container" >
                        <div class="swiper mySwiper">
                            <div class="home-grid swiper-wrapper">
                                <c:forEach items="${listUnisex}" var="p">
                                    <div class="home-card swiper-slide">
                                        <a href="${pageContext.request.contextPath}/product?id=${p.id}">
                                            <c:choose>
                                                <c:when test="${not empty p.thumbnail}">
                                                    <c:set var="imgPath" value="${p.thumbnail}" />
                                                    <c:if test="${not fn:contains(imgPath, '.')}">
                                                        <c:set var="imgPath" value="${imgPath}.jpg" />
                                                    </c:if>
                                                    <c:choose>
                                                        <c:when test="${fn:startsWith(imgPath,'http://') or fn:startsWith(imgPath,'https://')}">
                                                            <img src="${imgPath}" alt="${p.name}" >
                                                        </c:when>
                                                        <c:when test="${fn:contains(imgPath,'/')}">
                                                            <c:choose>
                                                                <c:when test="${fn:startsWith(imgPath,'/')}">
                                                                    <img src="${pageContext.request.contextPath}${imgPath}" alt="${p.name}" >
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <img src="${pageContext.request.contextPath}/${imgPath}" alt="${p.name}" >
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <img src="${pageContext.request.contextPath}/img/products/${imgPath}" alt="${p.name}" >
                                                        </c:otherwise>
                                                    </c:choose>
                                                </c:when>
                                                <c:otherwise>
                                                    <div ></div>
                                                </c:otherwise>
                                            </c:choose>
                                        </a>
                                        <div class="info">
                                            <div class="name">${p.name}</div>
                                            <div class="price"><fmt:formatNumber value="${p.basePrice}" pattern="#,##0"/>đ</div>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                        </div>
                        <div class="swiper-button-next"></div>
                        <div class="swiper-button-prev"></div>
                    </div>
                </div>
            </section>
        </div>

        <div class="list-wrap" id="product-section">


            <div class="list-layout" id="product-content">

                <div class="check-section">
                    <h2 class="section-title">Sản Phẩm <br>Thời Trang </h2>

                    <aside class="filter-panel">
                        <div class="filter-title">Bộ lọc</div>
                        <form action="${pageContext.request.contextPath}/home" method="get" id="product-filter-form">
                            <div class="filter-group">
                                <label>Từ khóa</label>
                                <input type="text" name="q" value="${q}" placeholder="Ví dụ: quần nam, áo nữ, sneaker unisex...">
                            </div>

                            <div class="filter-group">
                                <label>Danh mục</label>
                                <select name="cate">
                                    <option value="">Tất cả</option>
                                    <c:forEach items="${categories}" var="cat">
                                        <option value="${cat.id}" ${cate == cat.id ? 'selected' : ''}>
                                            <c:forEach begin="1" end="${cat.level}">--</c:forEach>${cat.name}
                                            </option>
                                    </c:forEach>
                                </select>
                            </div>

                            <div class="filter-group">
                                <label>Khoảng giá</label>
                                <div class="range-row">
                                    <input type="number" name="min" value="${min}" placeholder="Min">
                                    <input type="number" name="max" value="${max}" placeholder="Max">
                                </div>
                            </div>

                            <button type="submit" class="filter-btn">Áp dụng</button>
                        </form>
                    </aside>
                </div>



                <section>
                    <c:choose>
                        <c:when test="${empty products}">
                            <div class="empty">Không có sản phẩm phù hợp</div>
                        </c:when>
                        <c:otherwise>
                            <div class="product-grid">
                                <c:forEach items="${products}" var="p">
                                    <div class="product-card">
                                        <a href="${pageContext.request.contextPath}/product?id=${p.id}">
                                            <c:choose>
                                                <c:when test="${not empty p.thumbnail}">
                                                    <c:set var="imgPath" value="${p.thumbnail}" />
                                                    <c:if test="${not fn:contains(imgPath, '.')}">
                                                        <c:set var="imgPath" value="${imgPath}.jpg" />
                                                    </c:if>
                                                    <c:choose>
                                                        <c:when test="${fn:startsWith(imgPath,'http://') or fn:startsWith(imgPath,'https://')}">
                                                            <img src="${imgPath}" alt="${p.name}">
                                                        </c:when>
                                                        <c:when test="${fn:contains(imgPath,'/')}">
                                                            <c:choose>
                                                                <c:when test="${fn:startsWith(imgPath,'/')}">
                                                                    <img src="${pageContext.request.contextPath}${imgPath}" alt="${p.name}">
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <img src="${pageContext.request.contextPath}/${imgPath}" alt="${p.name}">
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <img src="${pageContext.request.contextPath}/img/products/${imgPath}" alt="${p.name}">
                                                        </c:otherwise>
                                                    </c:choose>
                                                </c:when>
                                                <c:otherwise>
                                                    <div></div>
                                                </c:otherwise>
                                            </c:choose>
                                        </a>

                                        <div class="info">
                                            <div class="name" href="${pageContext.request.contextPath}/product?id=${p.id}">${p.name}</div>
                                            <div class="price">
                                                <fmt:formatNumber value="${p.basePrice}" pattern="#,##0"/> đ
                                            </div>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                        </c:otherwise>
                    </c:choose>

                    <c:if test="${totalPages > 1}">
                        <div class="paging-container" >
                            <div class="paging-info">
                                Trang <strong>${page}</strong>/<strong>${totalPages}</strong>
                            </div>

                            <div class="paging" >
                                <%-- Nút Previous --%>
                                <c:if test="${page > 1}">
                                    <a class="page-link" href="${pageContext.request.contextPath}/home?page=${page - 1}&q=${q}&cate=${cate}&min=${min}&max=${max}">
                                        <i class="fa-solid fa-chevron-left"></i>
                                    </a>
                                </c:if>

                                <c:forEach begin="1" end="${totalPages}" var="i">
                                    <c:choose>
                                        <c:when test="${i == 1 || i == totalPages || (i >= page - 2 && i <= page + 2)}">
                                            <a class="page-link ${i == page ? 'active' : ''}" 
                                               href="${pageContext.request.contextPath}/home?page=${i}&q=${q}&cate=${cate}&min=${min}&max=${max}">${i}</a>
                                        </c:when>
                                        <c:when test="${i == page - 3 || i == page + 3}">
                                            <span class="page-dots" >...</span>
                                        </c:when>
                                    </c:choose>
                                </c:forEach>

                                <%-- Nút Next --%>
                                <c:if test="${page < totalPages}">
                                    <a class="page-link" href="${pageContext.request.contextPath}/home?page=${page + 1}&q=${q}&cate=${cate}&min=${min}&max=${max}">
                                        <i class="fa-solid fa-chevron-right"></i>
                                    </a>
                                </c:if>
                            </div>
                        </div>
                    </c:if>
                </section>
            </div>
        </div>

        <jsp:include page="/jsp/footer.jsp" />

        <script src="${pageContext.request.contextPath}/js/HomePage.js"></script>
    </body>
</html>