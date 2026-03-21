package io.github.jukomu.langchain4jcodeplatform.service.impl;

import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import io.github.jukomu.langchain4jcodeplatform.common.DeleteRequest;
import io.github.jukomu.langchain4jcodeplatform.exception.BusinessException;
import io.github.jukomu.langchain4jcodeplatform.exception.ErrorCode;
import io.github.jukomu.langchain4jcodeplatform.mapper.UserMapper;
import io.github.jukomu.langchain4jcodeplatform.model.dto.user.*;
import io.github.jukomu.langchain4jcodeplatform.model.entity.User;
import io.github.jukomu.langchain4jcodeplatform.model.enums.UserRoleEnum;
import io.github.jukomu.langchain4jcodeplatform.model.vo.LoginUserVo;
import io.github.jukomu.langchain4jcodeplatform.model.vo.UserVo;
import io.github.jukomu.langchain4jcodeplatform.service.UserService;
import io.github.jukomu.langchain4jcodeplatform.util.ThrowUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

import static io.github.jukomu.langchain4jcodeplatform.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户 服务层实现。
 *
 * @author JUKOMU
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public long register(UserRegisterDto userRegisterDto) {
        String userAccount = userRegisterDto.getUserAccount();
        String userPassword = userRegisterDto.getUserPassword();
        String checkPassword = userRegisterDto.getCheckPassword();
        // 校验密码
        if (StrUtil.hasBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        ThrowUtils.throwIf(userAccount.length() < 4, ErrorCode.PARAMS_ERROR, "用户名过短");
        ThrowUtils.throwIf(userPassword.length() < 6 || checkPassword.length() < 6, ErrorCode.PARAMS_ERROR, "用户密码过短");
        ThrowUtils.throwIf(!userPassword.equals(checkPassword), ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        // 验重
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_account", userAccount);
        long count = this.mapper.selectCountByQuery(queryWrapper);
        ThrowUtils.throwIf(count > 0, ErrorCode.PARAMS_ERROR, "账号重复");
        // 加密
        String encryptPassword = getEncryptPassword(userPassword);
        // 保存
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setUserName(userAccount);
        user.setUserRole(UserRoleEnum.USER.getValue());
        boolean b = this.save(user);
        ThrowUtils.throwIf(!b, ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
        return user.getId();
    }

    @Override
    public LoginUserVo login(UserLoginDto userLoginDto, HttpServletRequest request) {
        String userAccount = userLoginDto.getUserAccount();
        String userPassword = userLoginDto.getUserPassword();
        if (StrUtil.hasBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户名或密码不能为空");
        }
        String encryptPassword = getEncryptPassword(userPassword);
        QueryWrapper queryWrapper = new QueryWrapper()
                .eq("user_account", userAccount)
                .eq("user_password", encryptPassword);
        User user = this.mapper.selectOneByQuery(queryWrapper);
        ThrowUtils.throwIf(user == null, ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        LoginUserVo loginUserVo = new LoginUserVo();
        BeanUtils.copyProperties(user, loginUserVo);
        request.getSession().setAttribute(USER_LOGIN_STATE, loginUserVo);
        return loginUserVo;
    }

    @Override
    public @NotNull LoginUserVo getLoginUser(HttpServletRequest request) {
        Object attribute = request.getSession().getAttribute(USER_LOGIN_STATE);
        LoginUserVo loginUserVo = (LoginUserVo) attribute;
        ThrowUtils.throwIf(loginUserVo == null || loginUserVo.getId() == null, ErrorCode.NOT_LOGIN_ERROR);
        return loginUserVo;
    }

    @Override
    public LoginUserVo getLoginUser() {
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        return getLoginUser(request);
    }

    @Override
    public boolean logout(HttpServletRequest request) {
        Object attribute = request.getSession().getAttribute(USER_LOGIN_STATE);
        LoginUserVo loginUserVo = (LoginUserVo) attribute;
        ThrowUtils.throwIf(loginUserVo == null || loginUserVo.getId() == null, ErrorCode.OPERATION_ERROR, "未登录");
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return true;
    }

    @Override
    public @NotNull UserVo getUser(long id) {
        User user = this.getById(id);
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(user, userVo);
        return userVo;
    }

    @Override
    public long addUser(UserAddDto userAddDto) {
        User user = new User();
        BeanUtils.copyProperties(userAddDto, user);
        final String password = "1234567890";
        String encryptPassword = getEncryptPassword(password);
        user.setUserPassword(encryptPassword);
        user.setUserName(userAddDto.getUserName());
        boolean b = this.save(user);
        ThrowUtils.throwIf(!b, ErrorCode.OPERATION_ERROR);
        return user.getId();
    }

    @Override
    public boolean updateUser(UserUpdateDto userUpdateDto) {
        User user = new User();
        BeanUtils.copyProperties(userUpdateDto, user);
        boolean b = this.updateById(user);
        ThrowUtils.throwIf(!b, ErrorCode.OPERATION_ERROR);
        return true;
    }

    @Override
    public boolean deleteUser(DeleteRequest deleteRequest) {
        boolean b = this.removeById(deleteRequest.getId());
        ThrowUtils.throwIf(!b, ErrorCode.OPERATION_ERROR);
        return true;
    }

    @Override
    public Page<UserVo> getUsers(UserQueryDto userQueryDto) {
        int pageNum = userQueryDto.getPageNum();
        int pageSize = userQueryDto.getPageSize();
        Page<User> userPage = this.page(Page.of(pageNum, pageSize), getQueryWrapper(userQueryDto));
        // 数据脱敏
        Page<UserVo> userVoPage = new Page<>(pageNum, pageSize, userPage.getTotalRow());
        List<UserVo> userVoList = userPage.getRecords().stream().map(item -> {
            UserVo userVo = new UserVo();
            BeanUtils.copyProperties(item, userVo);
            return userVo;
        }).toList();
        userVoPage.setRecords(userVoList);
        return userVoPage;
    }

    private String getEncryptPassword(String userPassword) {
        // 盐值，混淆密码
        final String SALT = "2salt2";
        return DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
    }

    private QueryWrapper getQueryWrapper(UserQueryDto userQueryDto) {
        if (userQueryDto == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = userQueryDto.getId();
        String userAccount = userQueryDto.getUserAccount();
        String userName = userQueryDto.getUserName();
        String userProfile = userQueryDto.getUserProfile();
        String userRole = userQueryDto.getUserRole();
        String sortField = userQueryDto.getSortField();
        String sortOrder = userQueryDto.getSortOrder();
        return QueryWrapper.create()
                .eq(User::getId, id)
                .eq(User::getUserRole, userRole)
                .like(User::getUserAccount, userAccount)
                .like(User::getUserName, userName)
                .like(User::getUserProfile, userProfile)
                .orderBy(sortField, "ascend".equals(sortOrder));
    }


}
