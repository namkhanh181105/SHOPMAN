<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thông báo hệ thống ShopMan</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/style.css" rel="stylesheet">
</head>
<body>
<div class="container">
    <div class="login-card">
        <div class="login-header">
            Thông báo hệ thống ShopMan
        </div>
        <div class="p-4 text-center">
            <div class="alert alert-warning" role="alert">
                Yêu cầu không hợp lệ hoặc hệ thống đang bận. Vui lòng quay lại và thử lại sau.
            </div>
            <a href="${pageContext.request.contextPath}/member/LoginView8.jsp" class="btn btn-primary">
                Về trang đăng nhập
            </a>
        </div>
    </div>
</div>
</body>
</html>

