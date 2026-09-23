package io.vgrente.modelrouter.service;

import dev.dokimos.core.Assertions;
import dev.dokimos.core.Dataset;
import dev.dokimos.core.EvalTestCaseParam;
import dev.dokimos.core.Example;
import dev.dokimos.core.evaluators.ExactMatchEvaluator;
import io.vgrente.modelrouter.domain.RoutingDecision;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ModelRouterServiceTest {

  private static final Dataset DATASET = Dataset.builder()
      .name("model-tier-routing")
      .addExample(Example.builder()
          .input("input", "hi there!")
          .expectedOutput("output", "HAIKU")
          .build())
      .addExample(Example.builder()
          .input("input", "Summarize this paragraph in two sentences.")
          .expectedOutput("output", "SONNET")
          .build())
      .addExample(Example.builder()
          .input("input", "Refactor this 5-file module to remove the circular dependency "
              + "and explain the tradeoffs.")
          .expectedOutput("output", "OPUS")
          .build())
      .addExample(Example.builder()
          .input("input", "Prove that this sorting algorithm is stable and derive its "
              + "worst-case complexity.")
          .expectedOutput("output", "FABLE")
          .build())
      .build();

  @Autowired
  private ModelRouterService modelRouterService;

  @Test
  void routesToExpectedTier() {
    ExactMatchEvaluator evaluator = ExactMatchEvaluator.builder()
        .evaluationParams(
            List.of(EvalTestCaseParam.ACTUAL_OUTPUT, EvalTestCaseParam.EXPECTED_OUTPUT))
        .threshold(1.0)
        .build();

    for (Example example : DATASET.examples()) {
      RoutingDecision decision = modelRouterService.route(example.input());
      Assertions.assertEval(example.toTestCase(decision.modelTier().name()), evaluator);
    }
  }
}
