package io.github.jukomu.langchain4jcodeplatform.ai.message;

import lombok.Getter;

/**
 * @author JUKOMU
 * @Description: 流式消息类型枚举
 * @Project: LangChain4j-Code-Platform
 * @Date: 2026/4/10
 */
@Getter
public enum StreamMessageTypeEnum {

    AI_RESPONSE("ai_response", "Ai 响应"),
    TOOL_REQUEST("tool_request", "工具调用"),
    TOOL_EXECUTED("tool_executed", "工具执行结果");

    private final String value;
    private final String text;

    StreamMessageTypeEnum(String value, String text) {
        this.value = value;
        this.text = text;
    }

    /**
     * 根据值获取枚举
     *
     * @param value
     * @return
     */
    public static StreamMessageTypeEnum fromValue(String value) {
        for (StreamMessageTypeEnum e : StreamMessageTypeEnum.values()) {
            if (e.getValue().equals(value)) {
                return e;
            }
        }
        return null;
    }
}
