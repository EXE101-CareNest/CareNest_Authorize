package com.exe.carenest.authorizeservice.ultil;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import com.exe.carenest.authorizeservice.exception.ApiException;
import com.exe.carenest.authorizeservice.ultil.Messages;

public class DateUtil {

    /**
     * Chuyển đổi String ngày tháng sang Timestamp.
     * @param dateStr String ngày tháng (ví dụ: "1995-08-15")
     * @param format Định dạng (mặc định: "yyyy-MM-dd")
     * @return Timestamp nếu hợp lệ, throw ApiException nếu lỗi
     */
    public static Timestamp stringToTimestamp(String dateStr, String format) {
        if (dateStr == null || dateStr.isEmpty()) {
            throw new ApiException(Messages.BAD_REQUEST.getCode(), "Ngày tháng không được để trống", Messages.BAD_REQUEST.getStatus());
        }

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(format);
            java.util.Date parsedDate = dateFormat.parse(dateStr);
            return new Timestamp(parsedDate.getTime());
        } catch (ParseException e) {
            throw new ApiException(Messages.BAD_REQUEST.getCode(), "Định dạng ngày tháng không hợp lệ (yêu cầu: " + format + ")", Messages.BAD_REQUEST.getStatus());
        }
    }

    // Overload cho format mặc định
    public static Timestamp stringToTimestamp(String dateStr) {
        return stringToTimestamp(dateStr, "yyyy-MM-dd");  // Mặc định yyyy-MM-dd
    }
}
