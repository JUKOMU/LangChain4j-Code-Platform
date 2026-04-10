package io.github.jukomu.langchain4jcodeplatform.service.impl;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import io.github.jukomu.langchain4jcodeplatform.common.DeleteRequest;
import io.github.jukomu.langchain4jcodeplatform.constant.AppConstant;
import io.github.jukomu.langchain4jcodeplatform.core.AiCodeGeneratorFacade;
import io.github.jukomu.langchain4jcodeplatform.core.handler.StreamHandlerExecutor;
import io.github.jukomu.langchain4jcodeplatform.exception.BusinessException;
import io.github.jukomu.langchain4jcodeplatform.exception.ErrorCode;
import io.github.jukomu.langchain4jcodeplatform.mapper.AppMapper;
import io.github.jukomu.langchain4jcodeplatform.model.dto.app.*;
import io.github.jukomu.langchain4jcodeplatform.model.entity.App;
import io.github.jukomu.langchain4jcodeplatform.model.entity.User;
import io.github.jukomu.langchain4jcodeplatform.model.enums.ChatHistoryMessageTypeEnum;
import io.github.jukomu.langchain4jcodeplatform.model.enums.CodeGenTypeEnum;
import io.github.jukomu.langchain4jcodeplatform.model.vo.AppVo;
import io.github.jukomu.langchain4jcodeplatform.model.vo.LoginUserVo;
import io.github.jukomu.langchain4jcodeplatform.model.vo.UserVo;
import io.github.jukomu.langchain4jcodeplatform.service.AppService;
import io.github.jukomu.langchain4jcodeplatform.service.ChatHistoryService;
import io.github.jukomu.langchain4jcodeplatform.service.UserService;
import io.github.jukomu.langchain4jcodeplatform.util.ThrowUtils;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

import java.io.File;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static io.github.jukomu.langchain4jcodeplatform.constant.AppConstant.DEFAULT_APP_PRIORITY;
import static io.github.jukomu.langchain4jcodeplatform.constant.AppConstant.FEATURED_APP_PRIORITY;

/**
 * 应用 服务层实现
 *
 * @author JUKOMU
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AppServiceImpl extends ServiceImpl<AppMapper, App> implements AppService {

    private static final int USER_MAX_PAGE_SIZE = 20;
    private final UserService userService;
    private final AiCodeGeneratorFacade aiCodeGeneratorFacade;
    private final ChatHistoryService chatHistoryService;
    private final StreamHandlerExecutor streamHandlerExecutor;

    @Override
    public long addApp(AppAddDto appAddDto, Long userId) {
        ThrowUtils.throwIf(userId == null || userId <= 0, ErrorCode.PARAMS_ERROR);
        validateAppAddDto(appAddDto);
        if (appAddDto.getCodeGenType() == null || CodeGenTypeEnum.getEnumByValue(appAddDto.getCodeGenType()) == null) {
            // 设置默认为多文件生成
            appAddDto.setCodeGenType(CodeGenTypeEnum.MULTI_FILE.getValue());
        }
        App app = new App();
        BeanUtils.copyProperties(appAddDto, app);
        if (StrUtil.isBlank(app.getAppName())) {
            app.setAppName("未命名应用");
        }
        app.setUserId(userId);
        app.setPriority(DEFAULT_APP_PRIORITY);
        boolean saved = this.save(app);
        ThrowUtils.throwIf(!saved, ErrorCode.OPERATION_ERROR);
        return app.getId();
    }

    @Override
    public boolean updateMyApp(AppUpdateDto appUpdateDto, Long userId) {
        ThrowUtils.throwIf(userId == null || userId <= 0, ErrorCode.PARAMS_ERROR);
        validateAppUpdateDto(appUpdateDto);
        // 判断app是否存在
        App oldApp = this.getById(appUpdateDto.getId());
        ThrowUtils.throwIf(oldApp == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人可更新
        checkAppOwner(oldApp, userId);
        App app = new App();
        app.setId(appUpdateDto.getId());
        app.setAppName(appUpdateDto.getAppName());
        // 设置编辑时间区分updateTime
        app.setEditTime(LocalDateTime.now());
        boolean updated = this.updateById(app);
        ThrowUtils.throwIf(!updated, ErrorCode.OPERATION_ERROR);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteMyApp(DeleteRequest deleteRequest, Long userId) {
        ThrowUtils.throwIf(deleteRequest == null || deleteRequest.getId() == null, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(userId == null || userId <= 0, ErrorCode.PARAMS_ERROR);
        // 判断app是否存在
        App oldApp = this.getById(deleteRequest.getId());
        ThrowUtils.throwIf(oldApp == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人可删除
        checkAppOwner(oldApp, userId);
        // 先删除对话历史
        try {
            boolean historyDeleted = chatHistoryService.deleteByAppId(deleteRequest.getId());
            if (!historyDeleted) {
                log.error("应用id: {}, 删除应用对话历史失败", deleteRequest.getId());
            }
        } catch (Exception e) {
            log.error("应用id: {}, 删除应用对话历史失败: {}", deleteRequest.getId(), e.getMessage());
        }
        boolean removed = this.removeById(deleteRequest.getId());
        ThrowUtils.throwIf(!removed, ErrorCode.OPERATION_ERROR);
        return true;
    }

    @Override
    public @NotNull AppVo getApp(long id, Long userId) {
        ThrowUtils.throwIf(id <= 0 || userId == null || userId <= 0, ErrorCode.PARAMS_ERROR);
        App app = this.getById(id);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR);
        // 用户只能查看自己的并且优先级正常的应用
        if (!userId.equals(app.getUserId()) && (app.getPriority() == null || app.getPriority() < DEFAULT_APP_PRIORITY)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限查看该应用");
        }
        // 组装用户信息
        AppVo appVo = getAppVo(app);
        UserVo userVo = userService.getUser(app.getUserId());
        appVo.setUser(userVo);
        return appVo;
    }

    @Override
    public Page<AppVo> getMyApps(AppQueryDto appQueryDto, Long userId) {
        ThrowUtils.throwIf(appQueryDto == null, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(userId == null || userId <= 0, ErrorCode.PARAMS_ERROR);
        int pageNum = appQueryDto.getPageNum();
        int pageSize = appQueryDto.getPageSize();
        validateUserPage(pageNum, pageSize);
        // 用户只能查询自己的应用
        appQueryDto.setUserId(userId);
        Page<App> appPage = this.page(Page.of(pageNum, pageSize), getQueryWrapper(appQueryDto));
        return buildAppVoPage(appPage, pageNum, pageSize);
    }

    @Override
    public Page<AppVo> getFeaturedApps(AppQueryDto appQueryDto) {
        ThrowUtils.throwIf(appQueryDto == null, ErrorCode.PARAMS_ERROR);
        int pageNum = appQueryDto.getPageNum();
        int pageSize = appQueryDto.getPageSize();
        validateUserPage(pageNum, pageSize);
        QueryWrapper queryWrapper = QueryWrapper.create()
                .like(App::getAppName, appQueryDto.getAppName())
                .gt(App::getPriority, FEATURED_APP_PRIORITY)
                .orderBy(App::getPriority, false)
                .orderBy(App::getUpdateTime, false);
        Page<App> appPage = this.page(Page.of(pageNum, pageSize), queryWrapper);
        return buildAppVoPage(appPage, pageNum, pageSize);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteApp(DeleteRequest deleteRequest) {
        ThrowUtils.throwIf(deleteRequest == null || deleteRequest.getId() == null, ErrorCode.PARAMS_ERROR);
        App oldApp = this.getById(deleteRequest.getId());
        ThrowUtils.throwIf(oldApp == null, ErrorCode.NOT_FOUND_ERROR);
        boolean removed = this.removeById(deleteRequest.getId());
        ThrowUtils.throwIf(!removed, ErrorCode.OPERATION_ERROR);
        boolean historyDeleted = chatHistoryService.deleteByAppId(deleteRequest.getId());
        ThrowUtils.throwIf(!historyDeleted, ErrorCode.OPERATION_ERROR, "删除应用对话历史失败");
        return true;
    }

    @Override
    public boolean updateApp(AppAdminUpdateDto appAdminUpdateDto) {
        ThrowUtils.throwIf(appAdminUpdateDto == null || appAdminUpdateDto.getId() == null, ErrorCode.PARAMS_ERROR);
        validateAppAdminUpdateDto(appAdminUpdateDto);
        App oldApp = this.getById(appAdminUpdateDto.getId());
        ThrowUtils.throwIf(oldApp == null, ErrorCode.NOT_FOUND_ERROR);
        App app = new App();
        BeanUtils.copyProperties(appAdminUpdateDto, app);
        app.setEditTime(LocalDateTime.now());
        boolean updated = this.updateById(app);
        ThrowUtils.throwIf(!updated, ErrorCode.OPERATION_ERROR);
        return true;
    }

    @Override
    public AppVo getApp(long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        App app = this.getById(id);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR);
        // 组装用户信息
        AppVo appVo = getAppVo(app);
        UserVo userVo = userService.getUser(app.getUserId());
        appVo.setUser(userVo);
        return appVo;
    }

    @Override
    public Page<AppVo> getApps(AppAdminQueryDto appAdminQueryDto) {
        ThrowUtils.throwIf(appAdminQueryDto == null, ErrorCode.PARAMS_ERROR);
        int pageNum = appAdminQueryDto.getPageNum();
        int pageSize = appAdminQueryDto.getPageSize();
        ThrowUtils.throwIf(pageNum <= 0 || pageSize <= 0, ErrorCode.PARAMS_ERROR, "分页参数错误");
        Page<App> appPage = this.page(Page.of(pageNum, pageSize), getAdminQueryWrapper(appAdminQueryDto));
        return buildAppVoPage(appPage, pageNum, pageSize);
    }

    @Override
    public Flux<String> chatToGenCode(Long appId, String userMessage) {
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用 ID 不能为空");
        ThrowUtils.throwIf(StrUtil.isBlank(userMessage), ErrorCode.PARAMS_ERROR, "用户消息不能为空");
        LoginUserVo loginUser = userService.getLoginUser();
        // 查询应用信息
        App app = this.getById(appId);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR, "应用不存在");
        // 验证用户是否有权限访问该应用，仅本人可以生成代码
        if (!app.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限访问该应用");
        }
        // 获取应用的代码生成类型
        String codeGenTypeStr = app.getCodeGenType();
        CodeGenTypeEnum codeGenTypeEnum = CodeGenTypeEnum.getEnumByValue(codeGenTypeStr);
        ThrowUtils.throwIf(codeGenTypeEnum == null, ErrorCode.SYSTEM_ERROR, "不支持的代码生成类型");
        // 添加用户消息到对话历史
        Long parentId = chatHistoryService.getLatestMessageId(appId);
        long userMessageId = chatHistoryService.saveMessage(appId, loginUser.getId(), userMessage, ChatHistoryMessageTypeEnum.USER, parentId);
        // 收集ai响应内容并在完成后记录到对话历史
        User user = new User();
        BeanUtils.copyProperties(loginUser, user);
        return streamHandlerExecutor.doExecute(aiCodeGeneratorFacade.streamGenerateAndSaveCode(userMessage, codeGenTypeEnum, appId), chatHistoryService, appId, user, codeGenTypeEnum,userMessageId);
    }

    @Override
    public String deployApp(Long appId) {
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用 ID 不能为空");
        LoginUserVo loginUser = userService.getLoginUser();
        // 查询应用信息
        App app = this.getById(appId);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR, "应用不存在");
        // 校验权限
        if (!app.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限部署该应用");
        }
        // 校验 deployKey
        String deployKey = app.getDeployKey();
        if (StrUtil.isBlank(deployKey)) {
            // 生成6位key(大小写字母+数字)
            deployKey = RandomUtil.randomString(6);
        }

        String sourceDirName = app.getCodeGenType() + "_" + appId;
        String sourceDirPath = AppConstant.CODE_OUTPUT_ROOT_DIR + File.separator + sourceDirName;
        // 验证源目录是否存在
        File sourceDir = new File(sourceDirPath);
        if (!sourceDir.exists() || !sourceDir.isDirectory()) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "应用代码不存在，请先生成代码");
        }
        // 复制文件到部署目录
        String deployDirPath = AppConstant.CODE_DEPLOY_ROOT_DIR + File.separator + deployKey;
        try {
            FileUtil.copyContent(sourceDir, new File(deployDirPath), true);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "部署失败: " + e.getMessage());
        }
        // 更新应用 deployKey 和部署时间
        app.setDeployKey(deployKey);
        app.setDeployedTime(LocalDateTime.now());
        boolean updated = this.updateById(app);
        ThrowUtils.throwIf(!updated, ErrorCode.OPERATION_ERROR, "更新应用部署信息失败");
        return String.format("%s/%s/", AppConstant.CODE_DEPLOY_HOST, deployKey);
    }

    private void validateAppAddDto(AppAddDto appAddDto) {
        ThrowUtils.throwIf(appAddDto == null, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(StrUtil.isBlank(appAddDto.getInitPrompt()), ErrorCode.PARAMS_ERROR, "initPrompt 不能为空");
        ThrowUtils.throwIf(appAddDto.getAppName() != null && appAddDto.getAppName().length() > 256, ErrorCode.PARAMS_ERROR, "应用名称过长");
        ThrowUtils.throwIf(appAddDto.getCover() != null && appAddDto.getCover().length() > 512, ErrorCode.PARAMS_ERROR, "应用封面过长");
        ThrowUtils.throwIf(appAddDto.getCodeGenType() != null && appAddDto.getCodeGenType().length() > 64, ErrorCode.PARAMS_ERROR, "代码生成类型过长");
    }

    private void validateAppUpdateDto(AppUpdateDto appUpdateDto) {
        ThrowUtils.throwIf(appUpdateDto == null, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(StrUtil.isBlank(appUpdateDto.getAppName()), ErrorCode.PARAMS_ERROR, "应用名称不能为空");
        ThrowUtils.throwIf(appUpdateDto.getAppName().length() > 256, ErrorCode.PARAMS_ERROR, "应用名称过长");
    }

    private void validateAppAdminUpdateDto(AppAdminUpdateDto appAdminUpdateDto) {
        ThrowUtils.throwIf(appAdminUpdateDto.getAppName() != null && appAdminUpdateDto.getAppName().length() > 256, ErrorCode.PARAMS_ERROR, "应用名称过长");
        ThrowUtils.throwIf(appAdminUpdateDto.getCover() != null && appAdminUpdateDto.getCover().length() > 512, ErrorCode.PARAMS_ERROR, "应用封面过长");
    }

    private void validateUserPage(int pageNum, int pageSize) {
        ThrowUtils.throwIf(pageNum <= 0 || pageSize <= 0, ErrorCode.PARAMS_ERROR, "分页参数错误");
        ThrowUtils.throwIf(pageSize > USER_MAX_PAGE_SIZE, ErrorCode.PARAMS_ERROR, "每页最多查询 20 条");
    }

    private void checkAppOwner(App app, Long userId) {
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR);
        ThrowUtils.throwIf(!userId.equals(app.getUserId()), ErrorCode.NO_AUTH_ERROR, "只能操作自己的应用");
    }

    private QueryWrapper getQueryWrapper(AppQueryDto appQueryDto) {
        if (appQueryDto == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        String sortField = appQueryDto.getSortField();
        String sortOrder = appQueryDto.getSortOrder();
        QueryWrapper queryWrapper = QueryWrapper.create()
                .eq(App::getId, appQueryDto.getId())
                .like(App::getAppName, appQueryDto.getAppName())
                .like(App::getCover, appQueryDto.getCover())
                .like(App::getInitPrompt, appQueryDto.getInitPrompt())
                .eq(App::getCodeGenType, appQueryDto.getCodeGenType())
                .eq(App::getDeployKey, appQueryDto.getDeployKey())
                .eq(App::getPriority, appQueryDto.getPriority())
                .eq(App::getUserId, appQueryDto.getUserId());
        if (StrUtil.isNotBlank(sortField)) {
            queryWrapper.orderBy(sortField, "ascend".equals(sortOrder));
        } else {
            queryWrapper.orderBy(App::getUpdateTime, false);
        }
        return queryWrapper;
    }

    private QueryWrapper getAdminQueryWrapper(AppAdminQueryDto appAdminQueryDto) {
        if (appAdminQueryDto == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        String sortField = appAdminQueryDto.getSortField();
        String sortOrder = appAdminQueryDto.getSortOrder();
        QueryWrapper queryWrapper = QueryWrapper.create()
                .eq(App::getId, appAdminQueryDto.getId())
                .like(App::getAppName, appAdminQueryDto.getAppName())
                .like(App::getCover, appAdminQueryDto.getCover())
                .like(App::getInitPrompt, appAdminQueryDto.getInitPrompt())
                .eq(App::getCodeGenType, appAdminQueryDto.getCodeGenType())
                .eq(App::getDeployKey, appAdminQueryDto.getDeployKey())
                .eq(App::getPriority, appAdminQueryDto.getPriority())
                .eq(App::getUserId, appAdminQueryDto.getUserId());
        if (StrUtil.isNotBlank(sortField)) {
            queryWrapper.orderBy(sortField, "ascend".equals(sortOrder));
        } else {
            queryWrapper.orderBy(App::getUpdateTime, false);
        }
        return queryWrapper;
    }

    private Page<AppVo> buildAppVoPage(Page<App> appPage, int pageNum, int pageSize) {
        Page<AppVo> appVoPage = new Page<>(pageNum, pageSize, appPage.getTotalRow());
        Map<Long, UserVo> userMap = getUserMap(appPage.getRecords());
        // 组装app列表和用户信息
        List<AppVo> appVoList = appPage.getRecords().stream()
                .map(app -> {
                    AppVo appVo = getAppVo(app);
                    UserVo userVo = userMap.get(app.getUserId());
                    appVo.setUser(userVo);
                    return appVo;
                }).toList();
        appVoPage.setRecords(appVoList);
        return appVoPage;
    }

    private AppVo getAppVo(App app) {
        AppVo appVo = new AppVo();
        BeanUtils.copyProperties(app, appVo);
        return appVo;
    }

    /**
     * 批量获取用户列表
     *
     * @return
     */
    private Map<Long, UserVo> getUserMap(Collection<App> apps) {
        Set<Long> userIds = apps.stream()
                .map(App::getUserId)
                .collect(Collectors.toSet());
        if (userIds.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<Long, UserVo> userMap = userService.listByIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, item -> {
                    UserVo userVo = new UserVo();
                    BeanUtils.copyProperties(item, userVo);
                    return userVo;
                }));
        return userMap;
    }
}
