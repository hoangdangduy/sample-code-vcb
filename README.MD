## 📋 Danh sách Endpoints

| Method | Endpoint | Auth Required | Role | Mô tả                   |
|--------|----------|---------------|------|-------------------------|
| GET    | `/api/admin/employees` | ✅ IAM | `ADMIN` | Lấy danh sách nhân viên |
| POST   | `/api/admin` | ✅ IAM | `ADMIN` | Thêm nhân viên          |
| DELETE | `/api/admin//{username}` | ✅ IAM | `ADMIN` | Xoá nhân viên           |
| GET    | `/api/employee/profile` | ✅ IAM | `EMPLOYEE` hoặc `ADMIN` | Lấy thông tin nhân viên |

---

## 🧪 Test thủ công bằng curl

```bash
# 1. Role employee lấy thông tin chính nhân viên
./get-iam-employee-token.sh

# 2. Role admin lấy danh sách nhân viên
./get-iam-admin-token.sh

# 3. Role admin thêm nhân viên
./get-iam-admin-add.sh

# 4. Role admin xoá nhân viên
./get-iam-admin-delete.sh
```