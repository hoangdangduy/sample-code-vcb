package com.vcb.handler;

import com.vcb.exception.ErrorCode;
import com.vcb.exception.CommonException;
import com.vcb.model.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.LocaleResolver;

@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageSource messageSource;
    private final LocaleResolver localeResolver;

    @ExceptionHandler(CommonException.class)
    public ResponseEntity<ErrorResponse> handleVcbException(CommonException e, HttpServletRequest request) {
        Locale locale = localeResolver.resolveLocale(request);
        ErrorCode errorCode = e.getErrorCode();
        String message = messageSource.getMessage(errorCode.getMessageKey(), e.getArgs(), locale);
        log.error("[{}] {}", errorCode.getCode(), message, e);
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ErrorResponse.builder()
                        .code(errorCode.getCode())
                        .message(message)
                        .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception e, HttpServletRequest request) {
        Locale locale = localeResolver.resolveLocale(request);
        String message = messageSource.getMessage(
                ErrorCode.MISSING_HANDLE_SERVER_ERROR.getMessageKey(), null, locale);
        log.error("[SYS-001] Unexpected error: {}", e.getMessage(), e);
        return ResponseEntity
                .status(ErrorCode.MISSING_HANDLE_SERVER_ERROR.getHttpStatus())
                .body(ErrorResponse.builder()
                        .code(ErrorCode.MISSING_HANDLE_SERVER_ERROR.getCode())
                        .message(message)
                        .build());
    }
}
