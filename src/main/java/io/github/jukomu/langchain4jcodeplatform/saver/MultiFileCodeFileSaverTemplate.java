package io.github.jukomu.langchain4jcodeplatform.saver;

import cn.hutool.core.util.StrUtil;
import io.github.jukomu.langchain4jcodeplatform.ai.model.MultiFileCodeResult;
import io.github.jukomu.langchain4jcodeplatform.exception.ErrorCode;
import io.github.jukomu.langchain4jcodeplatform.model.enums.CodeGenTypeEnum;
import io.github.jukomu.langchain4jcodeplatform.util.ThrowUtils;

/**
 * @author JUKOMU
 * @Description: 多文件代码文件保存模板
 * @Project: LangChain4j-Code-Platform
 * @Date: 2026/3/18
 */
public class MultiFileCodeFileSaverTemplate extends CodeFileSaverTemplate<MultiFileCodeResult> {
    @Override
    protected CodeGenTypeEnum getCodeType() {
        return CodeGenTypeEnum.MULTI_FILE;
    }

    @Override
    protected void saveFiles(MultiFileCodeResult result, String baseDirPath) {
        writeToFile(baseDirPath, "index.html", result.getHtmlCode());
        writeToFile(baseDirPath, "style.css", result.getCssCode());
        writeToFile(baseDirPath, "script.js", result.getJsCode());

    }

    @Override
    protected void validateInput(MultiFileCodeResult result) {
        super.validateInput(result);
        ThrowUtils.throwIf(StrUtil.isBlank(result.getHtmlCode()), ErrorCode.SYSTEM_ERROR, "HTML代码内容不能为空");
    }
}
