package io.github.jukomu.langchain4jcodeplatform.core;

import io.github.jukomu.langchain4jcodeplatform.ai.AiCodeGeneratorService;
import io.github.jukomu.langchain4jcodeplatform.ai.model.HtmlCodeResult;
import io.github.jukomu.langchain4jcodeplatform.ai.model.MultiFileCodeResult;
import io.github.jukomu.langchain4jcodeplatform.exception.BusinessException;
import io.github.jukomu.langchain4jcodeplatform.exception.ErrorCode;
import io.github.jukomu.langchain4jcodeplatform.model.enums.CodeGenTypeEnum;
import io.github.jukomu.langchain4jcodeplatform.util.ThrowUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * @author JUKOMU
 * @Description: AI 服务门面类，组合生成和保存功能
 * @Project: LangChain4j-Code-Platform
 * @Date: 2026/3/18
 */
@Service
@RequiredArgsConstructor
public class AiCodeGeneratorFacade {

    private final AiCodeGeneratorService aiCodeGeneratorService;

    public File generateAndSaveCode(String userMessage, CodeGenTypeEnum codeGenTypeEnum) {
        ThrowUtils.throwIf(codeGenTypeEnum == null, ErrorCode.SYSTEM_ERROR, "生成类型不能为空");
        return switch (codeGenTypeEnum) {
            case HTML -> generateAndSaveHtmlCode(userMessage);
            case MULTI_FILE -> generateAndSaveMultiFileCode(userMessage);
            default -> {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "不支持的生成类型: " + codeGenTypeEnum.getValue());
            }
        };
    }

    /**
     * 生成 HTML 描述的代码并保存
     *
     * @param userMessage
     * @return
     */
    private File generateAndSaveHtmlCode(String userMessage) {
        HtmlCodeResult result = aiCodeGeneratorService.generateHtmlCode(userMessage);
        return CodeFileSaver.saveHtmlCodeResult(result);
    }

    /**
     * 生成多文件描述的代码并保存
     *
     * @param userMessage
     * @return
     */
    private File generateAndSaveMultiFileCode(String userMessage) {
        MultiFileCodeResult result = aiCodeGeneratorService.generateHtmlMultiFileCode(userMessage);
        return CodeFileSaver.saveMultiFileCodeResult(result);
    }
}
