package io.github.jukomu.langchain4jcodeplatform.controller;

import com.mybatisflex.core.paginate.Page;
import io.github.jukomu.langchain4jcodeplatform.annotation.AuthCheck;
import io.github.jukomu.langchain4jcodeplatform.common.BaseResponse;
import io.github.jukomu.langchain4jcodeplatform.exception.ErrorCode;
import io.github.jukomu.langchain4jcodeplatform.model.dto.chatHistory.AppChatHistoryQueryDto;
import io.github.jukomu.langchain4jcodeplatform.model.dto.chatHistory.ChatHistoryQueryDto;
import io.github.jukomu.langchain4jcodeplatform.model.vo.AppChatHistoryPageVo;
import io.github.jukomu.langchain4jcodeplatform.model.vo.ChatHistoryVo;
import io.github.jukomu.langchain4jcodeplatform.model.vo.LoginUserVo;
import io.github.jukomu.langchain4jcodeplatform.service.ChatHistoryService;
import io.github.jukomu.langchain4jcodeplatform.service.UserService;
import io.github.jukomu.langchain4jcodeplatform.util.ResultUtils;
import io.github.jukomu.langchain4jcodeplatform.util.ThrowUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static io.github.jukomu.langchain4jcodeplatform.constant.UserConstant.ADMIN_ROLE;
import static io.github.jukomu.langchain4jcodeplatform.constant.UserConstant.DEFAULT_ROLE;

/**
 * 对话历史 控制层
 *
 * @author JUKOMU
 */
@RestController
@RequestMapping("/chatHistory")
@RequiredArgsConstructor
public class ChatHistoryController {

    private final ChatHistoryService chatHistoryService;

    private final UserService userService;

    /**
     * 查询某个应用的聊天记录
     */
    @PostMapping("/app/list")
    @AuthCheck(hasRole = DEFAULT_ROLE)
    public BaseResponse<AppChatHistoryPageVo> getAppChatHistories(@RequestBody AppChatHistoryQueryDto appChatHistoryQueryDto,
                                                                  HttpServletRequest request) {
        ThrowUtils.throwIf(appChatHistoryQueryDto == null || request == null, ErrorCode.PARAMS_ERROR);
        LoginUserVo loginUser = userService.getLoginUser(request);
        AppChatHistoryPageVo pageVo = chatHistoryService.getAppChatHistories(appChatHistoryQueryDto, loginUser);
        return ResultUtils.success(pageVo);
    }

    /**
     * 管理员查询所有应用的聊天记录
     */
    @PostMapping("/admin/list")
    @AuthCheck(hasRole = ADMIN_ROLE)
    public BaseResponse<Page<ChatHistoryVo>> getChatHistories(@RequestBody ChatHistoryQueryDto chatHistoryQueryDto) {
        ThrowUtils.throwIf(chatHistoryQueryDto == null, ErrorCode.PARAMS_ERROR);
        Page<ChatHistoryVo> pageVo = chatHistoryService.getChatHistories(chatHistoryQueryDto);
        return ResultUtils.success(pageVo);
    }
}
