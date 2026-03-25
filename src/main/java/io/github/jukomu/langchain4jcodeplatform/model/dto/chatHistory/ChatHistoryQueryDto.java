package io.github.jukomu.langchain4jcodeplatform.model.dto.chatHistory;

import io.github.jukomu.langchain4jcodeplatform.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 对话历史管理查询请求
 *
 * @author JUKOMU
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ChatHistoryQueryDto extends PageRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 应用 id
     */
    private Long appId;

    /**
     * 用户 id
     */
    private Long userId;

    /**
     * 消息类型
     */
    private String messageType;

    /**
     * 父消息 id
     */
    private Long parentId;

    /**
     * 消息关键字
     */
    private String message;
}
