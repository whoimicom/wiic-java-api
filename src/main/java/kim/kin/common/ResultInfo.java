
package kim.kin.common;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;


public class ResultInfo<T> implements Serializable {

    private static final long serialVersionUID = -8949153510458566367L;

    /**
     * 作为交易成功失败的唯一标准
     */
    private Boolean success;
    /**
     * 用于区分失败类型[成功一般不设置值 ]
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer resultCode;
    /**
     * 失败描述[成功一般不设置值 ]
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String resultMsg;
    /**
     * 业务内容返回体[失败不设置值]
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T resultBody;

    public ResultInfo() {
        super();
    }

    public ResultInfo(Boolean success) {
        super();
        this.success = success;
    }

    public ResultInfo(T resultBody) {
        super();
        this.resultBody = resultBody;
    }

    public ResultInfo(Boolean success, T resultBody) {
        super();
        this.success = success;
        this.resultBody = resultBody;
    }

    public ResultInfo(Integer resultCode) {
        this(true);
        this.resultCode = resultCode;
    }

    public ResultInfo(Boolean success, Integer resultCode) {
        this(success);
        this.resultCode = resultCode;
    }

    public ResultInfo(Integer resultCode, String resultMsg) {
        this.resultCode = resultCode;
        this.resultMsg = resultMsg;
    }

    public ResultInfo(Boolean success, Integer resultCode, String resultMsg) {
        this(success, resultCode);
        this.resultMsg = resultMsg;
    }

    public ResultInfo(Integer resultCode, String resultMsg, T resultBody) {
        this(resultCode, resultMsg);
        this.resultBody = resultBody;
    }

    public ResultInfo(Boolean success, Integer resultCode, String resultMsg, T resultBody) {
        this(success, resultCode, resultMsg);
        this.resultBody = resultBody;
    }


    /**
     * @return 成功信息, 不包含BODY
     */
    public static <T> ResultInfo<T> ok() {
        return new ResultInfo<>(true);
    }

    /**
     * @param resultBody resultBody
     * @return 成功包含BODY
     */
    public static <T> ResultInfo<T> ok(T resultBody) {
        return new ResultInfo<>(true, resultBody);
    }

    /**
     * 成功包含所有信息【不建议使用，成功一般不设置CODE MESSAGE】
     *
     * @param resultCode    resultCode
     * @param resultMessage resultMessage
     * @param resultBody    body
     * @return 成功包含所有信息
     */
    @Deprecated
    public static <T> ResultInfo<T> ok(Integer resultCode, String resultMessage, T resultBody) {
        return new ResultInfo<>(true, resultCode, resultMessage, resultBody);
    }

    /**
     * @return Fail 不包含信息
     */
    public static <T> ResultInfo<T> fail() {
        ResultInfo<T> resultInfo = new ResultInfo<>();
        resultInfo.setSuccess(false);
        resultInfo.setResultCode(ResultConstant.FAIL_CODE);
        resultInfo.setResultMsg(ResultConstant.FAIL_MSG);
        return resultInfo;
    }

    public static <T> ResultInfo<T> fail(String resultMsg) {
        ResultInfo<T> resultInfo = new ResultInfo<>();
        resultInfo.setSuccess(false);
        resultInfo.setResultCode(ResultConstant.FAIL_CODE);
        resultInfo.setResultMsg(resultMsg);
        return resultInfo;
    }

    public static <T> ResultInfo<T> fail(Integer resultCode, String resultMsg) {
        ResultInfo<T> resultInfo = new ResultInfo<>();
        resultInfo.setSuccess(false);
        resultInfo.setResultCode(resultCode);
        resultInfo.setResultMsg(resultMsg);
        return resultInfo;
    }

    /**
     * 失败信息【不建议使用，FAIL时一般不包含BODY信息】
     *
     * @param resultCode resultCode
     * @param resultMsg  resultMsg
     * @param resultBody resultBody
     * @param <T>        resultBody
     * @return ResultInfo
     */
    @Deprecated
    public static <T> ResultInfo<T> fail(Integer resultCode, String resultMsg, T resultBody) {
        ResultInfo<T> resultInfo = new ResultInfo<>();
        resultInfo.setSuccess(false);
        resultInfo.setResultCode(resultCode);
        resultInfo.setResultMsg(resultMsg);
        resultInfo.setResultBody(resultBody);
        return resultInfo;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Integer getResultCode() {
        return resultCode;
    }

    public void setResultCode(Integer resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMsg() {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }

    public T getResultBody() {
        return resultBody;
    }

    public void setResultBody(T resultBody) {
        this.resultBody = resultBody;
    }
}
