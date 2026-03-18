package io.github.jukomu.langchain4jcodeplatform.saver;

import cn.hutool.core.util.StrUtil;
import io.github.jukomu.langchain4jcodeplatform.ai.model.HtmlCodeResult;
import io.github.jukomu.langchain4jcodeplatform.exception.ErrorCode;
import io.github.jukomu.langchain4jcodeplatform.model.enums.CodeGenTypeEnum;
import io.github.jukomu.langchain4jcodeplatform.util.ThrowUtils;

/**
 * @author JUKOMU
 * @Description: HTML 代码文件保存模板
 * @Project: LangChain4j-Code-Platform
 * @Date: 2026/3/18
 */
public class HtmlCodeSaverTemplate extends CodeFileSaverTemplate<HtmlCodeResult> {


    @Override
    protected CodeGenTypeEnum getCodeType() {
        return CodeGenTypeEnum.HTML;
    }

    @Override
    protected void saveFiles(HtmlCodeResult result, String baseDirPath) {
        writeToFile(baseDirPath, "index.html", result.getHtmlCode());
    }

    @Override
    protected void validateInput(HtmlCodeResult result) {
        super.validateInput(result);
        ThrowUtils.throwIf(StrUtil.isBlank(result.getHtmlCode()), ErrorCode.SYSTEM_ERROR, "HTML代码内容不能为空");
    }
}
