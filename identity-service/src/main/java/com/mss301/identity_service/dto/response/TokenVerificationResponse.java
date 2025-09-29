package com.mss301.identity_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenVerificationResponse {
    private boolean valid;
    private String username;
    private Date expiration;
    private String message;
    private String tokenId;
    private Map<String, Object> claims;
}
