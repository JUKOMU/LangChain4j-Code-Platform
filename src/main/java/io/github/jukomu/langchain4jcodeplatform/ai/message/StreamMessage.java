package io.github.jukomu.langchain4jcodeplatform.ai.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author JUKOMU
 * @Description: 流式消息响应基类
 * @Project: LangChain4j-Code-Platform
 * @Date: 2026/4/10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StreamMessage {

    private String type;
}
