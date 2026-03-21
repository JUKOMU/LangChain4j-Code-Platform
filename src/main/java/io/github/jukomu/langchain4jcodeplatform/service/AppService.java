package io.github.jukomu.langchain4jcodeplatform.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import io.github.jukomu.langchain4jcodeplatform.common.DeleteRequest;
import io.github.jukomu.langchain4jcodeplatform.model.dto.app.*;
import io.github.jukomu.langchain4jcodeplatform.model.entity.App;
import io.github.jukomu.langchain4jcodeplatform.model.vo.AppVo;
import jakarta.validation.constraints.NotNull;
import reactor.core.publisher.Flux;

/**
 * APP 服务层。
 *
 * @author JUKOMU
 */
public interface AppService extends IService<App> {

    /**
     * 用户创建应用
     *
     * @param appAddDto
     * @param userId
     * @return
     */
    long addApp(AppAddDto appAddDto, Long userId);

    /**
     * 用户更新应用
     *
     * @param appUpdateDto
     * @param userId
     * @return
     */
    boolean updateMyApp(AppUpdateDto appUpdateDto, Long userId);

    /**
     * 用户删除应用
     *
     * @param deleteRequest
     * @param userId
     * @return
     */
    boolean deleteMyApp(DeleteRequest deleteRequest, Long userId);

    /**
     * 用户获取应用详情
     *
     * @param id
     * @param userId
     * @return
     */
    @NotNull
    AppVo getApp(long id, Long userId);

    /**
     * 用户获取应用列表
     *
     * @param appQueryDto
     * @param userId
     * @return
     */
    Page<AppVo> getMyApps(AppQueryDto appQueryDto, Long userId);

    /**
     * 获取精选应用
     *
     * @param appQueryDto
     * @return
     */
    Page<AppVo> getFeaturedApps(AppQueryDto appQueryDto);

    /**
     * 管理员删除应用
     *
     * @param deleteRequest
     * @return
     */
    boolean deleteApp(DeleteRequest deleteRequest);

    /**
     * 管理员更新应用
     *
     * @param appAdminUpdateDto
     * @return
     */
    boolean updateApp(AppAdminUpdateDto appAdminUpdateDto);

    /**
     * 管理员获取应用详情
     *
     * @param id
     * @return
     */
    AppVo getApp(long id);

    /**
     * 管理员获取应用列表
     *
     * @param appAdminQueryDto
     * @return
     */
    Page<AppVo> getApps(AppAdminQueryDto appAdminQueryDto);

    /**
     * 生成应用代码
     *
     * @param appId
     * @param userMessage
     * @return
     */
    Flux<String> chatToGenCode(Long appId, String userMessage);

    /**
     * 部署应用
     *
     * @param appId
     * @return
     */
    String deployApp(Long appId);
}
