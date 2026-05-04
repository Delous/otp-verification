package me.delous.otp.common;

public record ApiResult<T>(T data, ApiError error) {
    public static <T> ApiResult<T> ok(T data) {
        return new ApiResult<>(data, null);
    }

    public static <T> ApiResult<T> fail(String code, String message) {
        return new ApiResult<>(null, new ApiError(code, message));
    }
}
