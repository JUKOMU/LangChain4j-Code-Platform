package io.github.jukomu.langchain4jcodeplatform.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import io.github.jukomu.langchain4jcodeplatform.exception.BusinessException;
import io.github.jukomu.langchain4jcodeplatform.exception.ErrorCode;
import io.github.jukomu.langchain4jcodeplatform.mapper.AppMapper;
import io.github.jukomu.langchain4jcodeplatform.mapper.ChatHistoryMapper;
import io.github.jukomu.langchain4jcodeplatform.model.dto.chatHistory.AppChatHistoryQueryDto;
import io.github.jukomu.langchain4jcodeplatform.model.dto.chatHistory.ChatHistoryQueryDto;
import io.github.jukomu.langchain4jcodeplatform.model.entity.App;
import io.github.jukomu.langchain4jcodeplatform.model.entity.ChatHistory;
import io.github.jukomu.langchain4jcodeplatform.model.entity.User;
import io.github.jukomu.langchain4jcodeplatform.model.enums.ChatHistoryMessageTypeEnum;
import io.github.jukomu.langchain4jcodeplatform.model.enums.UserRoleEnum;
import io.github.jukomu.langchain4jcodeplatform.model.vo.*;
import io.github.jukomu.langchain4jcodeplatform.service.ChatHistoryService;
import io.github.jukomu.langchain4jcodeplatform.service.UserService;
import io.github.jukomu.langchain4jcodeplatform.util.ThrowUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 对话历史 服务层实现
 *
 * @author JUKOMU
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatHistoryServiceImpl extends ServiceImpl<ChatHistoryMapper, ChatHistory> implements ChatHistoryService {

    private static final int DEFAULT_CHAT_PAGE_SIZE = 10;
    private static final int MAX_CHAT_PAGE_SIZE = 50;
    private static final int MAX_ADMIN_PAGE_SIZE = 100;

    private final AppMapper appMapper;

    private final UserService userService;

    @Override
    public long saveMessage(Long appId, Long userId, String message, ChatHistoryMessageTypeEnum messageType, Long parentId) {
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用 id 非法");
        ThrowUtils.throwIf(userId == null || userId <= 0, ErrorCode.PARAMS_ERROR, "用户 id 非法");
        ThrowUtils.throwIf(message == null, ErrorCode.PARAMS_ERROR, "消息内容不能为空");
        ThrowUtils.throwIf(messageType == null, ErrorCode.PARAMS_ERROR, "消息类型不能为空");
        ChatHistory chatHistory = ChatHistory.builder()
                .appId(appId)
                .userId(userId)
                .message(message)
                .messageType(messageType.getValue())
                .parentId(parentId)
                .build();
        boolean saved = this.save(chatHistory);
        ThrowUtils.throwIf(!saved, ErrorCode.OPERATION_ERROR, "保存对话历史失败");
        return chatHistory.getId();
    }

    @Override
    public Long getLatestMessageId(Long appId) {
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用 id 非法");
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select(ChatHistory::getId)
                .eq(ChatHistory::getAppId, appId)
                .orderBy(ChatHistory::getCreateTime, false);
        ChatHistory chatHistory = this.getOne(queryWrapper);
        return chatHistory == null ? null : chatHistory.getId();
    }

    @Override
    public AppChatHistoryPageVo getAppChatHistories(AppChatHistoryQueryDto appChatHistoryQueryDto, LoginUserVo loginUser) {
        ThrowUtils.throwIf(appChatHistoryQueryDto == null || loginUser == null, ErrorCode.PARAMS_ERROR);
        Long appId = appChatHistoryQueryDto.getAppId();
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用 id 非法");
        int pageSize = appChatHistoryQueryDto.getPageSize() > 0 ? appChatHistoryQueryDto.getPageSize() : DEFAULT_CHAT_PAGE_SIZE;
        ThrowUtils.throwIf(pageSize > MAX_CHAT_PAGE_SIZE, ErrorCode.PARAMS_ERROR, "单次最多加载 50 条消息");

        App app = getAppById(appId);
        // 检查权限
        checkAppViewPermission(app, loginUser);

        QueryWrapper queryWrapper = QueryWrapper.create()
                .eq(ChatHistory::getAppId, appId)
                .orderBy(ChatHistory::getCreateTime, false);
        // 游标查询最后一条消息前的消息
        LocalDateTime lastCreateTime = appChatHistoryQueryDto.getLastCreateTime();
        if (lastCreateTime != null && lastCreateTime.isBefore(LocalDateTime.now())) {
            queryWrapper.lt(ChatHistory::getCreateTime, lastCreateTime);
            // 不设置limit，因为需要判断是否还有更多记录，需要总记录数，数量限制在下方page查询中体现
            // queryWrapper.limit(pageSize);
        }

        Page<ChatHistory> chatHistoryPage = this.page(Page.of(1, pageSize), queryWrapper);
        List<ChatHistory> records = new ArrayList<>(chatHistoryPage.getRecords());
        // 反转列表使旧消息在前
        Collections.reverse(records);
        List<ChatHistoryVo> chatHistoryVoList = buildChatHistoryVoList(records);

        AppChatHistoryPageVo pageVo = new AppChatHistoryPageVo();
        pageVo.setRecords(chatHistoryVoList);
        pageVo.setHasMore(chatHistoryPage.getTotalRow() > pageSize);
        // 设置下次查询的游标
        pageVo.setNextLastCreateTime(chatHistoryVoList.isEmpty() ? null : chatHistoryVoList.getFirst().getCreateTime());
        return pageVo;
    }

    @Override
    public Page<ChatHistoryVo> getChatHistories(ChatHistoryQueryDto chatHistoryQueryDto) {
        ThrowUtils.throwIf(chatHistoryQueryDto == null, ErrorCode.PARAMS_ERROR);
        int pageNum = chatHistoryQueryDto.getPageNum();
        int pageSize = chatHistoryQueryDto.getPageSize();
        ThrowUtils.throwIf(pageNum <= 0 || pageSize <= 0, ErrorCode.PARAMS_ERROR, "分页参数错误");
        ThrowUtils.throwIf(pageSize > MAX_ADMIN_PAGE_SIZE, ErrorCode.PARAMS_ERROR, "每页最多查询 100 条");

        Page<ChatHistory> chatHistoryPage = this.page(Page.of(pageNum, pageSize), getAdminQueryWrapper(chatHistoryQueryDto));
        Page<ChatHistoryVo> pageVo = new Page<>(pageNum, pageSize, chatHistoryPage.getTotalRow());
        pageVo.setRecords(buildChatHistoryVoList(chatHistoryPage.getRecords()));
        return pageVo;
    }

    @Override
    public boolean deleteByAppId(Long appId) {
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用 id 非法");
        QueryWrapper queryWrapper = QueryWrapper.create().eq(ChatHistory::getAppId, appId);
        if (this.count(queryWrapper) == 0) {
            return true;
        }
        return this.remove(queryWrapper);
    }

    @Override
    public int loadChatHistoryToMemory(Long appId, MessageWindowChatMemory chatMemory, int maxCount) {
        try {
            // 从 1 开始排除用户最新的消息
            QueryWrapper queryWrapper = QueryWrapper.create()
                    .eq(ChatHistory::getAppId, appId)
                    .orderBy(ChatHistory::getCreateTime, false)
                    .limit(1, maxCount);
            List<ChatHistory> list = this.list(queryWrapper);
            if (list.isEmpty()) {
                return 0;
            }
            // 反转列表，使旧的消息在前
            list = list.reversed();
            // 按时间顺序添加到记忆中
            int loadedCount = 0;
            chatMemory.clear();
            for (ChatHistory chatHistory : list) {
                if (ChatHistoryMessageTypeEnum.USER.getValue().equals(chatHistory.getMessageType())) {
                    chatMemory.add(UserMessage.from(chatHistory.getMessage()));
                    loadedCount += 1;
                }
                if (ChatHistoryMessageTypeEnum.AI.getValue().equals(chatHistory.getMessageType())) {
                    chatMemory.add(AiMessage.from(chatHistory.getMessage()));
                    loadedCount += 1;
                }
            }
            log.info("成功为 appId: {} 加载了 {} 条历史对话", appId, loadedCount);
            return loadedCount;
        } catch (Exception e) {
            log.error("加载历史对话失败, appId: {}, error: {}", appId, e.getMessage(), e);
            return 0;
        }
    }

    private QueryWrapper getAdminQueryWrapper(ChatHistoryQueryDto chatHistoryQueryDto) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .eq(ChatHistory::getId, chatHistoryQueryDto.getId())
                .eq(ChatHistory::getAppId, chatHistoryQueryDto.getAppId())
                .eq(ChatHistory::getUserId, chatHistoryQueryDto.getUserId())
                .eq(ChatHistory::getMessageType, chatHistoryQueryDto.getMessageType())
                .eq(ChatHistory::getParentId, chatHistoryQueryDto.getParentId())
                .like(ChatHistory::getMessage, chatHistoryQueryDto.getMessage());
        if (StrUtil.isNotBlank(chatHistoryQueryDto.getSortField())) {
            queryWrapper.orderBy(chatHistoryQueryDto.getSortField(), "ascend".equals(chatHistoryQueryDto.getSortOrder()));
        } else {
            queryWrapper.orderBy(ChatHistory::getCreateTime, false)
                    .orderBy(ChatHistory::getId, false);
        }
        return queryWrapper;
    }

    private List<ChatHistoryVo> buildChatHistoryVoList(List<ChatHistory> chatHistories) {
        if (CollUtil.isEmpty(chatHistories)) {
            return Collections.emptyList();
        }
        Map<Long, AppVo> appVoMap = getAppVoMap(chatHistories);
        Map<Long, UserVo> userVoMap = getUserVoMap(chatHistories);
        return chatHistories.stream().map(chatHistory -> {
            ChatHistoryVo chatHistoryVo = new ChatHistoryVo();
            BeanUtils.copyProperties(chatHistory, chatHistoryVo);
            chatHistoryVo.setApp(appVoMap.get(chatHistory.getAppId()));
            chatHistoryVo.setUser(userVoMap.get(chatHistory.getUserId()));
            return chatHistoryVo;
        }).toList();
    }

    private Map<Long, AppVo> getAppVoMap(Collection<ChatHistory> chatHistories) {
        Set<Long> appIds = chatHistories.stream()
                .map(ChatHistory::getAppId)
                .collect(Collectors.toSet());
        if (appIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return appMapper.selectListByIds(appIds).stream()
                .collect(Collectors.toMap(App::getId, app -> {
                    AppVo appVo = new AppVo();
                    BeanUtils.copyProperties(app, appVo);
                    return appVo;
                }));
    }

    private Map<Long, UserVo> getUserVoMap(Collection<ChatHistory> chatHistories) {
        Set<Long> userIds = chatHistories.stream()
                .map(ChatHistory::getUserId)
                .collect(Collectors.toSet());
        if (userIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return userService.listByIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, user -> {
                    UserVo userVo = new UserVo();
                    BeanUtils.copyProperties(user, userVo);
                    return userVo;
                }));
    }

    private App getAppById(Long appId) {
        App app = appMapper.selectOneById(appId);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR, "应用不存在");
        return app;
    }

    private void checkAppViewPermission(App app, LoginUserVo loginUser) {
        ThrowUtils.throwIf(app == null || loginUser == null, ErrorCode.PARAMS_ERROR);
        boolean isAdmin = UserRoleEnum.ADMIN.getValue().equalsIgnoreCase(loginUser.getUserRole());
        if (!isAdmin && !loginUser.getId().equals(app.getUserId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "仅应用创建者和管理员可查看对话历史");
        }
    }
}
