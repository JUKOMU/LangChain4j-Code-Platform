package io.github.jukomu.langchain4jcodeplatform.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 应用对话历史分页结果
 *
 * @author JUKOMU
 */
@Data
public class AppChatHistoryPageVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 当前批次消息
     */
    private List<ChatHistoryVo> records;

    /**
     * 是否还有更早的历史记录
     */
    private boolean hasMore;

    /**
     * 下次查询的游标，最后一条消息的创建时间
     */
    private LocalDateTime nextLastCreateTime;
}
