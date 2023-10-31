package kim.kin.common;


import java.util.Objects;

/**
 * @author kin.kim
 * @since 2023-10-27
 **/
public enum ResultEnum {

    RESULT_SUCCESS(ResultConstant.CODE_OK, ResultConstant.MSG_OK),
    RESULT_FAIL(ResultConstant.CODE_FAIL, ResultConstant.MSG_FAIL),
    SMS_TIMEOUT(452, "短信验证码超时,请重新提交");
    private final Integer resultCode;
    private final String resultMsg;
    private static final ResultEnum[] VALUES;

    static {
        VALUES = values();
    }

    ResultEnum(Integer resultCode, String resultMsg) {
        this.resultCode = resultCode;
        this.resultMsg = resultMsg;
    }

    public Integer getResultCode() {
        return resultCode;
    }


    public String getResultMsg() {
        return resultMsg;
    }


    public static ResultEnum valueOf(Integer resultCode) {
        ResultEnum resolve = resolve(resultCode);
        if (resolve == null) {
            throw new IllegalArgumentException("No matching constant for [" + resultCode + "]");
        }
        return resolve;
    }

    public static ResultEnum resolve(Integer resultCode) {
        for (ResultEnum status : VALUES) {
            if (Objects.equals(status.resultCode, resultCode)) {
                return status;
            }
        }
        return null;
    }
}
