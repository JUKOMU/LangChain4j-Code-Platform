package io.github.jukomu.langchain4jcodeplatform.constant;

/**
 * @author JUKOMU
 * @Description: APP 常量
 * @Project: LangChain4j-Code-Platform
 * @Date: 2026/3/19
 */
public interface AppConstant {

    /**
     * 精选应用的优先级
     */
    Integer FEATURED_APP_PRIORITY = 99;

    /**
     * 默认应用的优先级
     */
    Integer DEFAULT_APP_PRIORITY = 0;

    /**
     * 应用生成目录
     */
    String CODE_OUTPUT_ROOT_DIR = System.getProperty("user.dir") + "/tmp/code_output";

    /**
     * 应用部署目录
     */
    String CODE_DEPLOY_ROOT_DIR = System.getProperty("user.dir") + "/tmp/code_deploy";

    /**
     * 应用部署域名
     */
    String CODE_DEPLOY_HOST = "http://localhost:8023/deploy";
}
