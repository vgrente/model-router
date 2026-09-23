package io.vgrente.modelrouter.controller;

import io.vgrente.modelrouter.domain.MyChatRequest;
import io.vgrente.modelrouter.domain.MyChatResponse;
import io.vgrente.modelrouter.service.ChatService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** REST endpoint that accepts a chat prompt and returns the routed model's response. */
@RestController
@RequestMapping("/api")
public class ChatController {

  private final ChatService chatService;

  public ChatController(ChatService chatService) {
    this.chatService = chatService;
  }

  @PostMapping("/chat")
  public MyChatResponse chat(@Valid @RequestBody MyChatRequest request) {
    return chatService.handleRequest(request);
  }
}
