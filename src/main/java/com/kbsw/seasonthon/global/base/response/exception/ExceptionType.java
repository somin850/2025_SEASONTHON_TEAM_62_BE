package com.kbsw.seasonthon.global.base.response.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
public enum ExceptionType {
    // common
    UNEXPECTED_SERVER_ERROR(INTERNAL_SERVER_ERROR, "C001", "예상치못한 서버에러 발생"),
    BINDING_ERROR(BAD_REQUEST, "C002", "바인딩시 에러 발생"),
    ESSENTIAL_FIELD_MISSING_ERROR(NO_CONTENT , "C003","필수적인 필드 부재"),
    INVALID_VALUE_ERROR(NOT_ACCEPTABLE , "C004","값이 유효하지 않음"),
    DUPLICATE_VALUE_ERROR(NOT_ACCEPTABLE , "C005","값이 중복됨"),
    NULL_POINTER_ERROR(INTERNAL_SERVER_ERROR, "C006", "널 포인터 에러 발생"),
    ARRAY_INDEX_OUT_OF_BOUNDS_ERROR(BAD_REQUEST, "C007", "배열 인덱스 범위 초과"),
    CLASS_CAST_ERROR(BAD_REQUEST, "C008", "타입 캐스팅 에러"),
    NUMBER_FORMAT_ERROR(BAD_REQUEST, "C009", "숫자 형식 에러"),
    JSON_PROCESSING_ERROR(BAD_REQUEST, "C010", "JSON 처리 에러"),
    HTTP_CLIENT_ERROR(BAD_GATEWAY, "C011", "외부 API 호출 에러"),
    TIMEOUT_ERROR(REQUEST_TIMEOUT, "C012", "요청 시간 초과"),
    RESOURCE_NOT_FOUND_ERROR(NOT_FOUND, "C013", "리소스를 찾을 수 없음"),
    ACCESS_DENIED_ERROR(FORBIDDEN, "C014", "접근 권한 없음"),
    METHOD_NOT_ALLOWED_ERROR(METHOD_NOT_ALLOWED, "C015", "지원하지 않는 HTTP 메서드"),
    UNSUPPORTED_MEDIA_TYPE_ERROR(UNSUPPORTED_MEDIA_TYPE, "C016", "지원하지 않는 미디어 타입"),
    REQUEST_TOO_LARGE_ERROR(PAYLOAD_TOO_LARGE, "C017", "요청 크기 초과"),
    TOO_MANY_REQUESTS_ERROR(TOO_MANY_REQUESTS, "C018", "요청 횟수 초과"),

    // auth
    INVALID_REFRESH_TOKEN(NOT_ACCEPTABLE , "A001","유효하지 않은 리프레시 토큰"),
    REFRESH_TOKEN_EXPIRED(UNAUTHORIZED,"A002","리프레시 토큰 만료"),
    PASSWORD_NOT_MATCHED(NOT_ACCEPTABLE , "A003","비밀번호 불일치"),

    // oauth2
    INVALID_PROVIDER_TYPE_ERROR(NOT_ACCEPTABLE , "O001","지원하지 않는 provider"),

    // user
    USER_NOT_FOUND(NOT_FOUND, "U001", "존재하지 않는 사용자"),
    DUPLICATED_USER_ID(CONFLICT, "U002", "중복 아이디(PK)"),
    DUPLICATED_USERNAME(CONFLICT, "U003", "중복 아이디(username)"),
    ALREADY_REGISTERED_USER(NOT_ACCEPTABLE , "U006","이미 최종 회원 가입된 사용자"),
    NOT_REGISTERED_USER(FORBIDDEN , "U007","최종 회원 가입 되지 않은 사용자"),
    UNAUTHORIZED_USER(UNAUTHORIZED, "U005","로그인 되지 않은 사용자"),
    
    // favorite
    FAVORITE_NOT_FOUND(NOT_FOUND, "F001", "존재하지 않는 즐겨찾기"),
    
    // crew
    CREW_NOT_FOUND(NOT_FOUND, "CR001", "존재하지 않는 크루"),
    CREW_ALREADY_EXISTS(CONFLICT, "CR002", "이미 존재하는 크루"),
    CREW_FULL_ERROR(FORBIDDEN, "CR003", "크루 정원이 가득참"),
    CREW_ALREADY_JOINED(CONFLICT, "CR004", "이미 참여한 크루"),
    CREW_NOT_JOINED(FORBIDDEN, "CR005", "참여하지 않은 크루"),
    CREW_OWNER_CANNOT_LEAVE(FORBIDDEN, "CR006", "크루장은 크루를 떠날 수 없음"),
    CREW_PERMISSION_DENIED(FORBIDDEN, "CR007", "크루 권한 없음"),
    CREW_ROUTE_ERROR(BAD_REQUEST, "CR008", "크루 경로 생성 에러"),
    CREW_AI_SERVICE_ERROR(BAD_GATEWAY, "CR009", "AI 서비스 호출 에러"),
    
    // crew participant
    PARTICIPANT_NOT_FOUND(NOT_FOUND, "CP001", "존재하지 않는 참여자"),
    PARTICIPANT_ALREADY_EXISTS(CONFLICT, "CP002", "이미 존재하는 참여자"),
    PARTICIPANT_STATUS_ERROR(BAD_REQUEST, "CP003", "잘못된 참여자 상태"),
    
    // route
    ROUTE_NOT_FOUND(NOT_FOUND, "R001", "존재하지 않는 경로"),
    ROUTE_GENERATION_ERROR(INTERNAL_SERVER_ERROR, "R002", "경로 생성 에러"),
    ROUTE_AI_SERVICE_TIMEOUT(REQUEST_TIMEOUT, "R003", "AI 서비스 응답 시간 초과");

    private final HttpStatus status;
    private final String code;
    private final String message;}
