package io.github.jukomu.langchain4jcodeplatform.saver;

import io.github.jukomu.langchain4jcodeplatform.ai.model.HtmlCodeResult;
import io.github.jukomu.langchain4jcodeplatform.ai.model.MultiFileCodeResult;
import io.github.jukomu.langchain4jcodeplatform.exception.BusinessException;
import io.github.jukomu.langchain4jcodeplatform.exception.ErrorCode;
import io.github.jukomu.langchain4jcodeplatform.model.enums.CodeGenTypeEnum;

import java.io.File;

/**
 * @author JUKOMU
 * @Description: 代码文件保存执行器，根据代码生成类型执行相应的保存逻辑
 * @Project: LangChain4j-Code-Platform
 * @Date: 2026/3/18
 */
public class CodeFileSaverExecutor {

    private static final HtmlCodeSaverTemplate htmlCodeFileSaver = new HtmlCodeSaverTemplate();

    private static final MultiFileCodeFileSaverTemplate multiFileCodeFileSaver = new MultiFileCodeFileSaverTemplate();

    /**
     * 执行代码保存
     *
     * @param codeResult  代码结果对象
     * @param codeGenType 代码生成类型
     * @param appId       应用 id
     * @return 保存的目录
     */
    public static File executeSaver(Object codeResult, CodeGenTypeEnum codeGenType, Long appId) {
        return switch (codeGenType) {
            case HTML -> htmlCodeFileSaver.saveCode((HtmlCodeResult) codeResult, appId);
            case MULTI_FILE -> multiFileCodeFileSaver.saveCode((MultiFileCodeResult) codeResult, appId);
            default -> throw new BusinessException(ErrorCode.SYSTEM_ERROR, "不支持的代码生成类型: " + codeGenType);
        };
    }
}

