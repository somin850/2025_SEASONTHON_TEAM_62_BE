package com.kbsw.seasonthon.global.base.response.exception;

import com.kbsw.seasonthon.global.base.response.ResponseBody;
import com.kbsw.seasonthon.global.base.response.ResponseUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.net.SocketTimeoutException;
import java.util.concurrent.TimeoutException;

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

    /* === 추가적인 예외 처리기들 === */

    /** NullPointerException → 500 */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ResponseBody<Void>> handleNullPointer(NullPointerException e) {
        log.error("NullPointerException: {}", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createFailureResponse(ExceptionType.NULL_POINTER_ERROR));
    }

    /** ArrayIndexOutOfBoundsException → 400 */
    @ExceptionHandler(ArrayIndexOutOfBoundsException.class)
    public ResponseEntity<ResponseBody<Void>> handleArrayIndexOutOfBounds(ArrayIndexOutOfBoundsException e) {
        log.error("ArrayIndexOutOfBoundsException: {}", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(createFailureResponse(ExceptionType.ARRAY_INDEX_OUT_OF_BOUNDS_ERROR));
    }

    /** ClassCastException → 400 */
    @ExceptionHandler(ClassCastException.class)
    public ResponseEntity<ResponseBody<Void>> handleClassCast(ClassCastException e) {
        log.error("ClassCastException: {}", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(createFailureResponse(ExceptionType.CLASS_CAST_ERROR));
    }

    /** NumberFormatException → 400 */
    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<ResponseBody<Void>> handleNumberFormat(NumberFormatException e) {
        log.error("NumberFormatException: {}", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(createFailureResponse(ExceptionType.NUMBER_FORMAT_ERROR));
    }

    /** JSON 처리 에러 → 400 */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ResponseBody<Void>> handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
        log.error("HttpMessageNotReadableException: {}", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(createFailureResponse(ExceptionType.JSON_PROCESSING_ERROR));
    }

    /** HTTP 클라이언트 에러 → 502 */
    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ResponseBody<Void>> handleHttpClientError(HttpClientErrorException e) {
        log.error("HttpClientErrorException: {}", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(createFailureResponse(ExceptionType.HTTP_CLIENT_ERROR));
    }

    /** HTTP 서버 에러 → 502 */
    @ExceptionHandler(HttpServerErrorException.class)
    public ResponseEntity<ResponseBody<Void>> handleHttpServerError(HttpServerErrorException e) {
        log.error("HttpServerErrorException: {}", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(createFailureResponse(ExceptionType.HTTP_CLIENT_ERROR));
    }

    /** RestClientException → 502 */
    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<ResponseBody<Void>> handleRestClient(RestClientException e) {
        log.error("RestClientException: {}", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(createFailureResponse(ExceptionType.HTTP_CLIENT_ERROR));
    }

    /** 타임아웃 에러 → 408 */
    @ExceptionHandler({TimeoutException.class, SocketTimeoutException.class})
    public ResponseEntity<ResponseBody<Void>> handleTimeout(Exception e) {
        log.error("TimeoutException: {}", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.REQUEST_TIMEOUT)
                .body(createFailureResponse(ExceptionType.TIMEOUT_ERROR));
    }

    /** 리소스 접근 에러 → 502 */
    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<ResponseBody<Void>> handleResourceAccess(ResourceAccessException e) {
        log.error("ResourceAccessException: {}", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(createFailureResponse(ExceptionType.HTTP_CLIENT_ERROR));
    }

    /** 404 Not Found → 404 */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ResponseBody<Void>> handleNoHandlerFound(NoHandlerFoundException e) {
        log.error("NoHandlerFoundException: {}", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(createFailureResponse(ExceptionType.RESOURCE_NOT_FOUND_ERROR));
    }

    /** 405 Method Not Allowed → 405 */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ResponseBody<Void>> handleMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        log.error("HttpRequestMethodNotSupportedException: {}", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(createFailureResponse(ExceptionType.METHOD_NOT_ALLOWED_ERROR));
    }

    /** 415 Unsupported Media Type → 415 */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ResponseBody<Void>> handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException e) {
        log.error("HttpMediaTypeNotSupportedException: {}", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(createFailureResponse(ExceptionType.UNSUPPORTED_MEDIA_TYPE_ERROR));
    }

    /** 필수 파라미터 누락 → 400 */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ResponseBody<Void>> handleMissingParameter(MissingServletRequestParameterException e) {
        log.error("MissingServletRequestParameterException: {}", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(createFailureResponse(ExceptionType.ESSENTIAL_FIELD_MISSING_ERROR));
    }

    /** 파라미터 타입 불일치 → 400 */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ResponseBody<Void>> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        log.error("MethodArgumentTypeMismatchException: {}", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(createFailureResponse(ExceptionType.BINDING_ERROR));
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