package kim.kin.config;

import java.io.Serializable;

/**
 *
 */
public class ResResult implements Serializable {
    /**
     * if request is success
     */
    private Boolean success;
    /**
     * response data
     */
    private Object data;
    /**
     * code for errorType 0000 SUCCESS
     */
    private String errorCode;
    /**
     * message display to user
     */
    private String errorMessage;
    /**
     * error display type： 0 silent; 1 message.warn; 2 message.error; 4 notification; 9 page
     */
    private Integer showType;
    /**
     * Convenient for back-end Troubleshooting: unique request ID
     */
    private String traceId;
    /**
     * onvenient for backend Troubleshooting: host of current access server
     */
    private String host;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
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

    public Integer getShowType() {
        return showType;
    }

    public void setShowType(Integer showType) {
        this.showType = showType;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    @Override
    public String toString() {
        return "ResResult{" +
                "success=" + success +
                ", data=" + data +
                ", errorCode='" + errorCode + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                ", showType=" + showType +
                ", traceId='" + traceId + '\'' +
                ", host='" + host + '\'' +
                '}';
    }
}
