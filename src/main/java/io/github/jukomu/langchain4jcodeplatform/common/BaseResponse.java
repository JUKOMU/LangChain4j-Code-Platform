package io.github.jukomu.langchain4jcodeplatform.common;

import io.github.jukomu.langchain4jcodeplatform.exception.ErrorCode;
import lombok.Data;

import java.io.Serializable;

/**
 * @author JUKOMU
 * @Description: 通用响应类
 * @Project: LangChain4j-Code-Platform
 * @Date: 2026/3/15
 */
@Data
public class BaseResponse<T> implements Serializable {

    private int code;

    private T data;

    private String message;

    public BaseResponse(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public BaseResponse(int code, T data) {
        this(code, data, "");
    }

    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), null, errorCode.getMessage());
    }
}
