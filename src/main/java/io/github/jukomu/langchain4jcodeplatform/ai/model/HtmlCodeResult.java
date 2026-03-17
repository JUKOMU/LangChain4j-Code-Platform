package io.github.jukomu.langchain4jcodeplatform.ai.model;

import dev.langchain4j.model.output.structured.Description;
import lombok.Data;

/**
 * @author JUKOMU
 * @Description:
 * @Project: LangChain4j-Code-Platform
 * @Date: 2026/3/17
 */
@Description("生成 HTML 代码文件的结果")
@Data
public class HtmlCodeResult {

    @Description("HTML代码")
    private String htmlCode;

    @Description("生成代码的描述")
    private String description;
}


