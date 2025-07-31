package com.comwith.fitlog.profile.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfilePersonalInfoRequest {

    @NotBlank(message = "이름은 필수로 입력하세요.")
    @Size(min=2, max=50, message="이름은 2자 이상, 50자 이내여야 합니다.")
    private String name;

//    @NotBlank(message = "이메일은 필수입니다.")
//    @Email(message = "올바른 이메일 형식이 아닙니다! 다시 입력하세요.")
//    private String email;

    @Size(min = 6, max = 100, message = "비밀번호는 6자 이상, 100자 이내여야 합니다.")
    private String password;

}
