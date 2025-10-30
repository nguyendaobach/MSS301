package com.mss301.adminservice.repository;

import com.mss301.adminservice.entity.Role;
import com.mss301.adminservice.entity.Status;
import com.mss301.adminservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    List<User> findByRole(Role role);

    List<User> findByStatus(Status status);

    Long countByStatus(Status status);

    @Query("SELECT COUNT(u) FROM User u WHERE u.role.code = :roleCode")
    Long countByRoleCode(String roleCode);

    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt >= :startDate")
    Long countUsersCreatedAfter(OffsetDateTime startDate);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.role.code = :roleCode")
    List<User> findByRoleCode(String roleCode);
}
