package com.sprint.mission.discodeit.exception;

/**
 * 예외 상세 정보를 담는 타입 안전한 값 객체.
 * Map<String, Object> 대신 사용하여 응답 구조를 예측 가능하게 합니다.
 */
public record ExceptionDetail(String key, String value) {

    public static ExceptionDetail of(String key, Object value) {
        return new ExceptionDetail(key, String.valueOf(value));
    }
}
