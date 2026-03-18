package io.github.jukomu.langchain4jcodeplatform;

import io.github.jukomu.langchain4jcodeplatform.core.AiCodeGeneratorFacade;
import io.github.jukomu.langchain4jcodeplatform.model.enums.CodeGenTypeEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

import java.io.File;

/**
 * @author JUKOMU
 * @Description: 门面模式测试
 * @Project: LangChain4j-Code-Platform
 * @Date: 2026/3/18
 */
@SpringBootTest
public class AiCodeGeneratorFacadeTest {

    @Autowired
    private AiCodeGeneratorFacade aiCodeGeneratorFacade;

    @Test
    public void generateAndSaveCode() {
        File file = aiCodeGeneratorFacade.generateAndSaveCode("任务记录网站", CodeGenTypeEnum.MULTI_FILE);
        Assertions.assertNotNull(file);
    }

    @Test
    public void streamGenerateAndSaveCode() {
        Flux<String> stream = aiCodeGeneratorFacade.streamGenerateAndSaveCode("任务记录网站", CodeGenTypeEnum.MULTI_FILE);
        // 阻塞
        stream.doOnNext(chunk -> System.out.print(chunk))
                .blockLast();
    }
}
