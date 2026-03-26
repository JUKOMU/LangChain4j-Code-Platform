package io.github.jukomu.langchain4jcodeplatform;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@MapperScan("io.github.jukomu.langchain4jcodeplatform.mapper")
@SpringBootApplication(exclude = {dev.langchain4j.community.store.embedding.redis.spring.RedisEmbeddingStoreAutoConfiguration.class})
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class LangChain4jCodePlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(LangChain4jCodePlatformApplication.class, args);
    }

}
