package io.github.jukomu.langchain4jcodeplatform.core.handler;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import io.github.jukomu.langchain4jcodeplatform.ai.message.*;
import io.github.jukomu.langchain4jcodeplatform.model.entity.User;
import io.github.jukomu.langchain4jcodeplatform.model.enums.ChatHistoryMessageTypeEnum;
import io.github.jukomu.langchain4jcodeplatform.service.ChatHistoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.HashSet;
import java.util.Set;

/**
 * @author JUKOMU
 * @Description: JSON 流消息处理器
 * @Project: LangChain4j-Code-Platform
 * @Date: 2026/4/10
 */
@Slf4j
@Component
public class JsonMessageStreamHandler {

    public Flux<String> handle(Flux<String> originFlux,
                               ChatHistoryService chatHistoryService,
                               long appId,
                               User loginUser,
                               long parentMessageId) {
        StringBuilder chatHistoryBuilder = new StringBuilder();
        Set<String> toolIds = new HashSet<>();
        return originFlux.map(chunk -> {
                    // 解析每个JSON
                    return handleJsonMessageChunk(chunk, chatHistoryBuilder, toolIds);
                })
                .filter(StrUtil::isNotEmpty)
                .doOnComplete(() -> chatHistoryService.saveMessage(appId, loginUser.getId(),
                        chatHistoryBuilder.toString(), ChatHistoryMessageTypeEnum.AI, parentMessageId))
                .doOnError(throwable -> chatHistoryService.saveMessage(appId, loginUser.getId(),
                        "AI 回复失败: " + ExceptionUtil.getRootCauseMessage(throwable),
                        ChatHistoryMessageTypeEnum.AI_ERROR, parentMessageId));
    }

    /**
     * 收集并解析 TokenStream 的数据
     * @param chunk
     * @param chatHistoryBuilder
     * @param toolIds
     * @return
     */
    private String handleJsonMessageChunk(String chunk, StringBuilder chatHistoryBuilder, Set<String> toolIds) {
        // 解析 JSON
        StreamMessage streamMessage = JSONUtil.toBean(chunk, StreamMessage.class);
        StreamMessageTypeEnum streamMessageTypeEnum = StreamMessageTypeEnum.fromValue(streamMessage.getType());
        switch (streamMessageTypeEnum) {
            case AI_RESPONSE -> {
                AiResponseMessage aiResponseMessage = JSONUtil.toBean(chunk, AiResponseMessage.class);
                String data = aiResponseMessage.getData();
                chatHistoryBuilder.append(data);
                return data;
            }
            case TOOL_REQUEST -> {
                ToolRequestMessage toolRequestMessage = JSONUtil.toBean(chunk, ToolRequestMessage.class);
                String id = toolRequestMessage.getId();
                if (id != null && !toolIds.contains(id)) {
                    // 首次调用，记录id并完整返回工具信息
                    toolIds.add(id);
                    return "\n\n[选择工具] 写入文件\n\n";
                } else {
                    return "";
                }
            }
            case TOOL_EXECUTED -> {
                ToolExecutedMessage toolExecutedMessage = JSONUtil.toBean(chunk, ToolExecutedMessage.class);
                JSONObject jsonObject = JSONUtil.parseObj(toolExecutedMessage.getArguments());
                String relativePath = jsonObject.getStr("relativePath");
                String suffix = FileUtil.getSuffix(relativePath);
                String content = jsonObject.getStr("content");
                String result = String.format("""
                        [工具调用] 写入文件 %s
                        ```%s
                        %s
                        ```
                        
                        """, relativePath, suffix, content);
                // 输出前端和要持久化的内容
                String output = String.format("\n\n%s\n\n", result);
                chatHistoryBuilder.append(output);
                return output;
            }
            default -> {
                log.error("不支持的消息类型: {}", streamMessageTypeEnum);
                return "";
            }
        }
    }
}
