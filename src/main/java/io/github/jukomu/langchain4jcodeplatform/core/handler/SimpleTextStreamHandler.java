package io.github.jukomu.langchain4jcodeplatform.core.handler;

import cn.hutool.core.exceptions.ExceptionUtil;
import io.github.jukomu.langchain4jcodeplatform.model.entity.User;
import io.github.jukomu.langchain4jcodeplatform.model.enums.ChatHistoryMessageTypeEnum;
import io.github.jukomu.langchain4jcodeplatform.service.ChatHistoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

/**
 * @author JUKOMU
 * @Description: 简单文本流处理器
 * @Project: LangChain4j-Code-Platform
 * @Date: 2026/4/10
 */
@Slf4j
@Component
public class SimpleTextStreamHandler {

    /**
     * 处理 HTML, MULTI_FILE 的消息流
     *
     * @param originFlux
     * @param chatHistoryService
     * @param appId
     * @param loginUser
     * @param parentMessageId
     * @return
     */
    public Flux<String> handle(Flux<String> originFlux,
                               ChatHistoryService chatHistoryService,
                               long appId,
                               User loginUser,
                               long parentMessageId) {
        StringBuilder aiReplyBuilder = new StringBuilder();
        return originFlux.map(chunk -> {
                    // 收集ai响应内容
                    aiReplyBuilder.append(chunk);
                    return chunk;
                })
                .doOnComplete(() -> chatHistoryService.saveMessage(appId, loginUser.getId(),
                        aiReplyBuilder.toString(), ChatHistoryMessageTypeEnum.AI, parentMessageId))
                .doOnError(throwable -> chatHistoryService.saveMessage(appId, loginUser.getId(),
                        "AI 回复失败: " + ExceptionUtil.getRootCauseMessage(throwable),
                        ChatHistoryMessageTypeEnum.AI_ERROR, parentMessageId));
    }
}
