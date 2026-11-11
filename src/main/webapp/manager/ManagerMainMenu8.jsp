<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.Manager8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    Object memberObj = session.getAttribute("member");
    if (!(memberObj instanceof Manager8)) {
        response.sendRedirect(request.getContextPath() + "/member/LoginView8.jsp");
        return;
    }
    Manager8 manager = (Manager8) memberObj;
    request.setAttribute("managerName", manager.getName());
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ManagerMainMenu8 - Giao diện chính của nhân viên quản lý</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/style.css" rel="stylesheet">
</head>
<body>
<div class="shopman-window">
    <div class="shopman-header">
        ManagerMainMenu8 &nbsp;-&nbsp; Giao diện chính của nhân viên quản lý
    </div>
    <ul class="shopman-nav">
        <li class="shopman-nav-item">
            <a href="javascript:void(0)" class="shopman-nav-link text-muted">Nhập hàng</a>
        </li>
        <li class="shopman-nav-item">
            <a href="javascript:void(0)" class="shopman-nav-link text-muted">Quản lý thông tin</a>
        </li>
        <li class="shopman-nav-item">
            <a href="javascript:void(0)" class="shopman-nav-link fw-bold text-dark">Xem thống kê</a>
        </li>
        <li class="shopman-nav-item">
            <a href="${pageContext.request.contextPath}/member/Logout8.jsp" class="shopman-nav-link text-danger">
                Đăng xuất
            </a>
        </li>
    </ul>

    <div class="shopman-body">
        <div class="d-flex justify-content-end pe-4 pt-3">
            <a href="${pageContext.request.contextPath}/manager/SupplierImportStatisticsView8.jsp" class="btn-submenu">
                Thống kê nhà cung cấp theo lượng hàng nhập
            </a>
        </div>
        <div class="mt-auto pb-4 ps-2">
            <small class="text-muted">Đang đăng nhập với vai trò: Quản lý - <strong><c:out value="${managerName}"/></strong></small>
        </div>
    </div>
</div>
</body>
</html>

