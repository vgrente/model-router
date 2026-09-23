package io.vgrente.modelrouter.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.vgrente.modelrouter.domain.MyChatRequest;
import io.vgrente.modelrouter.domain.MyChatResponse;
import io.vgrente.modelrouter.domain.RoutingDecision;
import io.vgrente.modelrouter.service.ChatService;
import io.vgrente.modelrouter.service.ModelTier;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = ChatController.class)
class ChatControllerTest {
  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private ChatService chatService;

  @Test
  void postPromptReturnsOk() throws Exception {
    MyChatResponse response = new MyChatResponse(
        "Hey! 👋 How's it going? What can I help you with today?",
        new RoutingDecision(ModelTier.HAIKU, "claude-haiku-4-5", 0.66,
            Map.of("SONNET", 0.0, "FABLE", 0.0, "OPUS", 0.0, "HAIKU", 1.0)));
    given(chatService.handleRequest(new MyChatRequest("hello there"))).willReturn(response);

    mockMvc.perform(post("/api/chat")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"prompt\": \"hello there\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.response").value(response.response()))
        .andExpect(jsonPath("$.routingDecision.modelTier").value("HAIKU"))
        .andExpect(jsonPath("$.routingDecision.model").value("claude-haiku-4-5"))
        .andExpect(jsonPath("$.routingDecision.answerConfidence").value(0.66));
  }

  @Test
  void postToUnknownPathReturnsNotFound() throws Exception {
    MyChatResponse response = new MyChatResponse(
        "Hey! 👋 How's it going? What can I help you with today?",
        new RoutingDecision(ModelTier.HAIKU, "claude-haiku-4-5", 0.66,
            Map.of("SONNET", 0.0, "FABLE", 0.0, "OPUS", 0.0, "HAIKU", 1.0)));
    given(chatService.handleRequest(new MyChatRequest("hello there"))).willReturn(response);

    mockMvc.perform(post("/api/chats")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"prompt\": \"hello there\"}"))
        .andExpect(status().isNotFound());
  }

  @Test
  void postBlankPromptReturnsBadRequest() throws Exception {
    mockMvc.perform(post("/api/chat")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"prompt\": \"\"}"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void postMissingPromptReturnsBadRequest() throws Exception {
    mockMvc.perform(post("/api/chat")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{}"))
        .andExpect(status().isBadRequest());
  }
}
