package com.kbsw.seasonthon.user.service;


import com.kbsw.seasonthon.global.base.response.exception.BusinessException;
import com.kbsw.seasonthon.global.base.response.exception.ExceptionType;
import com.kbsw.seasonthon.running.dto.response.RunningStatsResponse;
import com.kbsw.seasonthon.running.service.RunningRecordService;
import com.kbsw.seasonthon.user.dto.request.SignUpRequestDto;
import com.kbsw.seasonthon.user.dto.response.UserResponseDto;
import com.kbsw.seasonthon.user.entity.User;
import com.kbsw.seasonthon.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class UserService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RunningRecordService runningRecordService;




    public UserResponseDto signup(SignUpRequestDto request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new BusinessException(ExceptionType.DUPLICATED_USERNAME);
        }

        // 비밀번호 암호화 및 저장
        request.setPassword(passwordEncoder.encode(request.getPassword()));
        User user = request.toEntity();
        user = userRepository.save(user);


        return UserResponseDto.fromEntity(user);
    }


    public UserResponseDto getUserInfoById(Long userId) {
        User user = userRepository
                .findById(userId)
                .orElseThrow(()-> new BusinessException(ExceptionType.USER_NOT_FOUND));

        // 러닝 통계 조회
        RunningStatsResponse runningStats = runningRecordService.getRunningStats(user);

        return UserResponseDto.fromEntityWithStats(user, runningStats);
    }




}