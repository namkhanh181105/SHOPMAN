<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="dao.DataAccessException8, dao.ImportInvoiceDAO8, dao.ImportInvoiceDetailDAO8, model.ImportInvoice8, model.ImportInvoiceDetail8, model.Manager8, util.DateUtil8" %>
<%@ page import="java.util.ArrayList, java.util.Date, java.util.HashMap, java.util.List, java.util.Map" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    Object memberObj = session.getAttribute("member");
    if (!(memberObj instanceof Manager8)) {
        response.sendRedirect(request.getContextPath() + "/member/LoginView8.jsp");
        return;
    }

    String invoiceIdParam = request.getParameter("importInvoiceId");
    String supplierIdParam = request.getParameter("supplierId");
    String startParam = request.getParameter("startDate");
    String endParam = request.getParameter("endDate");

    ImportInvoice8 invoice = null;
    String error = null;
    List<Map<String, Object>> detailRows = new ArrayList<>();

    Integer validBackSupplierId = null;
    String validBackStart = null;
    String validBackEnd = null;
    if (supplierIdParam != null && startParam != null && endParam != null) {
        try {
            int parsedSuppId = Integer.parseInt(supplierIdParam.trim());
            Date parsedStart = DateUtil8.parseDate(startParam);
            Date parsedEnd = DateUtil8.parseDate(endParam);
            if (parsedSuppId > 0 && parsedStart != null && parsedEnd != null && !parsedStart.after(parsedEnd)) {
                validBackSupplierId = parsedSuppId;
                validBackStart = DateUtil8.toIsoString(parsedStart);
                validBackEnd = DateUtil8.toIsoString(parsedEnd);
            }
        } catch (NumberFormatException ignored) {
        }
    }

    if (invoiceIdParam == null || invoiceIdParam.trim().isEmpty()) {
        error = "Mã hóa đơn nhập không hợp lệ!";
    } else {
        try {
            int importInvoiceId = Integer.parseInt(invoiceIdParam.trim());
            if (importInvoiceId <= 0) {
                error = "Mã hóa đơn nhập không hợp lệ!";
            } else {
                ImportInvoiceDAO8 importDAO = new ImportInvoiceDAO8();
                invoice = importDAO.getImportInvoice(importInvoiceId);

                if (invoice != null) {
                    ImportInvoiceDetailDAO8 detailDAO = new ImportInvoiceDetailDAO8();
                    ImportInvoiceDetail8[] details = detailDAO.getImportInvoiceDetail(importInvoiceId);
                    invoice.setDetails(details);

                    request.setAttribute("invoiceCode", String.format("NH%02d", invoice.getId()));
                    request.setAttribute("invoiceDate", DateUtil8.formatDate(invoice.getCreatedDate()));
                    request.setAttribute("invoiceStatus", invoice.getStatus() != null ? invoice.getStatus() : "");
                    request.setAttribute("supplierName",
                            (invoice.getSupplier() != null && invoice.getSupplier().getName() != null)
                                    ? invoice.getSupplier().getName() : "-");
                    request.setAttribute("totalQty", invoice.getTotalQuantity());
                    request.setAttribute("totalAmount", String.format("%.0f", invoice.getTotalAmount()));

                    if (details != null) {
                        for (ImportInvoiceDetail8 item : details) {
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
                    error = "Hóa đơn nhập không tồn tại!";
                }
            }
        } catch (NumberFormatException e) {
            error = "Định dạng mã hóa đơn nhập không hợp lệ!";
        } catch (DataAccessException8 e) {
            error = "Hệ thống đang gặp sự cố khi tải hóa đơn nhập hàng. Vui lòng thử lại sau.";
        }
    }

    request.setAttribute("error", error);
    request.setAttribute("hasInvoice", invoice != null);
    request.setAttribute("detailRows", detailRows);
    request.setAttribute("validBackSupplierId", validBackSupplierId);
    request.setAttribute("validBackStart", validBackStart);
    request.setAttribute("validBackEnd", validBackEnd);
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ImportInvoiceDetailView8 - Hóa đơn nhập hàng</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/style.css" rel="stylesheet">
</head>
<body>
<div class="shopman-window">
    <div class="shopman-header">
        ImportInvoiceDetailView8 &nbsp;-&nbsp; Hóa đơn nhập hàng
    </div>
    <ul class="shopman-nav">
        <li class="shopman-nav-item">
            <a href="javascript:void(0)" class="shopman-nav-link text-muted">Nhập hàng</a>
        </li>
        <li class="shopman-nav-item">
            <a href="javascript:void(0)" class="shopman-nav-link text-muted">Quản lý thông tin</a>
        </li>
        <li class="shopman-nav-item active">
            <a href="${pageContext.request.contextPath}/manager/SupplierImportStatisticsView8.jsp" class="shopman-nav-link active">
                Xem thống kê
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
            <c:when test="${not empty error}">
                <div class="alert alert-danger" role="alert">
                    <c:out value="${error}"/>
                </div>
            </c:when>
            <c:when test="${hasInvoice}">
                <div>
                    <div class="info-box">
                        Mã hóa đơn nhập: <strong><c:out value="${invoiceCode}"/></strong>
                    </div>
                    <div class="info-box">
                        Ngày tạo: <c:out value="${invoiceDate}"/>
                    </div>
                </div>
                <div>
                    <div class="info-box">
                        Trạng thái: <span class="status-highlight"><c:out value="${invoiceStatus}"/></span>
                    </div>
                    <div class="info-box">
                        Nhà cung cấp: <strong><c:out value="${supplierName}"/></strong>
                    </div>
                </div>

                <h6 class="fw-bold mt-4 mb-2">Danh sách mặt hàng nhập</h6>
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
                                    <td colspan="6" class="text-center text-muted">Không có mặt hàng nào.</td>
                                </tr>
                            </c:otherwise>
                        </c:choose>
                        <tr class="total-row">
                            <td></td>
                            <td></td>
                            <td class="fw-bold">Tổng</td>
                            <td></td>
                            <td class="text-center fw-bold"><c:out value="${totalQty}"/></td>
                            <td class="text-center fw-bold"><c:out value="${totalAmount}"/></td>
                        </tr>
                    </tbody>
                </table>
            </c:when>
        </c:choose>

        <div class="d-flex justify-content-between align-items-center mt-auto">
            <a href="${pageContext.request.contextPath}/manager/ManagerMainMenu8.jsp" class="btn-back">
                Quay lại
            </a>
            <c:if test="${not empty validBackSupplierId and not empty validBackStart and not empty validBackEnd}">
                <c:url var="backHistoryUrl" value="/manager/ImportHistoryView8.jsp">
                    <c:param name="supplierId" value="${validBackSupplierId}"/>
                    <c:param name="startDate" value="${validBackStart}"/>
                    <c:param name="endDate" value="${validBackEnd}"/>
                </c:url>
                <a href="<c:out value='${backHistoryUrl}'/>" class="btn btn-outline-secondary btn-sm">
                    &larr; Xem lại các lần nhập của NCC
                </a>
            </c:if>
        </div>
    </div>
</div>
</body>
</html>

