package io.github.jukomu.langchain4jcodeplatform.ai;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.service.AiServices;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author JUKOMU
 * @Description: AI 初始化
 * @Project: LangChain4j-Code-Platform
 * @Date: 2026/3/17
 */
@Configuration
@RequiredArgsConstructor
public class AiCodeGeneratorServiceFactory {

    private final ChatModel chatModel;

    private final StreamingChatModel streamingChatModel;

    @Bean
    public AiCodeGeneratorService aiCodeGeneratorService() {
        return AiServices.builder(AiCodeGeneratorService.class)
                .chatModel(chatModel)
                .streamingChatModel(streamingChatModel)
                .build();
    }
}
