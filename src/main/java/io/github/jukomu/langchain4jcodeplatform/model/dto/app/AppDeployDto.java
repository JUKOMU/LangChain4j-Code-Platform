package io.github.jukomu.langchain4jcodeplatform.model.dto.app;

import lombok.Data;

import java.io.Serializable;

/**
 * @author JUKOMU
 * @Description: APP 部署请求数据传输对象
 * @Project: LangChain4j-Code-Platform
 * @Date: 2026/3/21
 */
@Data
public class AppDeployDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 应用 id
     */
    private Long appId;
}
