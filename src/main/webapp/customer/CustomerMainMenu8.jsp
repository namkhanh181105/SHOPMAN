<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.Customer8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    Object memberObj = session.getAttribute("member");
    if (!(memberObj instanceof Customer8)) {
        response.sendRedirect(request.getContextPath() + "/member/LoginView8.jsp");
        return;
    }
    Customer8 customer = (Customer8) memberObj;
    request.setAttribute("customerName", customer.getName());
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>CustomerMainMenu8 - Giao diện chính của khách hàng</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/style.css" rel="stylesheet">
</head>
<body>
<div class="shopman-window">
    <div class="shopman-header">
        CustomerMainMenu8 &nbsp;-&nbsp; Giao diện chính của khách hàng
    </div>
    <ul class="shopman-nav">
        <li class="shopman-nav-item">
            <a href="javascript:void(0)" class="shopman-nav-link text-muted">Tìm kiếm hàng hóa</a>
        </li>
        <li class="shopman-nav-item">
            <a href="javascript:void(0)" class="shopman-nav-link text-muted">Đặt hàng</a>
        </li>
        <li class="shopman-nav-item">
            <a href="${pageContext.request.contextPath}/customer/InvoiceTrackingMenu8.jsp" class="shopman-nav-link fw-bold text-dark">
                Theo dõi hóa đơn
            </a>
        </li>
        <li class="shopman-nav-item">
            <a href="${pageContext.request.contextPath}/member/Logout8.jsp" class="shopman-nav-link text-danger">
                Đăng xuất
            </a>
        </li>
    </ul>

    <div class="shopman-body">
        <div class="p-4 text-center my-auto">
            <h5 class="text-secondary">Xin chào khách hàng: <strong><c:out value="${customerName}"/></strong></h5>
            <p class="text-muted mt-2">Vui lòng chọn chức năng <strong>"Theo dõi hóa đơn"</strong> trên thanh điều hướng để xem danh sách và trạng thái hóa đơn của bạn.</p>
        </div>
    </div>
</div>
</body>
</html>

