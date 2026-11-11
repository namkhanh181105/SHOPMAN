/**
 * ShopMan JavaScript - Tiện ích kiểm tra dữ liệu phía Client
 */

function validateDateRange() {
    var startDateInput = document.getElementById("startDate");
    var endDateInput = document.getElementById("endDate");

    if (!startDateInput || !endDateInput) {
        return true;
    }

    var startVal = startDateInput.value.trim();
    var endVal = endDateInput.value.trim();

    if (!startVal) {
        alert("Vui lòng chọn ngày bắt đầu!");
        startDateInput.focus();
        return false;
    }

    if (!endVal) {
        alert("Vui lòng chọn ngày kết thúc!");
        endDateInput.focus();
        return false;
    }

    var start = new Date(startVal);
    var end = new Date(endVal);

    if (start > end) {
        alert("Ngày bắt đầu không được lớn hơn ngày kết thúc!");
        startDateInput.focus();
        return false;
    }

    return true;
}

