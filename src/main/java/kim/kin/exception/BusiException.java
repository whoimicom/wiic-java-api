package kim.kin.exception;

import kim.kin.common.ResultConstant;
import kim.kin.common.ResultEnum;

public class BusiException extends RuntimeException {

    private Integer errorCode = ResultConstant.CODE_FAIL;
    private String errorMessage;


    public BusiException(Integer errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;

    }

    public BusiException(String errorMessage) {
        super(errorMessage);
        this.errorMessage = errorMessage;

    }

    public BusiException(ResultEnum resultEnum) {
        this(resultEnum.getResultCode(), resultEnum.getResultMsg());

    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
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
