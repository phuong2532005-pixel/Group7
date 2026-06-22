<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Hướng dẫn mua hàng</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/guide.css?v=4775575757">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    </head>
    <body>
        <jsp:include page="header.jsp" />
        <div class="container my-5">
            <div class="row">
                <div class="col-lg-10 mx-auto">
                    <h1 class="mb-4">Hướng dẫn mua hàng</h1>
                    <div class="card">
                        <div class="card-body">
                            <div id="guideContent">
                                <c:choose>
                                    <c:when test="${not empty guideContent}">
                                        ${guideContent}
                                    </c:when>
                                    <c:otherwise>
                                        <h4>Bước 1: Đăng ký tài khoản</h4>
                                        <p>Truy cập trang web và nhấn vào nút "Đăng ký". Điền đầy đủ thông tin cá nhân và tạo tài khoản.</p>

                                        <h4>Bước 2: Tìm kiếm sản phẩm</h4>
                                        <p>Sử dụng thanh tìm kiếm hoặc duyệt qua các danh mục sản phẩm để tìm sản phẩm bạn muốn mua.</p>

                                        <h4>Bước 3: Thêm vào giỏ hàng</h4>
                                        <p>Khi tìm được sản phẩm ưng ý, nhấn vào nút "Thêm vào giỏ hàng". Bạn có thể tiếp tục mua sắm hoặc đi đến giỏ hàng để thanh toán.</p>

                                        <h4>Bước 4: Kiểm tra giỏ hàng</h4>
                                        <p>Nhấn vào biểu tượng giỏ hàng để xem các sản phẩm đã chọn. Bạn có thể điều chỉnh số lượng hoặc xóa sản phẩm không mong muốn.</p>

                                        <h4>Bước 5: Điền thông tin giao hàng</h4>
                                        <p>Nhập địa chỉ giao hàng, số điện thoại liên hệ và ghi chú (nếu có).</p>

                                        <h4>Bước 6: Chọn phương thức thanh toán</h4>
                                        <p>Chọn phương thức thanh toán phù hợp: COD (thanh toán khi nhận hàng), chuyển khoản ngân hàng, hoặc ví điện tử.</p>

                                        <h4>Bước 7: Xác nhận đơn hàng</h4>
                                        <p>Kiểm tra lại thông tin và nhấn "Đặt hàng". Bạn sẽ nhận được email xác nhận đơn hàng.</p>

                                        <h4>Bước 8: Theo dõi đơn hàng</h4>
                                        <p>Vào mục "Đơn hàng của tôi" để theo dõi trạng thái đơn hàng. Sản phẩm sẽ được giao trong vòng 3-5 ngày làm việc.</p>

                                        <div class="alert alert-info mt-4">
                                            <strong>Lưu ý:</strong> Nếu gặp vấn đề trong quá trình mua hàng, vui lòng liên hệ bộ phận hỗ trợ: support@example.com
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <jsp:include page="footer.jsp" />
    </body>
</html>
