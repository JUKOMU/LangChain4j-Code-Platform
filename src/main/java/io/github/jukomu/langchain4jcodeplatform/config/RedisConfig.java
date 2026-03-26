package io.github.jukomu.langchain4jcodeplatform.config;

import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import dev.langchain4j.store.memory.chat.InMemoryChatMemoryStore;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author JUKOMU
 * @Description: Redis 对话记忆存储配置
 * @Project: LangChain4j-Code-Platform
 * @Date: 2026/3/25
 */
@Configuration
@ConfigurationProperties(prefix = "spring.data.redis")
@Data
public class RedisConfig {

    private String host;
    private Integer port;
    private Long ttl;

    @Bean
    public ChatMemoryStore redisChatMemoryStore() {
        return new InMemoryChatMemoryStore();
    }
}
