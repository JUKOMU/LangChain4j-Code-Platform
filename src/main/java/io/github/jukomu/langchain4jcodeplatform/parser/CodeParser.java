package io.github.jukomu.langchain4jcodeplatform.parser;

/**
 * @author JUKOMU
 * @Description:
 * @Project: LangChain4j-Code-Platform
 * @Date: 2026/3/18
 */
public interface CodeParser<T> {

    T parseCode(String content);
}
