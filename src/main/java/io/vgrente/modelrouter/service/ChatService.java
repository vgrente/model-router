package io.vgrente.modelrouter.service;

import io.vgrente.modelrouter.domain.MyChatRequest;
import io.vgrente.modelrouter.domain.MyChatResponse;
import io.vgrente.modelrouter.domain.RoutingDecision;
import io.vgrente.modelrouter.exception.NoModelAnswerException;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.anthropic.AnthropicChatOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.stereotype.Service;

/** Routes a prompt to the selected Claude model tier and returns its answer. */
@Service
public class ChatService {
  private static final Logger logger = LoggerFactory.getLogger(ChatService.class);

  private final ModelRouterService modelRouterService;

  private final ChatClient chatClient;

  public ChatService(ModelRouterService modelRouterService, ChatClient.Builder builder) {
    this.modelRouterService = modelRouterService;
    this.chatClient = builder.build();
  }

  /** Routes the prompt to a model tier, calls it, and returns the combined answer. */
  public MyChatResponse handleRequest(MyChatRequest myChatRequest) {
    String prompt = myChatRequest.prompt();
    RoutingDecision routingDecision = modelRouterService.route(prompt);
    ChatResponse chatResponse = chatClient
        .prompt()
        .user(prompt)
        .options(AnthropicChatOptions
            .builder()
            .model(routingDecision.model())
        )
        .call()
        .chatResponse();

    List<Generation> results = chatResponse == null ? null : chatResponse.getResults();
    if (results == null || results.isEmpty()) {
      throw new NoModelAnswerException(
          String.format("%s did not answer", routingDecision.model()));
    }

    String response = results.stream()
        .map(generation -> generation.getOutput().getText())
        .filter(text -> text != null && !text.isEmpty())
        .collect(Collectors.joining("\n"));

    MyChatResponse myChatResponse = new MyChatResponse(response, routingDecision);
    logger.debug("{}", myChatResponse);
    return myChatResponse;
  }
}
