package io.vgrente.modelrouter.service;

import dev.dokimos.core.Assertions;
import dev.dokimos.core.ClasspathDatasetResolver;
import dev.dokimos.core.Dataset;
import dev.dokimos.core.EvalTestCaseParam;
import dev.dokimos.core.Example;
import dev.dokimos.core.JudgeLM;
import dev.dokimos.core.evaluators.LLMJudgeEvaluator;
import dev.dokimos.springai.SpringAiSupport;
import io.vgrente.modelrouter.domain.MyChatRequest;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ChatServiceTest {

  private static final Dataset DATASET =
      new ClasspathDatasetResolver().resolve("classpath:datasets/chat-dataset.json");

  @Autowired
  private ChatService chatService;

  @Autowired
  private ChatClient.Builder chatClientBuilder;

  private JudgeLM judge;

  @BeforeEach
  void setUp() {
    judge = SpringAiSupport.asJudge(chatClientBuilder);
  }

  @Test
  void answersHelpfullyAndAccurately() {
    LLMJudgeEvaluator evaluator = LLMJudgeEvaluator.builder()
        .criteria("The response directly and helpfully answers the user's prompt, "
            + "stays on topic, and contains no fabricated or irrelevant content.")
        .evaluationParams(List.of(EvalTestCaseParam.INPUT, EvalTestCaseParam.ACTUAL_OUTPUT))
        .judge(judge)
        .scoreRange(0.0, 1.0)
        .threshold(0.7)
        .build();

    for (Example example : DATASET.examples()) {
      String response =
          chatService.handleRequest(new MyChatRequest(example.input())).response();
      Assertions.assertEval(example.toTestCase(response), evaluator);
    }
  }
}
