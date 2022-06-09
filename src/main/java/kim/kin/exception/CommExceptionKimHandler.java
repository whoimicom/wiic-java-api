package kim.kin.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * @author choky
 */
@RestControllerAdvice
public class CommExceptionKimHandler {
    private final Logger logger = LoggerFactory.getLogger(CommExceptionKimHandler.class);


    @ExceptionHandler(AuthenticationException.class)
//    @ResponseBody
    public ResponseEntity<Object> authenticationException(AuthenticationException e) {
        logger.error("RestControllerAdvice：", e);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }

    /**
     * 自定义异常捕捉处理
     */
    @ExceptionHandler(value = BusinessKimException.class)
    public ResponseEntity<Object> smsException(BusinessKimException ex) {
        ex.printStackTrace();
        String errorCode = Optional.ofNullable(ex.getErrorCode()).orElse("500");
        String errorMessage = Optional.ofNullable(ex.getErrorMessage()).orElse("No ErrorMessage");
        logger.error("businessException getErrorCode:{}, getErrorMessage:{},getMessage:{}", errorCode, errorMessage, ex.getMessage());
        return ResponseEntity.badRequest()
                .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
//                .header("errorCode", errorCode).header("errorMessage", errorMessage)
                .body(new ErrorInfo(ex));
    }

    /**
     * 全局异常捕捉处理
     */
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<Object> globalException(Throwable throwable) {
        throwable.printStackTrace();
        logger.error("globalException {}", throwable.getMessage());
        return ResponseEntity.badRequest()
                .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                .body(new ErrorInfo(throwable));
    }

}
