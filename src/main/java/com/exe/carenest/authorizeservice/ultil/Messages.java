package com.exe.carenest.authorizeservice.ultil;

import lombok.Getter;

/**
 * Enum chứa các thông báo lỗi (messages) cho ApiException.
 * Mỗi giá trị enum đại diện cho một loại lỗi, với code, message mặc định và HTTP status.
 * Sử dụng khi throw ApiException, ví dụ:
 * throw new ApiException(Messages.USER_NOT_FOUND.getCode(), Messages.USER_NOT_FOUND.getMessage(), 404);
 */
@Getter
public enum Messages {
    // Lỗi liên quan đến người dùng
    USER_NOT_FOUND("USER_NOT_FOUND", "Không tìm thấy người dùng", 404),
    USER_ALREADY_EXISTS("USER_ALREADY_EXISTS", "Người dùng đã tồn tại", 409),
    MAIL_ALREADY_LINKED("MAIL_ALREADY_LINKED", "Email này đã được liên kết với 1 tài khoản khác", 409),

    // Lỗi liên quan đến mật khẩu
    INVALID_PASSWORD("INVALID_PASSWORD", "Mật khẩu không hợp lệ hoặc sai", 400),
    PASSWORD_MISMATCH("PASSWORD_MISMATCH", "Mật khẩu nhập lại không khớp", 400),

    // Lỗi xác thực và ủy quyền
    INVALID_TOKEN("INVALID_TOKEN", "Token không hợp lệ hoặc hết hạn", 401),
    UNAUTHORIZED("UNAUTHORIZED", "Không được ủy quyền truy cập", 401),
    FORBIDDEN("FORBIDDEN", "Quyền truy cập bị cấm", 403),

    // Lỗi chung
    INTERNAL_SERVER_ERROR("INTERNAL_ERROR", "Lỗi hệ thống nội bộ", 500),
    BAD_REQUEST("BAD_REQUEST", "Yêu cầu không hợp lệ", 400);

    private final String code;
    private final String message;
    private final int status;

    Messages(String code, String message, int status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
}