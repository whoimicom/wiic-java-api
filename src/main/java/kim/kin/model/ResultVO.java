package kim.kin.model;

public class ResultVO<T> {
    private String code = "0";
    private T result;
    private String message = "ok";
    private String type = "success";

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "ResultVO{" +
                "code='" + code + '\'' +
                ", result=" + result +
                ", message='" + message + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
