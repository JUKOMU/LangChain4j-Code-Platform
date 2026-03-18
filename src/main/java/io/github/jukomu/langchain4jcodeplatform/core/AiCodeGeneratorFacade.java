package io.github.jukomu.langchain4jcodeplatform.core;

import io.github.jukomu.langchain4jcodeplatform.ai.AiCodeGeneratorService;
import io.github.jukomu.langchain4jcodeplatform.ai.model.HtmlCodeResult;
import io.github.jukomu.langchain4jcodeplatform.ai.model.MultiFileCodeResult;
import io.github.jukomu.langchain4jcodeplatform.exception.BusinessException;
import io.github.jukomu.langchain4jcodeplatform.exception.ErrorCode;
import io.github.jukomu.langchain4jcodeplatform.model.enums.CodeGenTypeEnum;
import io.github.jukomu.langchain4jcodeplatform.util.ThrowUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.File;

/**
 * @author JUKOMU
 * @Description: AI 服务门面类，组合生成和保存功能
 * @Project: LangChain4j-Code-Platform
 * @Date: 2026/3/18
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiCodeGeneratorFacade {

    private final AiCodeGeneratorService aiCodeGeneratorService;

    /**
     * 生成代码并保存
     *
     * @param userMessage
     * @param codeGenTypeEnum
     * @return
     */
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
     * 生成 HTML 模式的代码并保存
     *
     * @param userMessage
     * @return
     */
    private File generateAndSaveHtmlCode(String userMessage) {
        HtmlCodeResult result = aiCodeGeneratorService.generateHtmlCode(userMessage);
        return CodeFileSaver.saveHtmlCodeResult(result);
    }

    /**
     * 生成多文件模式的代码并保存
     *
     * @param userMessage
     * @return
     */
    private File generateAndSaveMultiFileCode(String userMessage) {
        MultiFileCodeResult result = aiCodeGeneratorService.generateHtmlMultiFileCode(userMessage);
        return CodeFileSaver.saveMultiFileCodeResult(result);
    }

    /**
     * 生成代码并保存 (流式)
     *
     * @param userMessage
     * @param codeGenTypeEnum
     * @return
     */
    public Flux<String> streamGenerateAndSaveCode(String userMessage, CodeGenTypeEnum codeGenTypeEnum) {
        ThrowUtils.throwIf(codeGenTypeEnum == null, ErrorCode.SYSTEM_ERROR, "生成类型不能为空");
        return switch (codeGenTypeEnum) {
            case HTML -> streamGenerateAndSaveHtmlCode(userMessage);
            case MULTI_FILE -> streamGenerateAndSaveMultiFileCode(userMessage);
            default -> {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "不支持的生成类型: " + codeGenTypeEnum.getValue());
            }
        };
    }

    /**
     * 生成 HTML 模式的代码并保存 (流式)
     *
     * @param userMessage
     * @return
     */
    private Flux<String> streamGenerateAndSaveHtmlCode(String userMessage) {
        Flux<String> stream = aiCodeGeneratorService.streamGenerateHtmlCode(userMessage);
        StringBuilder stringBuilder = new StringBuilder();
        return stream
                .doOnNext(chunk -> stringBuilder.append(chunk))
                .doOnComplete(() -> {
                    try {
                        String string = stringBuilder.toString();
                        HtmlCodeResult result = CodeParser.parseHtmlCode(string);
                        CodeFileSaver.saveHtmlCodeResult(result);
                    } catch (Exception e) {
                        log.error("保存失败: {}", e.getMessage());
                    }
                });
    }

    /**
     * 生成多文件模式的代码并保存 (流式)
     *
     * @param userMessage
     * @return
     */
    private Flux<String> streamGenerateAndSaveMultiFileCode(String userMessage) {
        Flux<String> stream = aiCodeGeneratorService.streamGenerateHtmlMultiFileCode(userMessage);
        StringBuilder stringBuilder = new StringBuilder();
        return stream
                .doOnNext(chunk -> stringBuilder.append(chunk))
                .doOnComplete(() -> {
                    try {
                        String string = stringBuilder.toString();
                        MultiFileCodeResult result = CodeParser.parseMultiFileCode(string);
                        CodeFileSaver.saveMultiFileCodeResult(result);
                    } catch (Exception e) {
                        log.error("保存失败: {}", e.getMessage());
                    }
                });
    }
}
