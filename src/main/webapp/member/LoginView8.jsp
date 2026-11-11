<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="dao.DataAccessException8, dao.MemberDAO8, model.Member8, model.Customer8, model.Manager8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    String error = null;
    if ("POST".equalsIgnoreCase(request.getMethod())) {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (username == null || username.trim().isEmpty() || password == null || password.isEmpty()) {
            error = "Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu!";
        } else {
            try {
                MemberDAO8 memberDAO = new MemberDAO8();
                Member8 member = memberDAO.checkLogin(username.trim(), password);
                if (member instanceof Customer8 || member instanceof Manager8) {
                    member.setPassword(null);
                    HttpSession activeSession = request.getSession(true);
                    request.changeSessionId();
                    activeSession.setAttribute("member", member);
                    if (member instanceof Customer8) {
                        response.sendRedirect(request.getContextPath() + "/customer/CustomerMainMenu8.jsp");
                        return;
                    }
                    response.sendRedirect(request.getContextPath() + "/manager/ManagerMainMenu8.jsp");
                    return;
                } else if (member != null) {
                    if (session != null) {
                        session.removeAttribute("member");
                    }
                    error = "Tài khoản không thuộc vai trò Khách hàng hoặc Quản lý được hỗ trợ trong phạm vi hệ thống!";
                } else {
                    error = "Tên đăng nhập hoặc mật khẩu không chính xác!";
                }
            } catch (DataAccessException8 ex) {
                error = "Hệ thống đang gặp sự cố kết nối cơ sở dữ liệu. Vui lòng thử lại sau.";
            }
        }
    } else {
        String errParam = request.getParameter("error");
        if ("login_required".equals(errParam)) {
            error = "Vui lòng đăng nhập để tiếp tục truy cập!";
        } else if ("unauthorized".equals(errParam) || "customer_access_denied".equals(errParam) || "manager_access_denied".equals(errParam)) {
            error = "Bạn không có quyền truy cập vào chức năng này!";
        }
    }
    request.setAttribute("errorMsg", error);
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng nhập hệ thống ShopMan</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/style.css" rel="stylesheet">
</head>
<body>
<div class="container">
    <div class="login-card">
        <div class="login-header">
            Đăng nhập hệ thống ShopMan
        </div>
        <div class="p-4">
            <c:if test="${not empty errorMsg}">
                <div class="alert alert-danger py-2" role="alert">
                    <c:out value="${errorMsg}"/>
                </div>
            </c:if>

            <c:if test="${param.logout eq 'true'}">
                <div class="alert alert-success py-2" role="alert">
                    Đã đăng xuất thành công khỏi hệ thống!
                </div>
            </c:if>

            <form action="${pageContext.request.contextPath}/member/LoginView8.jsp" method="post">
                <div class="mb-3">
                    <label for="username" class="form-label fw-bold">Tên đăng nhập:</label>
                    <input type="text" class="form-control" id="username" name="username" required autofocus placeholder="Nhập tên đăng nhập">
                </div>
                <div class="mb-3">
                    <label for="password" class="form-label fw-bold">Mật khẩu:</label>
                    <input type="password" class="form-control" id="password" name="password" required placeholder="Nhập mật khẩu">
                </div>
                <div class="d-grid mt-4">
                    <button type="submit" class="btn btn-primary fw-bold" style="background-color: #2b6cb0; border-color: #2b6cb0;">
                        Đăng nhập
                    </button>
                </div>
            </form>

            <div class="mt-4 pt-3 border-top small text-muted">
                <p class="mb-1 fw-bold text-dark">Tài khoản mẫu thử nghiệm:</p>
                <div class="row">
                    <div class="col-6">
                        <strong>Khách hàng:</strong><br>
                        User: <code>khachhang1</code><br>
                        Pass: <code>123456</code>
                    </div>
                    <div class="col-6">
                        <strong>Quản lý:</strong><br>
                        User: <code>quanly1</code><br>
                        Pass: <code>123456</code>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>

