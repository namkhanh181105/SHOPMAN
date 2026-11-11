package util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * Tiện ích xử lý định dạng ngày tháng hiển thị và chuyển đổi ngày SQL chặt chẽ.
 */
public class DateUtil8 {
    private static final DateTimeFormatter DISPLAY_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter ISO_FORMATTER =
            DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT);
    private static final Pattern ISO_REGEX = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");
    private static final Pattern DISPLAY_REGEX = Pattern.compile("^\\d{2}/\\d{2}/\\d{4}$");

    private static LocalDate toLocalDate(Date date) {
        if (date instanceof java.sql.Date) {
            return ((java.sql.Date) date).toLocalDate();
        }
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * Định dạng Date sang chuỗi hiển thị theo định dạng dd/MM/yyyy (như trong báo cáo).
     */
    public static String formatDate(Date date) {
        if (date == null) {
            return "";
        }
        return toLocalDate(date).format(DISPLAY_FORMATTER);
    }

    /**
     * Chuyển đổi chuỗi ngày nghiêm ngặt (chỉ chấp nhận toàn bộ chuỗi dd/MM/yyyy hoặc yyyy-MM-dd
     * và ngày tồn tại thực trên lịch) sang java.sql.Date; trả về null nếu không hợp lệ.
     */
    public static java.sql.Date parseDate(String dateStr) {
        if (dateStr == null) {
            return null;
        }
        String trimmed = dateStr.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        try {
            LocalDate parsed;
            if (ISO_REGEX.matcher(trimmed).matches()) {
                parsed = LocalDate.parse(trimmed, ISO_FORMATTER);
            } else if (DISPLAY_REGEX.matcher(trimmed).matches()) {
                parsed = LocalDate.parse(trimmed, DISPLAY_FORMATTER);
            } else {
                return null;
            }
            return java.sql.Date.valueOf(parsed);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Định dạng Date sang chuỗi ISO yyyy-MM-dd (cho value của thẻ <input type="date">).
     */
    public static String toIsoString(Date date) {
        if (date == null) {
            return "";
        }
        return toLocalDate(date).format(ISO_FORMATTER);
    }
}

