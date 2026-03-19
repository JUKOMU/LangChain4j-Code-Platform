package io.github.jukomu.langchain4jcodeplatform.model.dto.app;

import lombok.Data;

import java.io.Serializable;

/**
 * @author JUKOMU
 * @Description: APP 新增数据传输对象
 * @Project: LangChain4j-Code-Platform
 * @Date: 2026/3/18
 */
@Data
public class AppAddDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 应用封面
     */
    private String cover;

    /**
     * 应用初始化 prompt
     */
    private String initPrompt;

    /**
     * 代码生成类型
     */
    private String codeGenType;
}
