package io.github.jukomu.langchain4jcodeplatform.exception;

import lombok.Getter;

/**
 * @author JUKOMU
 * @Description: 通用业务异常类
 * @Project: LangChain4j-Code-Platform
 * @Date: 2026/3/15
 */
@Getter
public class BusinessException extends RuntimeException {

    /**
     * 错误码
     */
    private final int code;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }
}
