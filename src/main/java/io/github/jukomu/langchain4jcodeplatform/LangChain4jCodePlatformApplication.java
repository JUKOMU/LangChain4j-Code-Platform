package io.github.jukomu.langchain4jcodeplatform;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@MapperScan("io.github.jukomu.langchain4jcodeplatform.mapper")
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class LangChain4jCodePlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(LangChain4jCodePlatformApplication.class, args);
    }

}
