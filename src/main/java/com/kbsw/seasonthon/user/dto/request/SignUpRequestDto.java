package com.kbsw.seasonthon.user.dto.request;

import com.kbsw.seasonthon.user.entity.User;
import com.kbsw.seasonthon.security.jwt.enums.Role;
import lombok.*;

/**
 * 자체 회원가입을 위한 dto
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignUpRequestDto {
    private String username;
    private String password;
    private String nickname;
    private String email;


    public User toEntity() {
        return User.builder()
                .username(username)
                .password(password)
                .nickname(nickname)
                .email(email)
                .role(Role.USER) // 별도의 추가 작업 없이 회원가입 시킴
                .build();
    }
}
