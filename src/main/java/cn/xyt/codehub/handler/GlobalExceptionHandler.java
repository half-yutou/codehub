package cn.xyt.codehub.handler;

import cn.xyt.codehub.dto.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    @ExceptionHandler(RuntimeException.class)
    public Result handleRuntimeException(RuntimeException ex) {
        log.error("RuntimeException: " + ex.getMessage());
        return Result.fail("Server error: " + ex.getMessage());
    }
}
