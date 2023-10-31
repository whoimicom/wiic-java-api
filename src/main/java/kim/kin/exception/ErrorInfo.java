package kim.kin.exception;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * errorInfo
 *
 * @author denglin
 */
public class ErrorInfo {
    private String errorCode = "500";
    private String errorMessage;
    private String errorHost;
    private String errorMethod;
    private LocalDateTime errorDateTime;

    public ErrorInfo(String errorMessage) {
        this.errorMessage = errorMessage;
        this.errorDateTime = LocalDateTime.now();
        try {
            this.errorHost = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public ErrorInfo(String errorCode, String errorMessage) {
        this(errorMessage);
        this.errorCode = errorCode;
    }

    public ErrorInfo(String errorCode, String errorMessage, String errorMethod) {
        this(errorCode, errorMessage);
        this.errorMethod = errorMethod;
    }

    public ErrorInfo(BusiException busiException) {
        this(String.valueOf(busiException.getErrorCode()), busiException.getErrorMessage());
        StackTraceElement[] stackTrace = busiException.getStackTrace();
        Optional.ofNullable(stackTrace).ifPresent(stackTraceElements -> {
            if (stackTraceElements.length > 1) {
                StackTraceElement stackTraceElement = stackTraceElements[0];
                String className = stackTraceElement.getClassName();
                String methodName = stackTraceElement.getMethodName();
                int lineNumber = stackTraceElement.getLineNumber();
                this.errorMethod = className + ":" + methodName + ":" + lineNumber;
            }
        });
    }
    public ErrorInfo(Throwable throwable) {
        this(throwable.getMessage());
        StackTraceElement[] stackTrace = throwable.getStackTrace();
        Optional.ofNullable(stackTrace).ifPresent(stackTraceElements -> {
            if (stackTraceElements.length > 1) {
                StackTraceElement stackTraceElement = stackTraceElements[0];
                String className = stackTraceElement.getClassName();
                String methodName = stackTraceElement.getMethodName();
                int lineNumber = stackTraceElement.getLineNumber();
                this.errorMethod = className + ":" + methodName + ":" + lineNumber;
            }
        });
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorHost() {
        return errorHost;
    }

    public void setErrorHost(String errorHost) {
        this.errorHost = errorHost;
    }

    public LocalDateTime getErrorDateTime() {
        return errorDateTime;
    }

    public void setErrorDateTime(LocalDateTime errorDateTime) {
        this.errorDateTime = errorDateTime;
    }

    public String getErrorMethod() {
        return errorMethod;
    }

    public void setErrorMethod(String errorMethod) {
        this.errorMethod = errorMethod;
    }
}
