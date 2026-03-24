#!/bin/bash
# Lấy token từ Keycloak (IAM)
# Thay client_secret tương ứng với client_id trong Keycloak
IAM_TOKEN=$(curl -s -X POST \
  "http://localhost:8180/realms/company-realm/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=password" \
  -d "client_id=my-app" \
  -d "client_secret=EzYO721MyjRN6rzA4aJO9fcN31I46OOR" \
  -d "username=hungtm" \
  -d "password=password123" \
  | jq -r '.access_token' \
  )

echo "IAM Token: $IAM_TOKEN"

# Gọi API nhân viên

RESPONSE=$(curl -s -o /tmp/response_body.txt -w "%{http_code}" -H "Authorization: Bearer $IAM_TOKEN" \
  http://localhost:8080/api/admin/employees)

if [ "$RESPONSE" -eq 401 ]; then
  echo "Error: HTTP 401 Unauthorized"
  echo "Response body: $(cat /tmp/response_body.txt)"
else
  echo "HTTP Status: $RESPONSE"
  cat /tmp/response_body.txt
fi