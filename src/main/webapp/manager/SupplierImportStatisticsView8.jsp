<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="dao.DataAccessException8, dao.SupplierDAO8, model.Manager8, model.SupplierImportStatistics8, util.DateUtil8" %>
<%@ page import="java.util.ArrayList, java.util.Date, java.util.HashMap, java.util.List, java.util.Map" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    Object memberObj = session.getAttribute("member");
    if (!(memberObj instanceof Manager8)) {
        response.sendRedirect(request.getContextPath() + "/member/LoginView8.jsp");
        return;
    }

    String submittedParam = request.getParameter("submitted");
    boolean submitted = "true".equalsIgnoreCase(submittedParam);

    String startParam = request.getParameter("startDate");
    String endParam = request.getParameter("endDate");

    // Khi mới mở trang (chưa gửi bộ lọc), điền sẵn ngày gợi ý nhưng không gọi DAO
    String startInputVal = (startParam != null && !startParam.trim().isEmpty()) ? startParam.trim() : (submitted ? "" : "2026-05-01");
    String endInputVal = (endParam != null && !endParam.trim().isEmpty()) ? endParam.trim() : (submitted ? "" : "2026-07-31");

    String dateError = null;
    String queryError = null;
    String isoStart = "";
    String isoEnd = "";
    List<Map<String, Object>> statRows = new ArrayList<>();

    if (submitted) {
        if (startParam == null || startParam.trim().isEmpty() || endParam == null || endParam.trim().isEmpty()) {
            dateError = "Vui lòng chọn đầy đủ ngày bắt đầu và ngày kết thúc!";
        } else {
            Date startDate = DateUtil8.parseDate(startParam);
            Date endDate = DateUtil8.parseDate(endParam);
            if (startDate == null || endDate == null) {
                dateError = "Định dạng ngày không hợp lệ hoặc ngày không tồn tại! Vui lòng chọn lại.";
            } else if (startDate.after(endDate)) {
                isoStart = DateUtil8.toIsoString(startDate);
                isoEnd = DateUtil8.toIsoString(endDate);
                startInputVal = isoStart;
                endInputVal = isoEnd;
                dateError = "Ngày bắt đầu không được lớn hơn ngày kết thúc!";
            } else {
                isoStart = DateUtil8.toIsoString(startDate);
                isoEnd = DateUtil8.toIsoString(endDate);
                startInputVal = isoStart;
                endInputVal = isoEnd;
                try {
                    SupplierDAO8 supplierDAO = new SupplierDAO8();
                    SupplierImportStatistics8[] stats = supplierDAO.getSupplierImportStatistics(startDate, endDate);
                    if (stats != null) {
                        for (SupplierImportStatistics8 item : stats) {
                            Map<String, Object> row = new HashMap<>();
                            row.put("id", item.getId());
                            row.put("code", String.format("NCC%02d", item.getId()));
                            row.put("name", item.getName() != null ? item.getName() : "");
                            row.put("totalQuantity", item.getTotalQuantity());
                            row.put("latestDate", DateUtil8.formatDate(item.getLastImportDate()));
                            statRows.add(row);
                        }
                    }
                } catch (IllegalArgumentException e) {
                    dateError = "Khoảng thời gian thống kê không hợp lệ!";
                } catch (DataAccessException8 e) {
                    queryError = "Hệ thống đang gặp sự cố khi truy vấn thống kê. Vui lòng thử lại sau.";
                }
            }
        }
    }

    request.setAttribute("submitted", submitted);
    request.setAttribute("startInputVal", startInputVal);
    request.setAttribute("endInputVal", endInputVal);
    request.setAttribute("isoStart", isoStart);
    request.setAttribute("isoEnd", isoEnd);
    request.setAttribute("dateError", dateError);
    request.setAttribute("queryError", queryError);
    request.setAttribute("statRows", statRows);
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>SupplierImportStatisticsView8 - Thống kê nhà cung cấp</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/style.css" rel="stylesheet">
    <script src="${pageContext.request.contextPath}/assets/js/script.js"></script>
</head>
<body>
<div class="shopman-window">
    <div class="shopman-header">
        SupplierImportStatisticsView8 &nbsp;-&nbsp; Thống kê nhà cung cấp
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
        <form action="${pageContext.request.contextPath}/manager/SupplierImportStatisticsView8.jsp" method="get" onsubmit="return validateDateRange();" class="mb-4">
            <input type="hidden" name="submitted" value="true">
            <div class="row align-items-center">
                <div class="col-md-7">
                    <div class="d-flex align-items-center mb-2">
                        <label for="startDate" class="form-label mb-0 fw-bold" style="width: 140px;">Ngày bắt đầu</label>
                        <input type="date" class="form-control form-control-sm" id="startDate" name="startDate"
                               value="<c:out value='${startInputVal}'/>" required style="max-width: 200px; border: 1.5px solid #1a202c;">
                    </div>
                    <div class="d-flex align-items-center">
                        <label for="endDate" class="form-label mb-0 fw-bold" style="width: 140px;">Ngày kết thúc</label>
                        <input type="date" class="form-control form-control-sm" id="endDate" name="endDate"
                               value="<c:out value='${endInputVal}'/>" required style="max-width: 200px; border: 1.5px solid #1a202c;">
                    </div>
                </div>
                <div class="col-md-5 text-end">
                    <button type="submit" class="btn-statistic">
                        Xem thống kê
                    </button>
                </div>
            </div>
        </form>

        <c:choose>
            <c:when test="${not empty dateError}">
                <div class="alert alert-warning text-center" role="alert">
                    <c:out value="${dateError}"/>
                </div>
            </c:when>
            <c:when test="${not empty queryError}">
                <div class="alert alert-danger text-center" role="alert">
                    <c:out value="${queryError}"/>
                </div>
            </c:when>
            <c:when test="${not submitted}">
                <div class="alert alert-secondary text-center my-4" role="alert">
                    Chưa thực hiện truy vấn. Vui lòng chọn khoảng ngày và nhấn <strong>"Xem thống kê"</strong>.
                </div>
            </c:when>
            <c:when test="${empty statRows}">
                <div class="alert alert-info text-center my-4" role="alert">
                    Không có dữ liệu thống kê trong khoảng thời gian đã chọn.
                </div>
            </c:when>
            <c:otherwise>
                <table class="shopman-table">
                    <thead>
                        <tr>
                            <th style="width: 8%;">STT</th>
                            <th style="width: 15%;">Mã</th>
                            <th style="width: 27%;">Tên NCC</th>
                            <th style="width: 20%;">Số lượng hàng nhập</th>
                            <th style="width: 18%;">Ngày nhập gần nhất</th>
                            <th style="width: 12%;"></th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="item" items="${statRows}" varStatus="loop">
                            <c:url var="historyUrl" value="/manager/ImportHistoryView8.jsp">
                                <c:param name="supplierId" value="${item.id}"/>
                                <c:param name="startDate" value="${isoStart}"/>
                                <c:param name="endDate" value="${isoEnd}"/>
                            </c:url>
                            <tr>
                                <td class="text-center"><c:out value="${loop.index + 1}"/></td>
                                <td class="text-center fw-bold"><c:out value="${item.code}"/></td>
                                <td><c:out value="${item.name}"/></td>
                                <td class="text-center fw-bold"><c:out value="${item.totalQuantity}"/></td>
                                <td class="text-center"><c:out value="${item.latestDate}"/></td>
                                <td class="text-center">
                                    <a href="<c:out value='${historyUrl}'/>" class="btn-view-detail">
                                        Xem chi tiết
                                    </a>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>

        <a href="${pageContext.request.contextPath}/manager/ManagerMainMenu8.jsp" class="btn-back">
            Quay lại
        </a>
    </div>
</div>
</body>
</html>

