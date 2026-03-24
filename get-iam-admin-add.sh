# Lấy token
  TOKEN=$(curl -s -X POST \
    "http://localhost:8180/realms/company-realm/protocol/openid-connect/token" \
    -H "Content-Type: application/x-www-form-urlencoded" \
    -d "grant_type=password" \
    -d "client_id=my-app" \
    -d "client_secret=EzYO721MyjRN6rzA4aJO9fcN31I46OOR" \
    -d "username=hungtm" \
    -d "password=password123" \
    | jq -r '.access_token')

  # Gọi API tạo nhân viên
  curl -s -X POST "http://localhost:8080/api/admin" \
    -H "Authorization: Bearer $TOKEN" \
    -H "Content-Type: application/json" \
    -d '{
      "name": "Nguyen Van A",
      "age": 30,
      "address": "Ha Noi",
      "username": "test2"
    }'