package me.delous.otp.common;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ApiResult<Void> badRequest(IllegalArgumentException ex) {
        return ApiResult.fail("request.invalid", ex.getMessage());
    }

    @ExceptionHandler(DuplicateKeyException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    ApiResult<Void> conflict(DuplicateKeyException ex) {
        return ApiResult.fail("account.duplicate", "Пользователь с таким логином уже существует");
    }

    @ExceptionHandler(SecurityException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    ApiResult<Void> forbidden(SecurityException ex) {
        return ApiResult.fail("access.denied", ex.getMessage());
    }
}
