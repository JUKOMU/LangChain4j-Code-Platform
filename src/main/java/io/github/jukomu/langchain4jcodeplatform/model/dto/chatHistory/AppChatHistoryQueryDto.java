package io.github.jukomu.langchain4jcodeplatform.model.dto.chatHistory;

import io.github.jukomu.langchain4jcodeplatform.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 应用对话历史查询请求
 *
 * @author JUKOMU
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AppChatHistoryQueryDto extends PageRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 应用 id
     */
    private Long appId;

    /**
     * 游标查询，最后一条消息的创建时间
     */
    private LocalDateTime lastCreateTime;
}
