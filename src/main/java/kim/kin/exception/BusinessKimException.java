package kim.kin.exception;

public class BusinessKimException extends RuntimeException {

    private String errorCode;
    private String errorMessage;


    public BusinessKimException(String errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;

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


    @Override
    public String toString() {
        return "BusinessException{" +
                "errorCode='" + errorCode + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
