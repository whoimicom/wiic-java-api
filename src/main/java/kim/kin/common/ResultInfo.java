
package kim.kin.common;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serial;
import java.io.Serializable;

/**
 * 统一返回信息
 *
 * @author kin.kim
 * @since 2023-10-12
 **/
public class ResultInfo<T> implements Serializable {

    @Serial
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
    private T data;

    public ResultInfo() {
        super();
    }

    public ResultInfo(Boolean success) {
        super();
        this.success = success;
    }

    public ResultInfo(T data) {
        super();
        this.data = data;
    }

    public ResultInfo(Boolean success, T data) {
        super();
        this.success = success;
        this.data = data;
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

    public ResultInfo(Integer resultCode, String resultMsg, T data) {
        this(resultCode, resultMsg);
        this.data = data;
    }

    public ResultInfo(Boolean success, Integer resultCode, String resultMsg, T data) {
        this(success, resultCode, resultMsg);
        this.data = data;
    }


    /**
     * @return 成功信息, 不包含DATA
     */
    public static <T> ResultInfo<T> ok() {
        return new ResultInfo<>(true);
    }

    /**
     * @param data data
     * @return 成功包含data
     */
    public static <T> ResultInfo<T> ok(T data) {
        return new ResultInfo<>(true, data);
    }

    /**
     * 成功包含所有信息【不建议使用，成功一般不设置CODE MESSAGE】
     *
     * @param resultCode    resultCode
     * @param resultMessage resultMessage
     * @param data          data
     * @return 成功包含所有信息
     */
    @Deprecated
    public static <T> ResultInfo<T> ok(Integer resultCode, String resultMessage, T data) {
        return new ResultInfo<>(true, resultCode, resultMessage, data);
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
        resultInfo.setData(resultBody);
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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
