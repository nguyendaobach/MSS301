package com.mss301.adminservice.service;

import com.mss301.adminservice.dto.ResponseApi;
import com.mss301.adminservice.dto.request.CreateUserRequest;
import com.mss301.adminservice.dto.request.UpdateUserRequest;
import com.mss301.adminservice.dto.response.RoleResponse;
import com.mss301.adminservice.dto.response.UserResponse;
import com.mss301.adminservice.dto.response.UserStatsResponse;
import com.mss301.adminservice.entity.Role;
import com.mss301.adminservice.entity.Status;
import com.mss301.adminservice.entity.User;
import com.mss301.adminservice.repository.RoleRepository;
import com.mss301.adminservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService implements IAdminService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    @Transactional(readOnly = true)
    public ResponseApi<List<UserResponse>> getAllUsers() {
        try {
            List<UserResponse> users = userRepository.findAll().stream()
                    .map(this::convertToUserResponse)
                    .collect(Collectors.toList());
            return new ResponseApi<>(HttpStatus.OK.value(), "Lấy danh sách người dùng thành công", users);
        } catch (Exception e) {
            return new ResponseApi<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Lỗi khi lấy danh sách người dùng: " + e.getMessage(), null);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseApi<UserResponse> getUserById(UUID id) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + id));
            return new ResponseApi<>(HttpStatus.OK.value(), "Lấy thông tin người dùng thành công",
                    convertToUserResponse(user));
        } catch (RuntimeException e) {
            return new ResponseApi<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseApi<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Lỗi khi lấy thông tin người dùng: " + e.getMessage(), null);
        }
    }

    @Override
    @Transactional
    public ResponseApi<UserResponse> createUser(CreateUserRequest request) {
        try {
            if (userRepository.existsByEmail(request.getEmail())) {
                return new ResponseApi<>(HttpStatus.BAD_REQUEST.value(),
                        "Email đã tồn tại trong hệ thống", null);
            }

            Role role = roleRepository.findById(request.getRoleId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy role với ID: " + request.getRoleId()));

            User user = User.builder()
                    .email(request.getEmail())
                    .password(request.getPassword()) // Trong production nên mã hóa password
                    .fullName(request.getFullName())
                    .role(role)
                    .status(Status.ACTIVE)
                    .build();

            User savedUser = userRepository.save(user);
            return new ResponseApi<>(HttpStatus.CREATED.value(),
                    "Tạo người dùng thành công", convertToUserResponse(savedUser));
        } catch (RuntimeException e) {
            return new ResponseApi<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseApi<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Lỗi khi tạo người dùng: " + e.getMessage(), null);
        }
    }

    @Override
    @Transactional
    public ResponseApi<UserResponse> updateUser(UUID id, UpdateUserRequest request) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + id));

            if (request.getFullName() != null) {
                user.setFullName(request.getFullName());
            }
            if (request.getRoleId() != null) {
                Role role = roleRepository.findById(request.getRoleId())
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy role với ID: " + request.getRoleId()));
                user.setRole(role);
            }
            if (request.getStatus() != null) {
                user.setStatus(request.getStatus());
            }

            User updatedUser = userRepository.save(user);
            return new ResponseApi<>(HttpStatus.OK.value(),
                    "Cập nhật người dùng thành công", convertToUserResponse(updatedUser));
        } catch (RuntimeException e) {
            return new ResponseApi<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseApi<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Lỗi khi cập nhật người dùng: " + e.getMessage(), null);
        }
    }

    @Override
    @Transactional
    public ResponseApi<String> deleteUser(UUID id) {
        try {
            if (!userRepository.existsById(id)) {
                return new ResponseApi<>(HttpStatus.NOT_FOUND.value(),
                        "Không tìm thấy người dùng với ID: " + id, null);
            }
            userRepository.deleteById(id);
            return new ResponseApi<>(HttpStatus.OK.value(),
                    "Xóa người dùng thành công", "Đã xóa người dùng ID: " + id);
        } catch (Exception e) {
            return new ResponseApi<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Lỗi khi xóa người dùng: " + e.getMessage(), null);
        }
    }

    @Override
    @Transactional
    public ResponseApi<UserResponse> toggleUserStatus(UUID id) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + id));

            Status newStatus = user.getStatus() == Status.ACTIVE ? Status.INACTIVE : Status.ACTIVE;
            user.setStatus(newStatus);
            User updatedUser = userRepository.save(user);

            String message = newStatus == Status.ACTIVE ?
                    "Đã kích hoạt người dùng" : "Đã vô hiệu hóa người dùng";
            return new ResponseApi<>(HttpStatus.OK.value(), message, convertToUserResponse(updatedUser));
        } catch (RuntimeException e) {
            return new ResponseApi<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseApi<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Lỗi khi thay đổi trạng thái người dùng: " + e.getMessage(), null);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseApi<List<UserResponse>> getUsersByRole(String roleCode) {
        try {
            List<UserResponse> users = userRepository.findByRoleCode(roleCode).stream()
                    .map(this::convertToUserResponse)
                    .collect(Collectors.toList());
            return new ResponseApi<>(HttpStatus.OK.value(),
                    "Lấy danh sách người dùng theo role thành công", users);
        } catch (Exception e) {
            return new ResponseApi<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Lỗi khi lấy danh sách người dùng: " + e.getMessage(), null);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseApi<UserStatsResponse> getUserStats() {
        try {
            OffsetDateTime startOfToday = OffsetDateTime.now().withHour(0).withMinute(0).withSecond(0);

            UserStatsResponse stats = UserStatsResponse.builder()
                    .totalUsers(userRepository.count())
                    .activeUsers(userRepository.countByStatus(Status.ACTIVE))
                    .verifiedUsers(0L) // Không có trường verified trong entity
                    .premiumUsers(userRepository.countByRoleCode("PREMIUM"))
                    .newUsersToday(userRepository.countUsersCreatedAfter(startOfToday))
                    .build();

            return new ResponseApi<>(HttpStatus.OK.value(),
                    "Lấy thống kê người dùng thành công", stats);
        } catch (Exception e) {
            return new ResponseApi<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Lỗi khi lấy thống kê: " + e.getMessage(), null);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseApi<List<RoleResponse>> getAllRoles() {
        try {
            List<RoleResponse> roles = roleRepository.findAll().stream()
                    .map(this::convertToRoleResponse)
                    .collect(Collectors.toList());
            return new ResponseApi<>(HttpStatus.OK.value(),
                    "Lấy danh sách roles thành công", roles);
        } catch (Exception e) {
            return new ResponseApi<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Lỗi khi lấy danh sách roles: " + e.getMessage(), null);
        }
    }

    private UserResponse convertToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .roleCode(user.getRole() != null ? user.getRole().getCode() : null)
                .roleName(user.getRole() != null ? user.getRole().getName() : null)
                .status(user.getStatus() != null ? user.getStatus().name() : null)
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    private RoleResponse convertToRoleResponse(Role role) {
        return RoleResponse.builder()
                .id(role.getId())
                .code(role.getCode())
                .name(role.getName())
                .description("Role: " + role.getName())
                .build();
    }
}
