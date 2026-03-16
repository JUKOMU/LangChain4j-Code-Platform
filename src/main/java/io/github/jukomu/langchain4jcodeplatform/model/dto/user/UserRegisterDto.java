package io.github.jukomu.langchain4jcodeplatform.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * @author JUKOMU
 * @Description: 用户注册数据传输对象
 * @Project: LangChain4j-Code-Platform
 * @Date: 2026/3/16
 */
@Data
public class UserRegisterDto implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 密码
     */
    private String userPassword;

    /**
     * 确认密码
     */
    private String checkPassword;
}

