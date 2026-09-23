package io.vgrente.modelrouter.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.vgrente.modelrouter.domain.MyChatRequest;
import io.vgrente.modelrouter.exception.NoModelAnswerException;
import io.vgrente.modelrouter.service.ChatService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = ChatController.class)
class GlobalExceptionHandlerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private ChatService chatService;

  @Test
  void noModelAnswerExceptionReturnsBadGatewayWithMessage() throws Exception {
    given(chatService.handleRequest(new MyChatRequest("hello there")))
        .willThrow(new NoModelAnswerException("claude-haiku-4-5 did not answer"));

    mockMvc.perform(post("/api/chat")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"prompt\": \"hello there\"}"))
        .andExpect(status().isBadGateway())
        .andExpect(jsonPath("$.error").value("claude-haiku-4-5 did not answer"));
  }

  @Test
  void unexpectedExceptionReturnsInternalServerErrorWithoutLeakingMessage() throws Exception {
    given(chatService.handleRequest(new MyChatRequest("hello there")))
        .willThrow(new RuntimeException("connection refused to internal-host:5432"));

    mockMvc.perform(post("/api/chat")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"prompt\": \"hello there\"}"))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.error").value("An unexpected error occurred"));
  }

  @Test
  void noResourceFoundExceptionReturnsNotFound() throws Exception {
    mockMvc.perform(post("/api/chats")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"prompt\": \"hello there\"}"))
        .andExpect(status().isNotFound());
  }

  @Test
  void methodArgumentNotValidExceptionReturnsBadRequestWithFieldDetails() throws Exception {
    mockMvc.perform(post("/api/chat")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"prompt\": \"\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Validation failed"))
        .andExpect(jsonPath("$.details.prompt").exists());
  }
}
