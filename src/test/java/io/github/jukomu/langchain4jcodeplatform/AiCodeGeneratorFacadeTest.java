package io.github.jukomu.langchain4jcodeplatform;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import io.github.jukomu.langchain4jcodeplatform.core.AiCodeGeneratorFacade;
import io.github.jukomu.langchain4jcodeplatform.model.enums.CodeGenTypeEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

import java.io.File;
import java.util.List;

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
        File file = aiCodeGeneratorFacade.generateAndSaveCode("任务记录网站", CodeGenTypeEnum.MULTI_FILE, RandomUtil.randomLong());
        Assertions.assertNotNull(file);
    }

    @Test
    public void streamGenerateAndSaveCode() {
        Flux<String> stream = aiCodeGeneratorFacade.streamGenerateAndSaveCode("任务记录网站", CodeGenTypeEnum.MULTI_FILE, RandomUtil.randomLong());
        // 阻塞
        stream.doOnNext(chunk -> System.out.print(chunk))
                .blockLast();
    }

    @Test
    void generateVueProjectCodeStream() {
        Flux<String> codeStream = aiCodeGeneratorFacade.streamGenerateAndSaveCode(
                "简单的任务记录网站，总代码量不超过 200 行",
                CodeGenTypeEnum.VUE_PROJECT, 1L);
        // 阻塞等待所有数据收集完成
        List<String> result = codeStream.collectList().block();
        // 验证结果
        Assertions.assertNotNull(result);
        String completeContent = String.join("", result);
        Assertions.assertNotNull(completeContent);
    }

}
