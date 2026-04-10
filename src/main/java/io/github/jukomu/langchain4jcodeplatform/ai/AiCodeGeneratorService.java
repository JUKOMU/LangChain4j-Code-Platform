package io.github.jukomu.langchain4jcodeplatform.ai;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;
import io.github.jukomu.langchain4jcodeplatform.ai.model.HtmlCodeResult;
import io.github.jukomu.langchain4jcodeplatform.ai.model.MultiFileCodeResult;
import reactor.core.publisher.Flux;

/**
 * @author JUKOMU
 * @Description: AI 服务接口
 * @Project: LangChain4j-Code-Platform
 * @Date: 2026/3/17
 */
public interface AiCodeGeneratorService {

    String generateCode(String userMessage);

    /**
     * 生成HTML代码
     *
     * @param userMessage
     * @return
     */
    @SystemMessage(fromResource = "prompt/codegen-html-system-prompt.txt")
    HtmlCodeResult generateHtmlCode(String userMessage);

    /**
     * 生成多文件代码
     *
     * @param userMessage
     * @return
     */
    @SystemMessage(fromResource = "prompt/codegen-multi-file-system-prompt.txt")
    MultiFileCodeResult generateHtmlMultiFileCode(String userMessage);

    /**
     * 生成HTML代码 (流式)
     *
     * @param userMessage
     * @return
     */
    @SystemMessage(fromResource = "prompt/codegen-html-system-prompt.txt")
    Flux<String> streamGenerateHtmlCode(String userMessage);

    /**
     * 生成多文件代码 (流式)
     *
     * @param userMessage
     * @return
     */
    @SystemMessage(fromResource = "prompt/codegen-multi-file-system-prompt.txt")
    Flux<String> streamGenerateHtmlMultiFileCode(String userMessage);

    /**
     * 生成 Vue 项目代码 (流式)
     *
     * @param userMessage
     * @return
     */
    @SystemMessage(fromResource = "prompt/codegen-vue-project-system-prompt.txt")
    TokenStream streamGenerateVueProjectCode(@MemoryId Long appId, @UserMessage String userMessage);

}
