package io.github.jukomu.langchain4jcodeplatform.controller;

import com.mybatisflex.core.paginate.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import io.github.jukomu.langchain4jcodeplatform.model.entity.ChatHistory;
import io.github.jukomu.langchain4jcodeplatform.service.ChatHistoryService;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * 对话历史 控制层。
 *
 * @author JUKOMU
 */
@RestController
@RequestMapping("/chatHistory")
@RequiredArgsConstructor
public class ChatHistoryController {

    private final ChatHistoryService chatHistoryService;

}
