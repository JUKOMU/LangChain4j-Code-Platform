package io.github.jukomu.langchain4jcodeplatform.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 对话历史视图
 *
 * @author JUKOMU
 */
@Data
public class ChatHistoryVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 消息
     */
    private String message;

    /**
     * 消息类型
     */
    private String messageType;

    /**
     * 应用id
     */
    private Long appId;

    /**
     * 创建用户id
     */
    private Long userId;

    /**
     * 父消息id
     */
    private Long parentId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 应用信息
     */
    private AppVo app;

    /**
     * 用户信息
     */
    private UserVo user;
}
