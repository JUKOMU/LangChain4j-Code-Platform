package io.github.jukomu.langchain4jcodeplatform.ai;

import dev.langchain4j.service.SystemMessage;

/**
 * @author JUKOMU
 * @Description: AI 服务接口
 * @Project: LangChain4j-Code-Platform
 * @Date: 2026/3/17
 */
public interface AiCodeGeneratorService {

    String generateCode(String userMessage);

    @SystemMessage(fromResource = "prompt/codegen-html-system-prompt.txt")
    String generateHtmlCode(String userMessage);

    @SystemMessage(fromResource = "prompt/codegen-multi-file-system-prompt.txt")
    String generateHtmlMultiFileCode(String userMessage);
}
