package com.mss301.adminservice.dto.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {


    private String email;

    private String password;

    private String fullName;

    private UUID roleId;
}
