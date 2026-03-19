package io.github.jukomu.langchain4jcodeplatform.model.dto.app;

import io.github.jukomu.langchain4jcodeplatform.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author JUKOMU
 * @Description: APP 查询数据传输对象
 * @Project: LangChain4j-Code-Platform
 * @Date: 2026/3/18
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AppQueryDto extends PageRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

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

    /**
     * 部署标识
     */
    private String deployKey;

    /**
     * 优先级
     */
    private Integer priority;

    /**
     * 用户id
     */
    private Long userId;
}
