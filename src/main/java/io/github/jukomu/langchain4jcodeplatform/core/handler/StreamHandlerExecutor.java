package io.github.jukomu.langchain4jcodeplatform.core.handler;

import io.github.jukomu.langchain4jcodeplatform.model.entity.User;
import io.github.jukomu.langchain4jcodeplatform.model.enums.CodeGenTypeEnum;
import io.github.jukomu.langchain4jcodeplatform.service.ChatHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

/**
 * @author JUKOMU
 * @Description: 流处理器执行器
 * @Project: LangChain4j-Code-Platform
 * @Date: 2026/4/10
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StreamHandlerExecutor {

    private final JsonMessageStreamHandler  jsonMessageStreamHandler;
    private final SimpleTextStreamHandler simpleTextStreamHandler;

    /**
     * 创建流处理器并处理聊天历史记录
     *
     * @param originFlux         原始流
     * @param chatHistoryService 聊天历史服务
     * @param appId              应用ID
     * @param loginUser          登录用户
     * @param codeGenType        代码生成类型
     * @return 处理后的流
     */
    public Flux<String> doExecute(Flux<String> originFlux,
                                  ChatHistoryService chatHistoryService,
                                  long appId,
                                  User loginUser,
                                  CodeGenTypeEnum codeGenType,
                                  long parentMessageId) {
        return switch (codeGenType) {
            case VUE_PROJECT -> // 使用注入的组件实例
                    jsonMessageStreamHandler.handle(originFlux, chatHistoryService, appId, loginUser, parentMessageId);
            case HTML, MULTI_FILE -> // 简单文本处理器不需要依赖注入
                    new SimpleTextStreamHandler().handle(originFlux, chatHistoryService, appId, loginUser, parentMessageId);
        };
    }
}
