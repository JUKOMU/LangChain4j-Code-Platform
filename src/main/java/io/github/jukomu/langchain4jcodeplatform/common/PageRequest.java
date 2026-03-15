package io.github.jukomu.langchain4jcodeplatform.common;

import lombok.Data;

/**
 * @author JUKOMU
 * @Description: 分页请求包装类
 * @Project: LangChain4j-Code-Platform
 * @Date: 2026/3/15
 */
@Data
public class PageRequest {

    /**
     * 当前页号
     */
    private int pageNum = 1;

    /**
     * 页面大小
     */
    private int pageSize = 10;

    /**
     * 排序字段
     */
    private String sortField;

    /**
     * 排序顺序（默认降序）
     */
    private String sortOrder = "descend";
}

