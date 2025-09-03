package com.kbsw.seasonthon.user.controller;


import com.kbsw.seasonthon.global.base.response.ResponseUtil;
import com.kbsw.seasonthon.security.oauth2.principal.PrincipalDetails;
import com.kbsw.seasonthon.user.dto.request.SignUpRequestDto;
import com.kbsw.seasonthon.user.dto.response.UserResponseDto;
import com.kbsw.seasonthon.global.base.response.ResponseBody;
import com.kbsw.seasonthon.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User API", description = "유저 및 회원가입 관련 API")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "자체 회원가입", description = "자체 로그인 기반의 회원가입을 수행합니다.")
    @PostMapping("/sign-up")
    public ResponseEntity<ResponseBody<UserResponseDto>> signup(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "회원가입 요청 정보", required = true,
                    content = @Content(schema = @Schema(hidden = true))
            )
            @RequestBody SignUpRequestDto requestDto
    ) {
        UserResponseDto responseDto = userService.signup(requestDto);
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse(responseDto));
    }

    @GetMapping("/me")
    public UserResponseDto me(@AuthenticationPrincipal PrincipalDetails principal) {
        return userService.getUserInfoById(principal.getUser().getId());
    }


}
