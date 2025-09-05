package com.kbsw.seasonthon.user.dto.response;

import com.kbsw.seasonthon.running.dto.response.RunningStatsResponse;
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
    private RunningStatsResponse runningStats;  // 러닝 통계 추가

    public static UserResponseDto fromEntity(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .build();
    }
    
    public static UserResponseDto fromEntityWithStats(User user, RunningStatsResponse runningStats) {
        return UserResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .runningStats(runningStats)
                .build();
    }
}
