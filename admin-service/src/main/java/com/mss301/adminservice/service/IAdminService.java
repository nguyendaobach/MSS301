package com.mss301.adminservice.service;

import com.mss301.adminservice.dto.ResponseApi;
import com.mss301.adminservice.dto.request.CreateUserRequest;
import com.mss301.adminservice.dto.request.UpdateUserRequest;
import com.mss301.adminservice.dto.response.UserResponse;
import com.mss301.adminservice.dto.response.UserStatsResponse;

import java.util.List;
import java.util.UUID;

public interface IAdminService {

    /**
     * Lấy danh sách tất cả người dùng
     * @return ResponseApi chứa danh sách UserResponse
     */
    ResponseApi<List<UserResponse>> getAllUsers();

    /**
     * Lấy thông tin người dùng theo ID
     * @param id ID của người dùng (UUID)
     * @return ResponseApi chứa UserResponse
     */
    ResponseApi<UserResponse> getUserById(UUID id);

    /**
     * Tạo người dùng mới
     * @param request Thông tin người dùng cần tạo
     * @return ResponseApi chứa UserResponse của người dùng mới tạo
     */
    ResponseApi<UserResponse> createUser(CreateUserRequest request);

    /**
     * Cập nhật thông tin người dùng
     * @param id ID của người dùng cần cập nhật (UUID)
     * @param request Thông tin cần cập nhật
     * @return ResponseApi chứa UserResponse đã cập nhật
     */
    ResponseApi<UserResponse> updateUser(UUID id, UpdateUserRequest request);

    /**
     * Xóa người dùng
     * @param id ID của người dùng cần xóa (UUID)
     * @return ResponseApi chứa thông báo kết quả
     */
    ResponseApi<String> deleteUser(UUID id);

    /**
     * Kích hoạt/Vô hiệu hóa người dùng
     * @param id ID của người dùng (UUID)
     * @return ResponseApi chứa UserResponse đã cập nhật
     */
    ResponseApi<UserResponse> toggleUserStatus(UUID id);

    /**
     * Lấy danh sách người dùng theo role code
     * @param roleCode Role code của người dùng (OWNER, EDITOR, VIEWER)
     * @return ResponseApi chứa danh sách UserResponse
     */
    ResponseApi<List<UserResponse>> getUsersByRole(String roleCode);

    /**
     * Lấy thống kê người dùng
     * @return ResponseApi chứa UserStatsResponse
     */
    ResponseApi<UserStatsResponse> getUserStats();
}
