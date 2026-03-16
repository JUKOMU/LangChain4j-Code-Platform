package io.github.jukomu.langchain4jcodeplatform.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import io.github.jukomu.langchain4jcodeplatform.common.DeleteRequest;
import io.github.jukomu.langchain4jcodeplatform.model.dto.user.*;
import io.github.jukomu.langchain4jcodeplatform.model.entity.User;
import io.github.jukomu.langchain4jcodeplatform.model.vo.LoginUserVo;
import io.github.jukomu.langchain4jcodeplatform.model.vo.UserVo;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 用户 服务层。
 *
 * @author JUKOMU
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userRegisterDto
     * @return
     */
    long register(UserRegisterDto userRegisterDto);

    /**
     * 用户登录
     *
     * @param userLoginDto
     * @param request
     * @return
     */
    LoginUserVo login(UserLoginDto userLoginDto, HttpServletRequest request);

    /**
     * 获取当前登录用户信息
     *
     * @param request
     * @return
     */
    LoginUserVo getLoginUser(HttpServletRequest request);

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    boolean logout(HttpServletRequest request);

    /**
     * 获取用户信息
     *
     * @param id
     * @return
     */
    UserVo getUser(long id);

    /**
     * 添加用户
     *
     * @param userAddDto
     * @return
     */
    long addUser(UserAddDto userAddDto);

    /**
     * 更新用户
     *
     * @param userUpdateDto
     * @return
     */
    boolean updateUser(UserUpdateDto userUpdateDto);

    /**
     * 删除用户
     *
     * @param deleteRequest
     * @return
     */
    boolean deleteUser(DeleteRequest deleteRequest);

    /**
     * 分页获取用户信息
     *
     * @return
     */
    Page<UserVo> getUsers(UserQueryDto userQueryDto);
}
