package io.github.jukomu.langchain4jcodeplatform.ai.message;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author JUKOMU
 * @Description: Ai 响应消息
 * @Project: LangChain4j-Code-Platform
 * @Date: 2026/4/10
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class AiResponseMessage extends StreamMessage {

    private String data;

    public AiResponseMessage(String data) {
        super(StreamMessageTypeEnum.AI_RESPONSE.getValue());
        this.data = data;
    }
}
