package io.github.jukomu.langchain4jcodeplatform.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * @author JUKOMU
 * @Description: 用户新增数据传输对象
 * @Project: LangChain4j-Code-Platform
 * @Date: 2026/3/16
 */
@Data
public class UserAddDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户简介
     */
    private String userProfile;

    /**
     * 用户角色: user, admin
     */
    private String userRole;

}
