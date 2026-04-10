package io.github.jukomu.langchain4jcodeplatform.ai.message;

import dev.langchain4j.service.tool.ToolExecution;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author JUKOMU
 * @Description: 工具执行结果消息
 * @Project: LangChain4j-Code-Platform
 * @Date: 2026/4/10
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class ToolExecutedMessage extends StreamMessage {

    private String id;
    private String name;
    private String arguments;
    private String result;

    public ToolExecutedMessage(ToolExecution toolExecution) {
        super(StreamMessageTypeEnum.TOOL_EXECUTED.getValue());
        this.id = toolExecution.request().id();
        this.name = toolExecution.request().name();
        this.arguments = toolExecution.request().arguments();
        this.result = toolExecution.result();
    }
}
