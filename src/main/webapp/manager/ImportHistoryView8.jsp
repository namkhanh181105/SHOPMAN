<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="dao.DataAccessException8, dao.ImportInvoiceDAO8, model.ImportInvoice8, model.Manager8, util.DateUtil8" %>
<%@ page import="java.util.ArrayList, java.util.Date, java.util.HashMap, java.util.List, java.util.Map" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    Object memberObj = session.getAttribute("member");
    if (!(memberObj instanceof Manager8)) {
        response.sendRedirect(request.getContextPath() + "/member/LoginView8.jsp");
        return;
    }

    String supplierIdParam = request.getParameter("supplierId");
    String startParam = request.getParameter("startDate");
    String endParam = request.getParameter("endDate");

    String supplierDisplay = "";
    String error = null;
    Integer validSupplierId = null;
    String isoStart = null;
    String isoEnd = null;
    List<Map<String, Object>> historyRows = new ArrayList<>();

    if (supplierIdParam == null || supplierIdParam.trim().isEmpty()
            || startParam == null || startParam.trim().isEmpty()
            || endParam == null || endParam.trim().isEmpty()) {
        error = "Thiếu tham số truy vấn nhà cung cấp hoặc khoảng thời gian!";
    } else {
        try {
            int supplierId = Integer.parseInt(supplierIdParam.trim());
            Date startDate = DateUtil8.parseDate(startParam);
            Date endDate = DateUtil8.parseDate(endParam);

            if (supplierId <= 0) {
                error = "Mã nhà cung cấp không hợp lệ!";
            } else if (startDate == null || endDate == null) {
                error = "Khoảng thời gian không hợp lệ!";
            } else if (startDate.after(endDate)) {
                error = "Ngày bắt đầu phải nhỏ hơn hoặc bằng ngày kết thúc!";
            } else {
                validSupplierId = supplierId;
                isoStart = DateUtil8.toIsoString(startDate);
                isoEnd = DateUtil8.toIsoString(endDate);

                ImportInvoiceDAO8 importDAO = new ImportInvoiceDAO8();
                ImportInvoice8[] history = importDAO.getImportHistory(supplierId, startDate, endDate);

                supplierDisplay = "NCC #" + supplierId;
                if (history != null && history.length > 0) {
                    if (history[0].getSupplier() != null && history[0].getSupplier().getName() != null) {
                        supplierDisplay = history[0].getSupplier().getName();
                    }
                    for (ImportInvoice8 inv : history) {
                        Map<String, Object> row = new HashMap<>();
                        row.put("id", inv.getId());
                        row.put("code", String.format("NH%02d", inv.getId()));
                        row.put("supplierName", inv.getSupplier() != null && inv.getSupplier().getName() != null ? inv.getSupplier().getName() : "");
                        row.put("totalQuantity", inv.getTotalQuantity());
                        row.put("createdDate", DateUtil8.formatDate(inv.getCreatedDate()));
                        row.put("totalAmount", String.format("%.0f", inv.getTotalAmount()));
                        historyRows.add(row);
                    }
                }
            }
        } catch (NumberFormatException e) {
            error = "Mã nhà cung cấp không hợp lệ!";
        } catch (IllegalArgumentException e) {
            error = "Tham số nhà cung cấp hoặc khoảng thời gian không hợp lệ!";
        } catch (DataAccessException8 e) {
            error = "Hệ thống đang gặp sự cố khi tải lịch sử nhập hàng. Vui lòng thử lại sau.";
        }
    }

    request.setAttribute("error", error);
    request.setAttribute("supplierDisplay", supplierDisplay);
    request.setAttribute("validSupplierId", validSupplierId);
    request.setAttribute("isoStart", isoStart);
    request.setAttribute("isoEnd", isoEnd);
    request.setAttribute("historyRows", historyRows);
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ImportHistoryView8 - Các lần nhập hàng của nhà cung cấp</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/style.css" rel="stylesheet">
</head>
<body>
<div class="shopman-window">
    <div class="shopman-header">
        ImportHistoryView8 &nbsp;-&nbsp; Các lần nhập hàng của nhà cung cấp
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
            <c:otherwise>
                <div class="mb-3">
                    <div class="info-box">
                        Nhà cung cấp: <strong><c:out value="${supplierDisplay}"/></strong>
                    </div>
                </div>

                <c:choose>
                    <c:when test="${empty historyRows}">
                        <div class="alert alert-info text-center my-4" role="alert">
                            Không có lần nhập hàng nào trong khoảng thời gian đã chọn.
                        </div>
                    </c:when>
                    <c:otherwise>
                        <table class="shopman-table">
                            <thead>
                                <tr>
                                    <th style="width: 8%;">STT</th>
                                    <th style="width: 15%;">Mã</th>
                                    <th style="width: 25%;">Tên NCC</th>
                                    <th style="width: 15%;">Số lượng</th>
                                    <th style="width: 17%;">Ngày nhập</th>
                                    <th style="width: 20%;">Tổng tiền</th>
                                    <th style="width: 15%;"></th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="inv" items="${historyRows}" varStatus="loop">
                                    <c:url var="detailUrl" value="/manager/ImportInvoiceDetailView8.jsp">
                                        <c:param name="importInvoiceId" value="${inv.id}"/>
                                        <c:param name="supplierId" value="${validSupplierId}"/>
                                        <c:param name="startDate" value="${isoStart}"/>
                                        <c:param name="endDate" value="${isoEnd}"/>
                                    </c:url>
                                    <tr>
                                        <td class="text-center"><c:out value="${loop.index + 1}"/></td>
                                        <td class="text-center fw-bold"><c:out value="${inv.code}"/></td>
                                        <td><c:out value="${inv.supplierName}"/></td>
                                        <td class="text-center"><c:out value="${inv.totalQuantity}"/></td>
                                        <td class="text-center"><c:out value="${inv.createdDate}"/></td>
                                        <td class="text-center"><c:out value="${inv.totalAmount}"/></td>
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
            </c:otherwise>
        </c:choose>

        <c:choose>
            <c:when test="${not empty isoStart and not empty isoEnd}">
                <c:url var="backStatsUrl" value="/manager/SupplierImportStatisticsView8.jsp">
                    <c:param name="submitted" value="true"/>
                    <c:param name="startDate" value="${isoStart}"/>
                    <c:param name="endDate" value="${isoEnd}"/>
                </c:url>
                <a href="<c:out value='${backStatsUrl}'/>" class="btn-back">
                    Quay lại
                </a>
            </c:when>
            <c:otherwise>
                <a href="${pageContext.request.contextPath}/manager/SupplierImportStatisticsView8.jsp" class="btn-back">
                    Quay lại
                </a>
            </c:otherwise>
        </c:choose>
    </div>
</div>
</body>
</html>

