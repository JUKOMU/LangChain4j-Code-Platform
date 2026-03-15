package io.github.jukomu.langchain4jcodeplatform.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @author JUKOMU
 * @Description: 删除请求包装类
 * @Project: LangChain4j-Code-Platform
 * @Date: 2026/3/15
 */
@Data
public class DeleteRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    private static final long serialVersionUID = 1L;
}

