package io.github.jukomu.langchain4jcodeplatform.ai;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import dev.langchain4j.community.store.memory.chat.redis.RedisChatMemoryStore;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.service.AiServices;
import io.github.jukomu.langchain4jcodeplatform.service.ChatHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * @author JUKOMU
 * @Description: AI 初始化
 * @Project: LangChain4j-Code-Platform
 * @Date: 2026/3/17
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class AiCodeGeneratorServiceFactory {

    private final ChatModel chatModel;

    private final StreamingChatModel streamingChatModel;

    private final RedisChatMemoryStore redisChatMemoryStore;

    private final ChatHistoryService chatHistoryService;

    @Bean
    public AiCodeGeneratorService aiCodeGeneratorService() {
        return getAiCodeGeneratorService(0L);
    }

    /**
     * 根据 appId 分配服务
     *
     * @param appId
     * @return
     */
    public AiCodeGeneratorService getAiCodeGeneratorService(long appId) {
        return aiCodeGeneratorServiceCache.get(appId, this::createAiCodeGeneratorService);
    }

    /**
     * AI 服务实例缓存
     * 缓存策略:
     * - 最大 1000 个实例
     * - 写入后 30 分钟过期
     * - 访问后 10 分钟过期
     */
    private final Cache<Long, AiCodeGeneratorService> aiCodeGeneratorServiceCache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(Duration.ofMinutes(30))
            .expireAfterAccess(Duration.ofMinutes(10))
            .removalListener((key, value, cause) -> {
                log.debug("AI 服务实例被移除, appId: {}, 原因: {}", key, cause);
            })
            .build();

    /**
     * 创建新的 AI 服务
     *
     * @param appId
     * @return
     */
    private AiCodeGeneratorService createAiCodeGeneratorService(long appId) {
        log.info("为 {} 创建新的 AI 服务实例", appId);
        MessageWindowChatMemory chatMemory = MessageWindowChatMemory.builder()
                .id(appId)
                .chatMemoryStore(redisChatMemoryStore)
                .maxMessages(30)
                .build();
        chatHistoryService.loadChatHistoryToMemory(appId, chatMemory, 30);
        return AiServices.builder(AiCodeGeneratorService.class)
                .chatModel(chatModel)
                .streamingChatModel(streamingChatModel)
                .chatMemory(chatMemory)
                .build();
    }
}
