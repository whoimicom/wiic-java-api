package kim.kin.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.beans.PropertyEditorSupport;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;

/**
 * @author choky
 */
@RestControllerAdvice
public class CommExceptionKimHandler {
    private final Logger log = LoggerFactory.getLogger(CommExceptionKimHandler.class);


    @ExceptionHandler(AuthenticationException.class)
//    @ResponseBody
    public ResponseEntity<Object> authenticationException(AuthenticationException e) {
        log.error("RestControllerAdvice：", e);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }

    /**
     * 自定义异常捕捉处理
     */
    @ExceptionHandler(value = BusinessKimException.class)
    public ResponseEntity<Object> smsException(BusinessKimException ex) {
        log.error(ex.getMessage(), ex);
        String errorCode = Optional.ofNullable(ex.getErrorCode()).orElse("500");
        String errorMessage = Optional.ofNullable(ex.getErrorMessage()).orElse("No ErrorMessage");
        log.error("businessException getErrorCode:{}, getErrorMessage:{},getMessage:{}", errorCode, errorMessage, ex.getMessage());
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
        log.error("globalException {}", throwable.getMessage(), throwable);
        return ResponseEntity.badRequest()
                .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                .body(new ErrorInfo(throwable));
    }

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(LocalDate.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                log.debug("initBinder LocalDate[{}]", text);
                setValue(LocalDate.parse(text, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            }
        });
        binder.registerCustomEditor(LocalDateTime.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                log.debug("initBinder LocalDateTime[{}]", text);
                setValue(LocalDateTime.parse(text, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            }
        });
        binder.registerCustomEditor(LocalTime.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                log.debug("initBinder LocalTime[{}]", text);
                setValue(LocalTime.parse(text, DateTimeFormatter.ofPattern("HH:mm:ss")));
            }
        });
        binder.registerCustomEditor(Date.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                log.debug("initBinder Date[{}]", text);
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    setValue(formatter.parse(text));
                } catch (Exception e) {
                    throw new RuntimeException(String.format("Error parsing %s to Date", text));
                }
            }
        });
    }
}
