package com.prography.game.global.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.prography.game.global.common.response.Result.*;

@Getter
public class ApiResponse<T> {
    private final Integer code;
    private final String message;
    @JsonInclude(NON_NULL)
    private final T result;

    public static <T> ApiResponse<T> success(T data) {
        return result(SUCCESS, data);
    }

    public static <T> ApiResponse<T> success() {
        return result(SUCCESS);
    }

    public static <T> ApiResponse<T> fail() {
        return result(FAIL);
    }

    public static <T> ApiResponse<T> error() {
        return result(ERROR);
    }

    public static <T> ApiResponse<T> result(Result result) {
        return ApiResponse.<T>builder()
                .result(result)
                .build();
    }

    public static <T> ApiResponse<T> result(Result result, T data) {
        return ApiResponse.<T>builder()
                .result(result)
                .data(data)
                .build();
    }

    @Builder
    public ApiResponse(Result result, Integer code, String message, T data) {
        this.code = (result == null) ? code : result.getCode();
        this.message = (result == null) ? message : result.getMessage();
        this.result = data;
    }
}
