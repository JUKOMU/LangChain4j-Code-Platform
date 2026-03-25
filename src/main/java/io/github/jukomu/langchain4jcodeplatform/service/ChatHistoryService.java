package io.github.jukomu.langchain4jcodeplatform.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import io.github.jukomu.langchain4jcodeplatform.model.dto.chatHistory.AppChatHistoryQueryDto;
import io.github.jukomu.langchain4jcodeplatform.model.dto.chatHistory.ChatHistoryQueryDto;
import io.github.jukomu.langchain4jcodeplatform.model.entity.ChatHistory;
import io.github.jukomu.langchain4jcodeplatform.model.enums.ChatHistoryMessageTypeEnum;
import io.github.jukomu.langchain4jcodeplatform.model.vo.AppChatHistoryPageVo;
import io.github.jukomu.langchain4jcodeplatform.model.vo.ChatHistoryVo;
import io.github.jukomu.langchain4jcodeplatform.model.vo.LoginUserVo;

/**
 * 对话历史 服务层
 *
 * @author JUKOMU
 */
public interface ChatHistoryService extends IService<ChatHistory> {

    /**
     * 保存对话
     *
     * @param appId       应用 id
     * @param userId      用户 id
     * @param message     消息
     * @param messageType 消息类型
     * @param parentId    父消息 id
     * @return 被记录消息 id
     */
    long saveMessage(Long appId, Long userId, String message, ChatHistoryMessageTypeEnum messageType, Long parentId);

    /**
     * 获取最新消息 id
     *
     * @param appId 应用 id
     * @return 获取最新消息 id
     */
    Long getLatestMessageId(Long appId);

    /**
     * 查询应用的对话历史
     *
     * @param appChatHistoryQueryDto
     * @param loginUser
     * @return
     */
    AppChatHistoryPageVo getAppChatHistories(AppChatHistoryQueryDto appChatHistoryQueryDto, LoginUserVo loginUser);

    /**
     * 查询所有对话历史 (管理员)
     *
     * @param chatHistoryQueryDto
     * @return
     */
    Page<ChatHistoryVo> getChatHistories(ChatHistoryQueryDto chatHistoryQueryDto);

    /**
     * 删除应用对话历史
     *
     * @param appId 应用 id
     * @return
     */
    boolean deleteByAppId(Long appId);
}
