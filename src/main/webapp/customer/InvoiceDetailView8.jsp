<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="dao.DataAccessException8, dao.InvoiceDAO8, dao.InvoiceDetailDAO8, model.Customer8, model.Invoice8, model.InvoiceDetail8, util.DateUtil8" %>
<%@ page import="java.util.ArrayList, java.util.HashMap, java.util.List, java.util.Map" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    Object memberObj = session.getAttribute("member");
    if (!(memberObj instanceof Customer8)) {
        response.sendRedirect(request.getContextPath() + "/member/LoginView8.jsp");
        return;
    }
    Customer8 customer = (Customer8) memberObj;

    String invoiceIdParam = request.getParameter("invoiceId");
    Invoice8 invoice = null;
    InvoiceDetail8[] details = null;
    String errorMessage = null;
    List<Map<String, Object>> detailRows = new ArrayList<>();

    if (invoiceIdParam == null || invoiceIdParam.trim().isEmpty()) {
        errorMessage = "Mã hóa đơn không hợp lệ!";
    } else {
        try {
            int invoiceId = Integer.parseInt(invoiceIdParam.trim());
            if (invoiceId <= 0) {
                errorMessage = "Mã hóa đơn không hợp lệ!";
            } else {
                InvoiceDAO8 invoiceDAO = new InvoiceDAO8();
                invoice = invoiceDAO.getInvoice(invoiceId, customer.getId());

                if (invoice != null) {
                    InvoiceDetailDAO8 detailDAO = new InvoiceDetailDAO8();
                    details = detailDAO.getInvoiceDetail(invoiceId, customer.getId());
                    invoice.setDetails(details);

                    request.setAttribute("invoiceCode", String.format("HD%02d", invoice.getId()));
                    request.setAttribute("invoiceDate", DateUtil8.formatDate(invoice.getCreatedDate()));
                    request.setAttribute("invoiceStatus", invoice.getStatus() != null ? invoice.getStatus() : "");
                    request.setAttribute("invoiceTotal", String.format("%.0f", invoice.getTotalAmount()));
                    request.setAttribute("salesStaffName",
                            (invoice.getSalesStaff() != null && invoice.getSalesStaff().getName() != null)
                                    ? invoice.getSalesStaff().getName() : "Chưa phân công");
                    request.setAttribute("deliveryStaffName",
                            (invoice.getDeliveryStaff() != null && invoice.getDeliveryStaff().getName() != null)
                                    ? invoice.getDeliveryStaff().getName() : "Chưa phân công");

                    if (details != null) {
                        for (InvoiceDetail8 item : details) {
                            Map<String, Object> row = new HashMap<>();
                            row.put("prodCode", item.getProduct() != null ? String.format("MH%02d", item.getProduct().getId()) : "-");
                            row.put("prodName", (item.getProduct() != null && item.getProduct().getName() != null) ? item.getProduct().getName() : "-");
                            row.put("unitPrice", String.format("%.0f", item.getUnitPrice()));
                            row.put("quantity", item.getQuantity());
                            row.put("amount", String.format("%.0f", item.getAmount()));
                            detailRows.add(row);
                        }
                    }
                } else {
                    errorMessage = "Hóa đơn không tồn tại hoặc bạn không có quyền truy cập vào hóa đơn này!";
                }
            }
        } catch (NumberFormatException e) {
            errorMessage = "Định dạng mã hóa đơn không hợp lệ!";
        } catch (DataAccessException8 e) {
            errorMessage = "Hệ thống đang gặp sự cố khi tải thông tin hóa đơn. Vui lòng thử lại sau.";
        }
    }
    request.setAttribute("errorMessage", errorMessage);
    request.setAttribute("hasInvoice", invoice != null);
    request.setAttribute("detailRows", detailRows);
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>InvoiceDetailView8 - Chi tiết hóa đơn</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/style.css" rel="stylesheet">
</head>
<body>
<div class="shopman-window">
    <div class="shopman-header">
        InvoiceDetailView8 &nbsp;-&nbsp; Chi tiết hóa đơn
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
            <c:when test="${not empty errorMessage}">
                <div class="alert alert-danger" role="alert">
                    <c:out value="${errorMessage}"/>
                </div>
            </c:when>
            <c:when test="${hasInvoice}">
                <div>
                    <div class="info-box">
                        Mã hóa đơn: <strong><c:out value="${invoiceCode}"/></strong>
                    </div>
                    <div class="info-box">
                        Ngày tạo: <c:out value="${invoiceDate}"/>
                    </div>
                </div>
                <div>
                    <div class="info-box">
                        Trạng thái: <span class="status-highlight"><c:out value="${invoiceStatus}"/></span>
                    </div>
                </div>
                <div>
                    <div class="info-box">
                        NV bán hàng: <c:out value="${salesStaffName}"/>
                    </div>
                    <div class="info-box">
                        NV giao hàng: <c:out value="${deliveryStaffName}"/>
                    </div>
                </div>

                <h6 class="fw-bold mt-4 mb-2">Danh sách mặt hàng</h6>
                <table class="shopman-table">
                    <thead>
                        <tr>
                            <th style="width: 8%;">STT</th>
                            <th style="width: 15%;">Mã</th>
                            <th style="width: 32%;">Tên</th>
                            <th style="width: 15%;">Đơn giá</th>
                            <th style="width: 15%;">Số lượng</th>
                            <th style="width: 15%;">Thành tiền</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${not empty detailRows}">
                                <c:forEach var="item" items="${detailRows}" varStatus="loop">
                                    <tr>
                                        <td class="text-center"><c:out value="${loop.index + 1}"/></td>
                                        <td class="text-center fw-bold"><c:out value="${item.prodCode}"/></td>
                                        <td><c:out value="${item.prodName}"/></td>
                                        <td class="text-center"><c:out value="${item.unitPrice}"/></td>
                                        <td class="text-center"><c:out value="${item.quantity}"/></td>
                                        <td class="text-center"><c:out value="${item.amount}"/></td>
                                    </tr>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <tr>
                                    <td colspan="6" class="text-center text-muted">Không có chi tiết hàng hóa nào.</td>
                                </tr>
                            </c:otherwise>
                        </c:choose>
                        <tr class="total-row">
                            <td></td>
                            <td></td>
                            <td class="fw-bold">Tổng tiền</td>
                            <td></td>
                            <td></td>
                            <td class="text-center fw-bold"><c:out value="${invoiceTotal}"/></td>
                        </tr>
                    </tbody>
                </table>
            </c:when>
        </c:choose>

        <a href="${pageContext.request.contextPath}/customer/CustomerMainMenu8.jsp" class="btn-back">
            Quay lại
        </a>
    </div>
</div>
</body>
</html>

