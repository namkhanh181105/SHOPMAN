<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="dao.DataAccessException8, dao.InvoiceDAO8, model.Customer8, model.Invoice8, util.DateUtil8" %>
<%@ page import="java.util.ArrayList, java.util.HashMap, java.util.List, java.util.Map" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    Object memberObj = session.getAttribute("member");
    if (!(memberObj instanceof Customer8)) {
        response.sendRedirect(request.getContextPath() + "/member/LoginView8.jsp");
        return;
    }
    Customer8 customer = (Customer8) memberObj;

    String dbError = null;
    List<Map<String, Object>> invoiceRows = new ArrayList<>();
    try {
        InvoiceDAO8 invoiceDAO = new InvoiceDAO8();
        Invoice8[] invoices = invoiceDAO.getInvoiceList(customer.getId());
        if (invoices != null) {
            for (Invoice8 inv : invoices) {
                Map<String, Object> row = new HashMap<>();
                row.put("id", inv.getId());
                row.put("code", String.format("HD%02d", inv.getId()));
                row.put("createdDate", DateUtil8.formatDate(inv.getCreatedDate()));
                row.put("totalAmount", String.format("%.0f", inv.getTotalAmount()));
                row.put("status", inv.getStatus() != null ? inv.getStatus() : "");
                invoiceRows.add(row);
            }
        }
    } catch (DataAccessException8 e) {
        dbError = "Hệ thống đang gặp sự cố khi tải danh sách hóa đơn. Vui lòng thử lại sau.";
    }
    request.setAttribute("dbError", dbError);
    request.setAttribute("invoiceRows", invoiceRows);
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>InvoiceTrackingMenu8 - Danh sách hóa đơn đang đặt</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/style.css" rel="stylesheet">
</head>
<body>
<div class="shopman-window">
    <div class="shopman-header">
        InvoiceTrackingMenu8 &nbsp;-&nbsp; Danh sách hóa đơn đang đặt
    </div>
    <ul class="shopman-nav">
        <li class="shopman-nav-item">
            <a href="javascript:void(0)" class="shopman-nav-link text-muted">Tìm kiếm hàng hóa</a>
        </li>
        <li class="shopman-nav-item">
            <a href="javascript:void(0)" class="shopman-nav-link text-muted">Đặt hàng</a>
        </li>
        <li class="shopman-nav-item active">
            <a href="${pageContext.request.contextPath}/customer/InvoiceTrackingMenu8.jsp" class="shopman-nav-link active">
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
        <c:choose>
            <c:when test="${not empty dbError}">
                <div class="alert alert-danger text-center my-4" role="alert">
                    <c:out value="${dbError}"/>
                </div>
            </c:when>
            <c:when test="${empty invoiceRows}">
                <div class="alert alert-info text-center my-4" role="alert">
                    Danh sách hóa đơn đang đặt trống.
                </div>
            </c:when>
            <c:otherwise>
                <table class="shopman-table">
                    <thead>
                        <tr>
                            <th style="width: 8%;">STT</th>
                            <th style="width: 15%;">Mã</th>
                            <th style="width: 20%;">Ngày tạo</th>
                            <th style="width: 20%;">Tổng tiền</th>
                            <th style="width: 20%;">Trạng thái</th>
                            <th style="width: 17%;"></th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="row" items="${invoiceRows}" varStatus="loop">
                            <c:url var="detailUrl" value="/customer/InvoiceDetailView8.jsp">
                                <c:param name="invoiceId" value="${row.id}"/>
                            </c:url>
                            <tr>
                                <td class="text-center"><c:out value="${loop.index + 1}"/></td>
                                <td class="text-center fw-bold"><c:out value="${row.code}"/></td>
                                <td class="text-center"><c:out value="${row.createdDate}"/></td>
                                <td class="text-center"><c:out value="${row.totalAmount}"/></td>
                                <td class="text-center"><c:out value="${row.status}"/></td>
                                <td class="text-center">
                                    <a href="<c:out value='${detailUrl}'/>" class="btn-view-detail">
                                        Xem chi tiết
                                    </a>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>

        <a href="${pageContext.request.contextPath}/customer/CustomerMainMenu8.jsp" class="btn-back">
            Quay lại
        </a>
    </div>
</div>
</body>
</html>

