package io.github.jukomu.langchain4jcodeplatform.aop;

import io.github.jukomu.langchain4jcodeplatform.annotation.AuthCheck;
import io.github.jukomu.langchain4jcodeplatform.exception.BusinessException;
import io.github.jukomu.langchain4jcodeplatform.exception.ErrorCode;
import io.github.jukomu.langchain4jcodeplatform.model.enums.UserRoleEnum;
import io.github.jukomu.langchain4jcodeplatform.model.vo.LoginUserVo;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static io.github.jukomu.langchain4jcodeplatform.constant.UserConstant.USER_LOGIN_STATE;

/**
 * @author JUKOMU
 * @Description: 权限校验AOP
 * @Project: LangChain4j-Code-Platform
 * @Date: 2026/3/16
 */
@Aspect
@Component
public class AuthInterceptor {

    /**
     * 执行拦截
     *
     * @param joinPoint 切入点
     * @param authCheck 权限校验注解
     */
    @Around("@annotation(authCheck)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        String hasRole = authCheck.hasRole();
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        // 当前登录用户
        Object attribute = request.getSession().getAttribute(USER_LOGIN_STATE);
        LoginUserVo loginUserVo = (LoginUserVo) attribute;
        UserRoleEnum hasRoleEnum = UserRoleEnum.getByValue(hasRole);
        // 不需要权限，放行
        if (hasRoleEnum == null) {
            return joinPoint.proceed();
        }
        // 获取当前用户具有的权限
        if (loginUserVo == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        UserRoleEnum currentRoleEnum = UserRoleEnum.getByValue(loginUserVo.getUserRole());
        // 没有权限，拒绝
        if (currentRoleEnum == null) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 要求必须有管理员权限，但用户没有管理员权限，拒绝
        if (UserRoleEnum.ADMIN.equals(hasRoleEnum) && !UserRoleEnum.ADMIN.equals(currentRoleEnum)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 通过权限校验，放行
        return joinPoint.proceed();
    }
}

