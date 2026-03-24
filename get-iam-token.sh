#!/bin/bash
# Lấy token từ Keycloak (IAM)
# Thay client_secret tương ứng với client_id trong Keycloak
IAM_TOKEN=$(curl -s -X POST \
  "http://localhost:8180/realms/company-realm/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=password" \
  -d "client_id=my-app" \
  -d "client_secret=EzYO721MyjRN6rzA4aJO9fcN31I46OOR" \
  -d "username=employee@company.com" \
  -d "password=password123" \
  | jq -r '.access_token' \
  )

echo "IAM Token: $IAM_TOKEN"

# Gọi API nhân viên
curl -H "Authorization: Bearer $IAM_TOKEN" \
  http://localhost:8080/api/employee/profile
