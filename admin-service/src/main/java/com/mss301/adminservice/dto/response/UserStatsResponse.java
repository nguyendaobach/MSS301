package com.mss301.adminservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStatsResponse {

    private Long totalUsers;
    private Long activeUsers;
    private Long verifiedUsers;
    private Long premiumUsers;
    private Long newUsersToday;
}
