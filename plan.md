- POST /api/auth/login
  - Input: LoginRequest { username, password }
  - Response: LoginResponse { accessToken, refreshToken, username }
  - AI integration point: KHÔNG gửi mật khẩu cho AI. Có thể dùng AI để gợi ý `login` error messages hoặc generate user-friendly messages for failed logins.
  - Suggested templateId: `auth/login/fail_message` (context: { usernameMasked, reason })

  API Spec (example):
  Request URL: POST /api/auth/login
  Request JSON:
  {
     "username": "",
     "password": ""
  }

  Response JSON (success):
  {
     "accessToken": "<jwt_access_token>",
     "refreshToken": "<refresh_token>",
     "username": "<username>"
  }

# API Spec: Detailed request/response examples for all controller endpoints
Below are concrete JSON request and response examples (the inner `data` payload that will be wrapped by `BaseResponse`) for each endpoint in the `controller` package. Use these samples for front-end or integration testing and for implementing AI context mappings.

---

## AuthController (/api/auth)

1) POST /api/auth/login
Request JSON:
{
  "username": "jdoe",
  "password": "p@ssw0rd"
}

Response data (LoginResponse):
{
  "accessToken": "eyJhbGci...",
  "refreshToken": "rfr3sht0k3n...",
  "username": "jdoe"
}

2) POST /api/auth/logout
Request: Header Authorization: "Bearer <accessToken>"
Response data (String):
"Logged out successfully"

3) GET /api/auth/verify
Request: Header Authorization: "Bearer <accessToken>"
Response data:
{
  "valid": true
}

4) GET /api/auth/authorize?role=ROLE_ADMIN
Request: Header Authorization: "Bearer <accessToken>"
Response data:
{
  "authorized": false
}

5) POST /api/auth/forgot-password
Request JSON:
{
  "email": "user@example.com"
}
Response: Body string (message) and header X-Key-APT: <otp-token>
Body:
"Nếu email tồn tại trong hệ thống, mã OTP đã được gửi"
Header:
X-Key-APT: "otp-token-abc123"

6) POST /api/auth/verify/otp
Request: Header X-Key-APT: "otp-token-abc123"
Request JSON:
{
  "email": "user@example.com",
  "otp": "123456"
}
Response data (on success): String body and response header X-Password-Reset-Token contains reset token
Body:
"OTP xác thực thành công. Bạn có thể đặt lại mật khẩu"
Header:
X-Password-Reset-Token: "pw-reset-token-..."

7) POST /api/auth/newPassword
Request: Header X-Password-Reset-Token: "pw-reset-token-..."
Request JSON:
{
  "email": "user@example.com",
  "password": "newpass1",
  "reEnterPassword": "newpass1"
}
Response data:
"Đặt lại mật khẩu thành công"

8) POST /api/auth/registerVerifyToken
Request: Header X-Key-APT: "otp-token"
Request JSON:
{
  "email": "user@example.com",
  "otp": "123456"
}
Response data:
"Xác thực email thành công. Tài khoản đã được kích hoạt"

9) POST /api/auth/refresh
Request JSON:
{
  "refreshToken": "rfr3sht0k3n..."
}
Response data (TokenResponse):
{
  "accessToken": "newAccessToken...",
  "refreshToken": "newRefreshToken..."
}

10) POST /api/auth/revoke
Request JSON:
{
  "refreshToken": "rfr3sht0k3n..."
}
Response data:
{
  "revoked": true
}

---

## UserAccountController (/api/accounts)

1) POST /api/accounts/register/customer
Request JSON (RegisterRequest):
{
  "username": "jdoe",
  "fullName": "John Doe",
  "birthday": "1990-01-01",
  "gender": "MALE",
  "email": "jdoe@example.com",
  "password": "p@ssw0rd",
  "reEnterPassword": "p@ssw0rd"
}
Response: HTTP 201 Created, header X-Key-APT: "otp-token"
Body: (empty / void)

2) GET /api/accounts/username/{username}
Response data (AccountResponse):
{
  "id": "uuid-123",
  "username": "jdoe",
  "email": "jdoe@example.com",
  "role": "ROLE_USER",
  "isActive": true
}

3) PUT /api/accounts/password
Request JSON (NewPasswordRequest):
{
  "email": "jdoe@example.com",
  "password": "newpass1",
  "reEnterPassword": "newpass1"
}
Response: void (200 OK)

---

## AdminAccountController (/api/admin/accounts)

1) GET /api/admin/accounts
Response data (List<AccountResponse>):
[
  {
    "id": "uuid-1",
    "username": "jdoe",
    "email": "jdoe@example.com",
    "role": "ROLE_USER",
    "isActive": true
  },
  {
    "id": "uuid-2",
    "username": "asmith",
    "email": "asmith@example.com",
    "role": "ROLE_ADMIN",
    "isActive": true
  }
]

2) GET /api/admin/accounts/{id}
Response data (AccountResponse):
{
  "id": "uuid-1",
  "username": "jdoe",
  "email": "jdoe@example.com",
  "role": "ROLE_USER",
  "isActive": true
}

3) DELETE /api/admin/accounts/{id}
Response data (Boolean):
true

4) PUT /api/admin/accounts/{id}/role?role=ROLE_ADMIN
Response data (Boolean):
true

---

## ShopAccountController (/api/shops)

1) POST /api/shops/register
Request JSON (RegisterRequest):
{
  "username": "shopowner",
  "fullName": "Owner Name",
  "birthday": "1985-05-05",
  "gender": "FEMALE",
  "email": "owner@shop.com",
  "password": "shopPass1",
  "reEnterPassword": "shopPass1"
}
Response: 201 Created, header X-Key-APT: "otp-token"

2) POST /api/shops/information
Request JSON (ShopRegistrationRequest):
{
  "shopName": "Nice Shop",
  "description": "We sell quality items",
  "shopPassword": "shopPass1",
  "imgUrl": "https://...",
  "workingDays": "Mon-Fri",
  "shopId": ""
}
Response data (ShopResponse):
{
  "id": 12,
  "shopName": "Nice Shop",
  "owner": "owner name",
  "description": "We sell quality items",
  "status": true,
  "workingDay": "Mon-Fri",
  "workingDays": "",
  "imgUrl": "https://..."
}

---

## PermissionController (/api/permission)

1) POST /api/permission/check?path=/api/orders/123&httpMethod=GET
Request (query): path, httpMethod; Header Authorization required
Response data (PermissionCheckResponse):
{
  "allowed": true
}

---

## ModuleController (/api/modules)

1) GET /api/modules
Response data (List<ModuleFunc>):
[
  { "urlPattern": "/api/orders/**", "name": "Order Module" },
  { "urlPattern": "/api/customers/**", "name": "Customer Module" }
]

2) POST /api/modules
Request JSON (CreateModuleRequest):
{
  "urlPattern": "/api/newmodule/**",
  "name": "New Module"
}
Response data (ModuleFunc):
{
  "urlPattern": "/api/newmodule/**",
  "name": "New Module"
}

3) GET /api/modules/{urlPattern}
Response data (ModuleFunc):
{
  "urlPattern": "/api/orders/**",
  "name": "Order Module"
}

4) DELETE /api/modules/{urlPattern}
Response: void (200 OK)

---

## RolePermissionController (/api/role-permissions)

1) PUT /api/role-permissions/{id}
Request JSON (UpdateRolePermissionRequest):
{
  "httpPermission": "READ"
}
Response data (RolePermission) (example fields):
{
  "id": 56,
  "role": { "id": 3, "name": "ROLE_USER" },
  "module": { "urlPattern": "/api/orders/**", "name": "Order Module" },
  "httpPermission": "READ"
}

2) DELETE /api/role-permissions/{id}
Response: void (200 OK)

3) POST /api/role-permissions/batch-update
Request JSON (BatchUpdatePermissionsRequest):
{
  "roleName": "ROLE_USER",
  "moduleUrlPattern": "/api/orders/**",
  "httpPermissions": ["READ","CREATE"]
}
Response data (String):
"Permissions updated successfully"

4) GET /api/role-permissions/display/{roleName}
Response data (List<RolePermissionDisplayDto>):
[
  {
    "moduleUrlPattern": "/api/orders/**",
    "moduleName": "Order Module",
    "currentPermissions": ["READ"],
    "availablePermissions": ["READ","CREATE","UPDATE","DELETE"]
  }
]

---

## UserRoleController (/api/roles)

1) GET /api/roles
Response data (List<UserRole>):
[
  { "id": 1, "name": "ROLE_USER" },
  { "id": 2, "name": "ROLE_ADMIN" }
]

2) POST /api/roles
Request JSON (CreateRoleRequest):
{
  "name": "ROLE_SUPPORT"
}
Response data (UserRole):
{ "id": 5, "name": "ROLE_SUPPORT" }

3) GET /api/roles/{name}
Response data (UserRole):
{ "id": 1, "name": "ROLE_USER" }

4) DELETE /api/roles/{id}
Response: void (200 OK)

---

## EmailController (/email)

1) GET /email/send-otp?email=user@example.com
Response: Body String and header X-Key-APT
Body:
"Forgot password reset successfully"
Header:
X-Key-APT: "otp-token-abc123"

2) GET /email/check-otp?email=user@example.com
Response (SkipWrap): boolean
true

3) POST /email/verify-otp
Request: Header X-Key-APT: "otp-token-abc123"
Request JSON (VerifyOtpRequest):
{
  "email": "user@example.com",
  "otp": "123456"
}
Response (SkipWrap): String
"Correct otp"

---

## ReportController & EventController
- Currently no public endpoints implemented. Suggested sample endpoints for future:
  - POST /api/reports/generate { reportType, filters } => returns PDF url or summary data
  - POST /event/notify { eventType, payload } => returns eventId

---

# Notes
- All responses above are the inner `data` returned by controllers. Actual HTTP responses are wrapped by `BaseResponse` unless the controller uses `@SkipWrap`.
- Endpoints returning `void` produce BaseResponse.success with data=null and appropriate status code (201 for creation when annotated).
- Include headers (e.g., X-Key-APT, X-Password-Reset-Token) where controllers set them.

