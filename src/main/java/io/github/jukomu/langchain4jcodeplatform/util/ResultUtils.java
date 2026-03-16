package io.github.jukomu.langchain4jcodeplatform.util;

import io.github.jukomu.langchain4jcodeplatform.common.BaseResponse;
import io.github.jukomu.langchain4jcodeplatform.exception.ErrorCode;

/**
 * @author JUKOMU
 * @Description:
 * @Project: LangChain4j-Code-Platform
 * @Date: 2026/3/15
 */
public class ResultUtils {

    /**
     * 成功
     *
     * @param data 数据
     * @param <T>  数据类型
     * @return 成功响应
     */
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(200, data, "ok");
    }

    /**
     * 失败
     *
     * @param errorCode 错误码
     * @return 失败响应
     */
    public static BaseResponse<?> error(ErrorCode errorCode) {
        return new BaseResponse<>(errorCode);
    }

    /**
     * 失败
     *
     * @param errorCode 错误码
     * @param message   错误信息
     * @return 失败响应
     */
    public static BaseResponse<?> error(ErrorCode errorCode, String message) {
        return new BaseResponse<>(errorCode.getCode(), null, message);
    }

    /**
     * 失败
     *
     * @param code    错误码
     * @param message 错误信息
     * @return 失败响应
     */
    public static BaseResponse<?> error(int code, String message) {
        return new BaseResponse<>(code, null, message);
    }
}
