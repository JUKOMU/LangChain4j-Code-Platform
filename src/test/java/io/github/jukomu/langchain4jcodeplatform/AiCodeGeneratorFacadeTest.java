package io.github.jukomu.langchain4jcodeplatform;

import io.github.jukomu.langchain4jcodeplatform.core.AiCodeGeneratorFacade;
import io.github.jukomu.langchain4jcodeplatform.model.enums.CodeGenTypeEnum;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;

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
}
