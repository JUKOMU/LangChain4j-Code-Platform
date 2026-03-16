package io.github.jukomu.langchain4jcodeplatform.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * @author JUKOMU
 * @Description:
 * @Project: LangChain4j-Code-Platform
 * @Date: 2026/3/16
 */
@Data
public class UserUpdateDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 简介
     */
    private String userProfile;

    /**
     * 用户角色：user/admin
     */
    private String userRole;
}
