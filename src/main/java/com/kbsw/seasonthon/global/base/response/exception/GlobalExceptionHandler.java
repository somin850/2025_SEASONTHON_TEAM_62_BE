package com.kbsw.seasonthon.global.base.response.exception;

import com.kbsw.seasonthon.global.base.response.ResponseBody;
import com.kbsw.seasonthon.global.base.response.ResponseUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import static com.kbsw.seasonthon.global.base.response.ResponseUtil.createFailureResponse;

@RequiredArgsConstructor
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 비즈니스 예외 → 정의된 상태코드/메시지로 응답 */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ResponseBody<Void>> businessException(BusinessException e) {
        ExceptionType exceptionType = e.getExceptionType();
        return ResponseEntity.status(exceptionType.getStatus())
                .body(ResponseUtil.createFailureResponse(exceptionType));
    }

    /** 바인딩(검증) 예외 → BINDING_ERROR 상태코드/메시지 */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseBody<Void>> methodArgumentNotValidException(MethodArgumentNotValidException e) {
        String customMessage = e.getBindingResult().getAllErrors().isEmpty()
                ? ExceptionType.BINDING_ERROR.getMessage()
                : e.getBindingResult().getAllErrors().get(0).getDefaultMessage();

        return ResponseEntity
                .status(ExceptionType.BINDING_ERROR.getStatus())
                .body(ResponseUtil.createFailureResponse(ExceptionType.BINDING_ERROR, customMessage));
    }

    /* === 공통적으로 자주 쓰는 4xx 예외들을 명시적으로 매핑 === */

    /** 컨트롤러/서비스에서 던진 ResponseStatusException은 상태코드 그대로 전달 */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ResponseBody<Void>> handleResponseStatus(ResponseStatusException e) {
        String message = (e.getReason() != null && !e.getReason().isBlank())
                ? e.getReason()
                : e.getStatusCode().toString();

        return ResponseEntity
                .status(e.getStatusCode())
                .body(createFailureResponse(ExceptionType.BINDING_ERROR, message));
    }

    /** 잘못된 파라미터/검증 실패 등 → 400 */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResponseBody<Void>> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(createFailureResponse(ExceptionType.BINDING_ERROR, e.getMessage()));
    }

    /** 비즈니스 상태 충돌(중복 등) → 409 */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ResponseBody<Void>> handleIllegalState(IllegalStateException e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(createFailureResponse(ExceptionType.BINDING_ERROR, e.getMessage()));
    }

    /** DB 제약조건 위반(널/유니크/외래키 등) → 400 */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ResponseBody<Void>> handleDataIntegrity(DataIntegrityViolationException e) {
        String msg = "요청 데이터가 유효하지 않습니다.";
        log.warn("DataIntegrityViolationException: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(createFailureResponse(ExceptionType.BINDING_ERROR, msg));
    }

    /** 잡히지 않은 나머지 → 500 */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseBody<Void>> exception(Exception e) {
        log.error("Exception Message : {} ", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseUtil.createFailureResponse(ExceptionType.UNEXPECTED_SERVER_ERROR));
    }
}