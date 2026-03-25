package io.github.jukomu.langchain4jcodeplatform.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import io.github.jukomu.langchain4jcodeplatform.model.entity.ChatHistory;
import io.github.jukomu.langchain4jcodeplatform.mapper.ChatHistoryMapper;
import io.github.jukomu.langchain4jcodeplatform.service.ChatHistoryService;
import org.springframework.stereotype.Service;

/**
 * 对话历史 服务层实现。
 *
 * @author JUKOMU
 */
@Service
public class ChatHistoryServiceImpl extends ServiceImpl<ChatHistoryMapper, ChatHistory>  implements ChatHistoryService{

}
