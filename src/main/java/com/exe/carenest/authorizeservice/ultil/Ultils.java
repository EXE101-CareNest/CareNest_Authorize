package com.exe.carenest.authorizeservice.ultil;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Random;

public class Ultils {
    public static String generateCode() {
        LocalDate today = LocalDate.now();

        // Lấy 2 chữ cái đầu ngày trong tuần (English)
        String day = today.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
        day = day.substring(0, 2).toUpperCase();

        // Lấy 2 chữ cái đầu tháng (English)
        String month = today.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
        month = month.substring(0, 2).toUpperCase();

        // Lấy năm đầy đủ
        int year = today.getYear();

        // Tạo số ngẫu nhiên 4 chữ số
        Random random = new Random();
        int randomNumber = random.nextInt(9000) + 1000; // từ 1000 đến 9999

        // Tạo hash ký tự ngẫu nhiên 3 chữ cái (A-Z)
        String hash = random.ints(3, 65, 91) // 65-90 là mã ASCII A-Z
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
        // Ghép chuỗi theo yêu cầu
        return day + month  + year  + hash + randomNumber ;
    }
}
