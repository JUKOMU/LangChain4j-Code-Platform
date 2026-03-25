package io.github.jukomu.langchain4jcodeplatform.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.mybatisflex.core.paginate.Page;
import io.github.jukomu.langchain4jcodeplatform.annotation.AuthCheck;
import io.github.jukomu.langchain4jcodeplatform.common.BaseResponse;
import io.github.jukomu.langchain4jcodeplatform.common.DeleteRequest;
import io.github.jukomu.langchain4jcodeplatform.exception.ErrorCode;
import io.github.jukomu.langchain4jcodeplatform.model.dto.app.*;
import io.github.jukomu.langchain4jcodeplatform.model.vo.AppVo;
import io.github.jukomu.langchain4jcodeplatform.model.vo.LoginUserVo;
import io.github.jukomu.langchain4jcodeplatform.service.AppService;
import io.github.jukomu.langchain4jcodeplatform.service.UserService;
import io.github.jukomu.langchain4jcodeplatform.util.ResultUtils;
import io.github.jukomu.langchain4jcodeplatform.util.ThrowUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

import static io.github.jukomu.langchain4jcodeplatform.constant.UserConstant.ADMIN_ROLE;
import static io.github.jukomu.langchain4jcodeplatform.constant.UserConstant.DEFAULT_ROLE;

/**
 * APP 控制层。
 *
 * @author JUKOMU
 */
@RestController
@RequestMapping("/app")
@RequiredArgsConstructor
public class AppController {

    private final AppService appService;
    private final UserService userService;

    /**
     * 用户创建应用
     */
    @PostMapping
    @AuthCheck(hasRole = DEFAULT_ROLE)
    public BaseResponse<Long> addApp(@RequestBody AppAddDto appAddDto, HttpServletRequest request) {
        ThrowUtils.throwIf(appAddDto == null || request == null, ErrorCode.PARAMS_ERROR);
        LoginUserVo loginUser = userService.getLoginUser(request);
        long appId = appService.addApp(appAddDto, loginUser.getId());
        return ResultUtils.success(appId);
    }

    /**
     * 用户更新自己的应用
     */
    @PutMapping("/my")
    @AuthCheck(hasRole = DEFAULT_ROLE)
    public BaseResponse<Boolean> updateMyApp(@RequestBody AppUpdateDto appUpdateDto, HttpServletRequest request) {
        ThrowUtils.throwIf(appUpdateDto == null || appUpdateDto.getId() == null || request == null, ErrorCode.PARAMS_ERROR);
        LoginUserVo loginUser = userService.getLoginUser(request);
        boolean updated = appService.updateMyApp(appUpdateDto, loginUser.getId());
        return ResultUtils.success(updated);
    }

    /**
     * 用户删除自己的应用
     */
    @DeleteMapping("/my")
    @AuthCheck(hasRole = DEFAULT_ROLE)
    public BaseResponse<Boolean> deleteMyApp(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(deleteRequest == null || deleteRequest.getId() == null || request == null, ErrorCode.PARAMS_ERROR);
        LoginUserVo loginUser = userService.getLoginUser(request);
        boolean deleted = appService.deleteMyApp(deleteRequest, loginUser.getId());
        return ResultUtils.success(deleted);
    }

    /**
     * 查看应用详情
     */
    @GetMapping("/{id}")
    @AuthCheck(hasRole = DEFAULT_ROLE)
    public BaseResponse<AppVo> getApp(@PathVariable long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0 || request == null, ErrorCode.PARAMS_ERROR);
        LoginUserVo loginUser = userService.getLoginUser(request);
        AppVo appVo = appService.getApp(id, loginUser.getId());
        return ResultUtils.success(appVo);
    }

    /**
     * 分页查询自己的应用列表
     */
    @PostMapping("/my/list")
    @AuthCheck(hasRole = DEFAULT_ROLE)
    public BaseResponse<Page<AppVo>> getMyApps(@RequestBody AppQueryDto appQueryDto, HttpServletRequest request) {
        ThrowUtils.throwIf(appQueryDto == null || request == null, ErrorCode.PARAMS_ERROR);
        LoginUserVo loginUser = userService.getLoginUser(request);
        Page<AppVo> appVoPage = appService.getMyApps(appQueryDto, loginUser.getId());
        return ResultUtils.success(appVoPage);
    }

    /**
     * 分页查询精选应用列表
     */
    @PostMapping("/good/list")
    @AuthCheck(hasRole = DEFAULT_ROLE)
    public BaseResponse<Page<AppVo>> getFeaturedApps(@RequestBody AppQueryDto appQueryDto) {
        ThrowUtils.throwIf(appQueryDto == null, ErrorCode.PARAMS_ERROR);
        Page<AppVo> appVoPage = appService.getFeaturedApps(appQueryDto);
        return ResultUtils.success(appVoPage);
    }

    /**
     * 管理员删除应用
     */
    @DeleteMapping("/admin")
    @AuthCheck(hasRole = ADMIN_ROLE)
    public BaseResponse<Boolean> deleteApp(@RequestBody DeleteRequest deleteRequest) {
        ThrowUtils.throwIf(deleteRequest == null || deleteRequest.getId() == null, ErrorCode.PARAMS_ERROR);
        boolean deleted = appService.deleteApp(deleteRequest);
        return ResultUtils.success(deleted);
    }

    /**
     * 管理员更新应用
     */
    @PutMapping("/admin")
    @AuthCheck(hasRole = ADMIN_ROLE)
    public BaseResponse<Boolean> updateApp(@RequestBody AppAdminUpdateDto appAdminUpdateDto) {
        ThrowUtils.throwIf(appAdminUpdateDto == null || appAdminUpdateDto.getId() == null, ErrorCode.PARAMS_ERROR);
        boolean updated = appService.updateApp(appAdminUpdateDto);
        return ResultUtils.success(updated);
    }

    /**
     * 管理员分页查询应用列表
     */
    @PostMapping("/admin/list")
    @AuthCheck(hasRole = ADMIN_ROLE)
    public BaseResponse<Page<AppVo>> getApps(@RequestBody AppAdminQueryDto appAdminQueryDto) {
        ThrowUtils.throwIf(appAdminQueryDto == null, ErrorCode.PARAMS_ERROR);
        Page<AppVo> appVoPage = appService.getApps(appAdminQueryDto);
        return ResultUtils.success(appVoPage);
    }

    /**
     * 管理员查看应用详情
     */
    @GetMapping("/admin/{id}")
    @AuthCheck(hasRole = ADMIN_ROLE)
    public BaseResponse<AppVo> getAppByAdmin(@PathVariable long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        AppVo appVo = appService.getApp(id);
        return ResultUtils.success(appVo);
    }

    /**
     * 应用聊天生成代码
     *
     * @param appId       应用 id
     * @param userMessage 用户消息
     * @return 生成结果流
     */
    @GetMapping(value = "/chat/{appId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @AuthCheck(hasRole = DEFAULT_ROLE)
    public Flux<ServerSentEvent<String>> chatTogGenCode(@PathVariable Long appId, @RequestParam String userMessage) {
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用ID无效");
        ThrowUtils.throwIf(StrUtil.isBlank(userMessage), ErrorCode.PARAMS_ERROR, "用户消息不能为空");
        Flux<String> content = appService.chatToGenCode(appId, userMessage);
        // 转换为 ServerSentEvent
        return content.map(chunk -> {
                    // 包装为 JSON
                    String jsonData = JSONUtil.toJsonStr(Map.of("v", chunk));
                    return ServerSentEvent.<String>builder()
                            .data(jsonData)
                            .build();
                })
                .concatWith(Mono.just(
                        // 结束事件
                        ServerSentEvent.<String>builder()
                                .event("done")
                                .data("finish")
                                .build()
                ));
    }

    /**
     * 应用部署
     *
     * @param appDeployDto 部署请求
     * @return 部署 URL
     */
    @PostMapping("/deploy")
    public BaseResponse<String> deployApp(@RequestBody AppDeployDto appDeployDto) {
        ThrowUtils.throwIf(appDeployDto == null, ErrorCode.PARAMS_ERROR);
        Long appId = appDeployDto.getAppId();
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用 ID 不能为空");
        // 调用服务部署应用
        String deployUrl = appService.deployApp(appId);
        return ResultUtils.success(deployUrl);
    }

}
