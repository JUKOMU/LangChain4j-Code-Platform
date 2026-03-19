package io.github.jukomu.langchain4jcodeplatform.controller;

import com.mybatisflex.core.paginate.Page;
import io.github.jukomu.langchain4jcodeplatform.annotation.AuthCheck;
import io.github.jukomu.langchain4jcodeplatform.common.BaseResponse;
import io.github.jukomu.langchain4jcodeplatform.common.DeleteRequest;
import io.github.jukomu.langchain4jcodeplatform.exception.ErrorCode;
import io.github.jukomu.langchain4jcodeplatform.model.dto.user.*;
import io.github.jukomu.langchain4jcodeplatform.model.vo.LoginUserVo;
import io.github.jukomu.langchain4jcodeplatform.model.vo.UserVo;
import io.github.jukomu.langchain4jcodeplatform.service.UserService;
import io.github.jukomu.langchain4jcodeplatform.util.ResultUtils;
import io.github.jukomu.langchain4jcodeplatform.util.ThrowUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static io.github.jukomu.langchain4jcodeplatform.constant.UserConstant.ADMIN_ROLE;
import static io.github.jukomu.langchain4jcodeplatform.constant.UserConstant.DEFAULT_ROLE;

/**
 * 用户 控制层。
 *
 * @author JUKOMU
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public BaseResponse<Long> register(@RequestBody UserRegisterDto userRegisterDto) {
        ThrowUtils.throwIf(userRegisterDto == null, ErrorCode.PARAMS_ERROR);
        long userRegisterId = userService.register(userRegisterDto);
        return ResultUtils.success(userRegisterId);
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public BaseResponse<LoginUserVo> login(@RequestBody UserLoginDto userLoginDto, HttpServletRequest request) {
        ThrowUtils.throwIf(userLoginDto == null || request == null, ErrorCode.PARAMS_ERROR);
        LoginUserVo login = userService.login(userLoginDto, request);
        return ResultUtils.success(login);
    }

    /**
     * 获取当前登录用户
     */
    @GetMapping("/get/login")
    @AuthCheck(hasRole = DEFAULT_ROLE)
    public BaseResponse<LoginUserVo> getLoginUser(HttpServletRequest request) {
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR);
        LoginUserVo loginUser = userService.getLoginUser(request);
        return ResultUtils.success(loginUser);
    }

    /**
     * 用户注销
     */
    @GetMapping("/logout")
    @AuthCheck(hasRole = DEFAULT_ROLE)
    public BaseResponse<Boolean> logout(HttpServletRequest request) {
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR);
        boolean logout = userService.logout(request);
        return ResultUtils.success(logout);
    }

    /**
     * 获取用户 （管理员）
     */
    @GetMapping("/{id}")
    @AuthCheck(hasRole = ADMIN_ROLE)
    public BaseResponse<UserVo> getUser(@PathVariable long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        UserVo userVo = userService.getUser(id);
        return ResultUtils.success(userVo);
    }

    /**
     * 添加用户 （管理员）
     *
     * @param userAddDto
     * @return
     */
    @PostMapping()
    @AuthCheck(hasRole = ADMIN_ROLE)
    public BaseResponse<Long> addUser(@RequestBody UserAddDto userAddDto) {
        ThrowUtils.throwIf(userAddDto == null, ErrorCode.PARAMS_ERROR);
        long userId = userService.addUser(userAddDto);
        return ResultUtils.success(userId);
    }

    /**
     * 更新用户 （管理员）
     *
     * @param userUpdateDto
     * @return
     */
    @PutMapping
    @AuthCheck(hasRole = ADMIN_ROLE)
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateDto userUpdateDto) {
        ThrowUtils.throwIf(userUpdateDto == null || userUpdateDto.getId() == null, ErrorCode.PARAMS_ERROR);
        boolean update = userService.updateUser(userUpdateDto);
        return ResultUtils.success(update);
    }

    /**
     * 删除用户 （管理员）
     *
     * @param deleteRequest
     * @return
     */
    @DeleteMapping
    @AuthCheck(hasRole = ADMIN_ROLE)
    public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest) {
        ThrowUtils.throwIf(deleteRequest == null || deleteRequest.getId() == null, ErrorCode.PARAMS_ERROR);
        boolean deleted = userService.deleteUser(deleteRequest);
        return ResultUtils.success(deleted);
    }

    /**
     * 获取用户列表 （管理员）
     *
     * @param userQueryDto
     * @return
     */
    @PostMapping("/list")
    @AuthCheck(hasRole = ADMIN_ROLE)
    public BaseResponse<Page<UserVo>> getUsers(@RequestBody UserQueryDto userQueryDto) {
        ThrowUtils.throwIf(userQueryDto == null, ErrorCode.PARAMS_ERROR);
        Page<UserVo> users = userService.getUsers(userQueryDto);
        return ResultUtils.success(users);
    }

}
