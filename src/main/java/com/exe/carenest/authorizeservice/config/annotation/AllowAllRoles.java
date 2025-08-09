package com.exe.carenest.authorizeservice.config.annotation;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.*;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@PreAuthorize("isAuthenticated()") // Hoặc bất kỳ biểu thức nào bạn muốn
public @interface AllowAllRoles {
}
