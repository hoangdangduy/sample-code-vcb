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

    # Gọi API xóa nhân viên theo username
    curl -s -X DELETE "http://localhost:8080/api/admin/test" \
      -H "Authorization: Bearer $TOKEN"