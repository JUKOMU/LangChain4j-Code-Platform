package io.github.jukomu.langchain4jcodeplatform.ai.message;

import dev.langchain4j.agent.tool.ToolExecutionRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author JUKOMU
 * @Description: 工具调用消息
 * @Project: LangChain4j-Code-Platform
 * @Date: 2026/4/10
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class ToolRequestMessage extends StreamMessage {

    private String id;
    private String name;
    private String arguments;

    public ToolRequestMessage(ToolExecutionRequest toolExecutionRequest) {
        super(StreamMessageTypeEnum.TOOL_REQUEST.getValue());
        this.id = toolExecutionRequest.id();
        this.name = toolExecutionRequest.name();
        this.arguments = toolExecutionRequest.arguments();
    }
}
