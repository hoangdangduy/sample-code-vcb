# IAM & CIAM Demo - Spring Boot

Dự án minh hoạ tích hợp **IAM (Keycloak)** và **CIAM (Auth0)** vào ứng dụng Spring Boot với JWT multi-tenant authentication.

---

## 📚 Tổng quan: IAM vs CIAM

| Tiêu chí | IAM (Identity & Access Management) | CIAM (Customer IAM) |
|---|---|---|
| **Đối tượng** | Nhân viên nội bộ | Khách hàng bên ngoài |
| **Công cụ phổ biến** | Keycloak, Okta Workforce | Auth0, Cognito, Firebase Auth |
| **Quy mô** | Hàng nghìn user | Hàng triệu user |
| **Tính năng chính** | SSO, MFA, RBAC nội bộ | Social login, self-registration, consent |
| **UX** | Admin-focused | Customer-friendly |
| **Compliance** | Active Directory, LDAP | GDPR, CCPA |

### Ví dụ trong project này:
- **IAM (Keycloak)**: Nhân viên đăng nhập vào hệ thống nội bộ → truy cập `/api/employee/**`
- **CIAM (Auth0)**: Khách hàng đăng nhập vào app → truy cập `/api/customer/**`

---

## 🏗️ Kiến trúc hệ thống

```
                    ┌─────────────────────────────────────────┐
                    │           Spring Boot App               │
                    │              :8080                      │
                    │                                         │
 [Nhân viên]        │  ┌──────────────────────────────────┐  │
     │              │  │     MultiTenantJwtDecoder        │  │
     │  JWT (IAM)   │  │                                  │  │
     ├─────────────▶│  │  iss == Keycloak → IAM Decoder   │  │
     │              │  │  iss == Auth0   → CIAM Decoder   │  │
 [Keycloak]         │  └──────────────────────────────────┘  │
 :8180              │                                         │
                    │  ┌───────────────────────────────────┐  │
 [Khách hàng]       │  │         SecurityConfig            │  │
     │              │  │                                   │  │
     │  JWT (CIAM)  │  │  /api/employee/** → ROLE_EMPLOYEE │  │
     ├─────────────▶│  │  /api/customer/** → ROLE_CUSTOMER │  │
     │              │  │  /api/public/**   → permitAll     │  │
 [Auth0]            │  └───────────────────────────────────┘  │
                    └─────────────────────────────────────────┘
```

---

## 📁 Cấu trúc project

```
.
├── pom.xml
├── src/main/java/com/demo/
│   ├── IamCiamApplication.java          # Entry point
│   ├── config/
│   │   ├── SecurityConfig.java          # Spring Security + RBAC
│   │   └── MultiTenantJwtConfig.java    # Multi-tenant JWT decoder
│   └── controller/
│       ├── PublicController.java        # /api/public/** (không cần auth)
│       ├── EmployeeController.java      # /api/employee/** (IAM - Keycloak)
│       └── CustomerController.java      # /api/customer/** (CIAM - Auth0)
├── src/main/resources/
│   └── application.yml
├── get-iam-token.sh    # Script lấy token từ Keycloak
├── get-ciam-token.sh   # Script lấy token từ Auth0
└── README.md
```

---

## 🚀 Cách chạy

### Bước 1: Khởi động Keycloak (IAM) bằng Docker

```bash
docker run -d \
  --name keycloak \
  -p 127.0.0.1:8180:8080 \
  -e KC_BOOTSTRAP_ADMIN_USERNAME=admin \
  -e KC_BOOTSTRAP_ADMIN_PASSWORD=admin \
  quay.io/keycloak/keycloak:26.5.5 start-dev
```

**Cấu hình Keycloak:**
1. Truy cập http://localhost:8180 → đăng nhập `admin/admin`
2. Tạo Realm mới: `company-realm`
3. Tạo Client: `my-app`
- Capability config (Settings tab):
  - Client authentication: tích ON
  - Authentication flow: chọn Standard flow, Direct Access Grants (dùng cho password)
- Tạo Roles (Roles tab): `EMPLOYEE`, `ADMIN`
- Tạo Realm Roles (Realm Roles tab): `EMPLOYEE`, `ADMIN`
4. Tạo User: `employee@company.com` → gán role `EMPLOYEE`
- Detais tab: 
  - Email verified: tích ON
  - Required user actions: empty
  - Credentials tab: set password `password123` (tắt Temporary)
  - Role Mappings tab: gán role `EMPLOYEE`
5. Bỏ Required Action trong Authentication (menu tab left): bỏ verify profile, email và bỏ hết default action

### Bước 2: Cấu hình Auth0 (CIAM)

1. Đăng ký tại https://auth0.com (miễn phí)
2. Tạo **API**: đặt Identifier (audience) là `https://my-spring-api`
3. Tạo **Application**
   - Name: My Spring Boot API (Test Application)
   - Application type: Regular Web App
     - Settings tab → Advanced  Settings → Grant Types: tích chọn "Password" và "Client Credentials"
     - Application Access: `User Access` và `Client Access` ứng với **My Spring Boot API (Test Application)** chuyển từ `Unauthorized` thành `Authorized`
4. Tạo **Rule/Action** để thêm roles vào token ở luồng post-login:

```javascript
// Auth0 Action (Login flow)
exports.onExecutePostLogin = async (event, api) => {
  const namespace = 'https://my-namespace';
  api.accessToken.setCustomClaim(`${namespace}/roles`, ['CUSTOMER']);
};
```

5. Setting (menu tab left) -> General tab -> API Authorization Settings
- Default Audience: `https://my-spring-api`
- Default Directory: `Username-Password-Authentication`

6. User Management (menu tab left) --> Create User
- Connection: `Username-Password-Authentication`
- Email: `dangduyhoang3009@gmail.com`
- Password: `123456a@`

### Cập nhật `application.yml`:

```yaml
app:
  security:
    ciam:
      issuer-uri: https://YOUR-TENANT.auth0.com/
      jwk-set-uri: https://YOUR-TENANT.auth0.com/.well-known/jwks.json
```

### Bước 3: Build và chạy ứng dụng

```bash
mvn spring-boot:run
```

---

## 🔑 Lấy token và test API

### IAM Token (Keycloak)

```bash
chmod +x get-iam-token.sh
./get-iam-token.sh
```

Hoặc thủ công:
```bash
curl -s -X POST \
  "http://localhost:8180/realms/company-realm/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=password&client_id=my-app&client_secret=YOUR_SECRET&username=employee@company.com&password=password123" \
  | jq -r '.access_token'
```

### CIAM Token (Auth0)

```bash
chmod +x get-ciam-token.sh
./get-ciam-token.sh
```

Hoặc thủ công:
```bash
curl -s -X POST \
  "https://YOUR-TENANT.auth0.com/oauth/token" \
  -H "Content-Type: application/json" \
  -d '{
    "client_id": "YOUR_CLIENT_ID",
    "client_secret": "YOUR_CLIENT_SECRET",
    "audience": "https://my-spring-api",
    "grant_type": "password",
    "username": "dangduyhoang3009@gmail.com",
    "password": "123456a@"
  }' | jq -r '.access_token'
```

---

## 📋 Danh sách Endpoints

| Method | Endpoint | Auth Required | Role | Mô tả |
|--------|----------|---------------|------|-------|
| GET | `/api/public/info` | ❌ Không | - | Thông tin app |
| GET | `/api/employee/profile` | ✅ IAM | `EMPLOYEE` | Hồ sơ nhân viên |
| GET | `/api/employee/dashboard` | ✅ IAM | `ADMIN` | Dashboard admin |
| GET | `/api/employee/resources` | ✅ IAM | `EMPLOYEE` hoặc `ADMIN` | Tài nguyên nội bộ |
| GET | `/api/customer/profile` | ✅ CIAM | `CUSTOMER` | Hồ sơ khách hàng |
| GET | `/api/customer/orders` | ✅ CIAM | `CUSTOMER` | Đơn hàng |
| GET | `/api/customer/products` | ❌ Không | - | Danh sách sản phẩm |

---

## 🧪 Test thủ công bằng curl

```bash
# 1. Public endpoint (không cần token)
curl http://localhost:8080/api/public/info

# 2. Employee endpoint với IAM token
IAM_TOKEN="your-keycloak-jwt-token"
curl -H "Authorization: Bearer $IAM_TOKEN" \
  http://localhost:8080/api/employee/profile

# 3. Customer endpoint với CIAM token
CIAM_TOKEN="your-auth0-jwt-token"
curl -H "Authorization: Bearer $CIAM_TOKEN" \
  http://localhost:8080/api/customer/profile

# 4. Test cross-access (IAM token dùng cho customer endpoint - phải bị từ chối)
curl -H "Authorization: Bearer $IAM_TOKEN" \
  http://localhost:8080/api/customer/profile
# → 403 Forbidden
```

---

## ⚙️ Cách hoạt động của Multi-Tenant JWT

```
Request với Bearer Token
         │
         ▼
┌─────────────────────┐
│  MultiTenantJwtConfig│
│                     │
│  1. Decode JWT (no  │
│     verify) để đọc  │
│     claim "iss"     │
│                     │
│  2. iss chứa        │
│     "keycloak"?     │
│     → IAM Decoder   │
│                     │
│  3. iss chứa        │
│     "auth0"?        │
│     → CIAM Decoder  │
│                     │
│  4. Không khớp?     │
│     → 401 Error     │
└─────────────────────┘
         │
         ▼
┌─────────────────────┐
│  JwtAuthConverter   │
│                     │
│  Keycloak:          │
│  realm_access.roles │
│  → ROLE_EMPLOYEE    │
│                     │
│  Auth0:             │
│  permissions /      │
│  custom namespace   │
│  → ROLE_CUSTOMER    │
└─────────────────────┘
         │
         ▼
  SecurityFilterChain
  (kiểm tra role vs endpoint)
```

---

## 🔒 Luồng bảo mật

1. **Nhân viên** đăng nhập vào Keycloak → nhận JWT với `iss = http://localhost:8180/realms/company-realm`
2. **Khách hàng** đăng nhập vào Auth0 → nhận JWT với `iss = https://your-tenant.auth0.com/`
3. Khi gọi API, Spring Boot đọc `iss` từ JWT (chưa verify)
4. Chọn đúng JWK Set URI để verify chữ ký
5. Kiểm tra issuer validator
6. Extract roles và phân quyền

---

## 📦 Dependencies chính

- `spring-boot-starter-security` - Spring Security
- `spring-boot-starter-oauth2-resource-server` - OAuth2 Resource Server (JWT support)
- `spring-boot-starter-web` - REST API
- `lombok` - Giảm boilerplate code

---

## 🛠️ Yêu cầu hệ thống

- Java 17+
- Maven 3.6+
- Docker (để chạy Keycloak)
- Tài khoản Auth0 (miễn phí)
