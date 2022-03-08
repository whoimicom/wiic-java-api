package kim.kin.model;

public class ResultVO<T extends Object> {
    private Integer code = 0;
    private T result;
    private String message = "ok";
    private String type = "success";

    public ResultVO() {
        super();
    }


    public ResultVO(Integer code, T result, String message, String type) {
        this.code = code;
        this.result = result;
        this.message = message;
        this.type = type;
    }

    public static ResultVO success() {
        ResultVO<Object> resultVO = new ResultVO<>();
        return resultVO;
    }

    public static ResultVO success(Object result) {
        ResultVO<Object> resultVO = new ResultVO<>();
        resultVO.setResult(result);
        return resultVO;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
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
