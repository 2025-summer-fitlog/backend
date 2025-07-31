package com.comwith.fitlog.profile.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfilePersonalInfoResponse {

    private String name;
    private String email;
    private String password;
    private String loginMethod;
    private boolean isChangePassword;
}
