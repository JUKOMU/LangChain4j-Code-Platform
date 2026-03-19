package io.github.jukomu.langchain4jcodeplatform.model.dto.app;

import lombok.Data;

import java.io.Serializable;

/**
 * @author JUKOMU
 * @Description: APP 管理员查询数据传输对象
 * @Project: LangChain4j-Code-Platform
 * @Date: 2026/3/18
 */
@Data
public class AppAdminUpdateDto implements Serializable {

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
     * 优先级
     */
    private Integer priority;
}
