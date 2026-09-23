package io.vgrente.modelrouter.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.vgrente.modelrouter.domain.MyChatRequest;
import io.vgrente.modelrouter.domain.RoutingDecision;
import io.vgrente.modelrouter.exception.NoModelAnswerException;
import java.util.Collections;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;

@ExtendWith(MockitoExtension.class)
class ChatServiceUnitTest {

  private static final RoutingDecision ROUTING_DECISION =
      new RoutingDecision(ModelTier.HAIKU, "claude-haiku-4-5", 0.9, Map.of("HAIKU", 1.0));

  @Mock
  private ModelRouterService modelRouterService;

  @Mock
  private ChatClient.Builder chatClientBuilder;

  @Mock
  private ChatClient chatClient;

  @Mock
  private ChatClient.ChatClientRequestSpec requestSpec;

  @Mock
  private ChatClient.CallResponseSpec callResponseSpec;

  private ChatService chatService;

  @BeforeEach
  void setUp() {
    when(chatClientBuilder.build()).thenReturn(chatClient);
    when(chatClient.prompt()).thenReturn(requestSpec);
    when(requestSpec.user(anyString())).thenReturn(requestSpec);
    when(requestSpec.options(any())).thenReturn(requestSpec);
    when(requestSpec.call()).thenReturn(callResponseSpec);
    when(modelRouterService.route(anyString())).thenReturn(ROUTING_DECISION);

    chatService = new ChatService(modelRouterService, chatClientBuilder);
  }

  @Test
  void throwsNoModelAnswerExceptionWhenChatResponseIsNull() {
    when(callResponseSpec.chatResponse()).thenReturn(null);
    MyChatRequest request = new MyChatRequest("hello");

    assertThatThrownBy(() -> chatService.handleRequest(request))
        .isInstanceOf(NoModelAnswerException.class)
        .hasMessage("claude-haiku-4-5 did not answer");
  }

  @Test
  void throwsNoModelAnswerExceptionWhenResultsAreNull() {
    ChatResponse chatResponse = mock(ChatResponse.class);
    when(chatResponse.getResults()).thenReturn(null);
    when(callResponseSpec.chatResponse()).thenReturn(chatResponse);
    MyChatRequest request = new MyChatRequest("hello");

    assertThatThrownBy(() -> chatService.handleRequest(request))
        .isInstanceOf(NoModelAnswerException.class)
        .hasMessage("claude-haiku-4-5 did not answer");
  }

  @Test
  void throwsNoModelAnswerExceptionWhenResultsAreEmpty() {
    ChatResponse chatResponse = mock(ChatResponse.class);
    when(chatResponse.getResults()).thenReturn(Collections.emptyList());
    when(callResponseSpec.chatResponse()).thenReturn(chatResponse);
    MyChatRequest request = new MyChatRequest("hello");

    assertThatThrownBy(() -> chatService.handleRequest(request))
        .isInstanceOf(NoModelAnswerException.class)
        .hasMessage("claude-haiku-4-5 did not answer");
  }
}
