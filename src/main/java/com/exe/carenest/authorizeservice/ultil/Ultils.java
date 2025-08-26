package com.exe.carenest.authorizeservice.ultil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Random;

public class Ultils {
    public static String generateCode() {
        LocalDateTime now = LocalDateTime.now();

        // Lấy 2 chữ cái đầu ngày trong tuần (English)
        String day = now.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH).substring(0, 2).toUpperCase();

        // Lấy 2 chữ cái đầu tháng (English)
        String month = now.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH).substring(0, 2).toUpperCase();

        // Lấy năm đầy đủ 4 số
        String year = String.valueOf(now.getYear());

        // Lấy giờ phút giây dạng 6 số để tăng độ duy nhất
        String time = now.format(DateTimeFormatter.ofPattern("HHmmss"));

        // Tạo hash ký tự ngẫu nhiên 3 chữ cái (A-Z)
        Random random = new Random();
        String hash = random.ints(3, 65, 91)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        // Ghép chuỗi id theo format: DayMonthYear + thời gian + hash ngẫu nhiên
        String id = day + month + year + time + hash;

        return id;
    }
}
