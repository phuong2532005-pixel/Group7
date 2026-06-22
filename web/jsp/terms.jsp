<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Điều khoản sử dụng</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
            <link rel="stylesheet" href="${pageContext.request.contextPath}/css/terms.css?v=20260576307b">

</head>
<body>
<jsp:include page="header.jsp" />
<div class="container my-5">
    <div class="row">
        <div class="col-lg-10 mx-auto">
            <h1 class="mb-4">Điều khoản sử dụng</h1>
            <div class="card">
                <div class="card-body">
                    <div id="termsContent">
                        <c:choose>
                            <c:when test="${not empty termsContent}">
                                ${termsContent}
                            </c:when>
                            <c:otherwise>
                                <h4>1. Giới thiệu</h4>
                                <p>Chào mừng bạn đến với hệ thống bán hàng của chúng tôi. Khi sử dụng dịch vụ, bạn đồng ý với các điều khoản sau đây.</p>
                                
                                <h4>2. Quyền và nghĩa vụ của người dùng</h4>
                                <p>Người dùng có quyền:</p>
                                <ul>
                                    <li>Truy cập và sử dụng các dịch vụ trên hệ thống</li>
                                    <li>Được bảo vệ thông tin cá nhân theo quy định pháp luật</li>
                                    <li>Được hỗ trợ khi gặp vấn đề trong quá trình mua hàng</li>
                                </ul>
                                <p>Người dùng có nghĩa vụ:</p>
                                <ul>
                                    <li>Cung cấp thông tin chính xác khi đăng ký</li>
                                    <li>Không sử dụng hệ thống cho mục đích bất hợp pháp</li>
                                    <li>Thanh toán đầy đủ cho đơn hàng đã đặt</li>
                                </ul>
                                
                                <h4>3. Chính sách thanh toán</h4>
                                <p>Chúng tôi chấp nhận các hình thức thanh toán: COD, chuyển khoản ngân hàng, ví điện tử.</p>
                                
                                <h4>4. Chính sách hoàn trả</h4>
                                <p>Sản phẩm được hoàn trả trong vòng 7 ngày nếu có lỗi từ nhà sản xuất. Không áp dụng cho sản phẩm đã qua sử dụng.</p>
                                
                                <h4>5. Bảo mật thông tin</h4>
                                <p>Chúng tôi cam kết bảo vệ thông tin cá nhân của khách hàng và không chia sẻ cho bên thứ ba mà không có sự đồng ý.</p>
                                
                                <h4>6. Liên hệ</h4>
                                <p>Nếu có thắc mắc về điều khoản sử dụng, vui lòng liên hệ: support@example.com</p>
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
