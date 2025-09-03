package com.kbsw.seasonthon.user.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import com.kbsw.seasonthon.user.entity.User;

//TODO : 해당 유저가 참여중인 공구 정보도 추가

@Setter
@Getter
@Builder
public class UserResponseDto {
    private Long id;
    private String username;
    private String nickname;
    private String email;

    public static UserResponseDto fromEntity(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .build();
    }
}
