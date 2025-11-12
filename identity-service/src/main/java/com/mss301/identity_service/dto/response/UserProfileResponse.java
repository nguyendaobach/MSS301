package com.mss301.identity_service.dto.response;

import com.mss301.identity_service.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileResponse {
    private UUID id;
    private String email;
    private String fullName;
    private Status status;
    private String role;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}

