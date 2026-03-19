package io.github.jukomu.langchain4jcodeplatform.controller;

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
import org.springframework.web.bind.annotation.*;

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
}
