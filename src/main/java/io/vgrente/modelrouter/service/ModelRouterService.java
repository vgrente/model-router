package io.vgrente.modelrouter.service;

import static java.util.Map.of;

import io.vgrente.modelrouter.domain.RoutingDecision;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springaicommunity.typesafe.TypeSafeClient;
import org.springaicommunity.typesafe.question.Choice;
import org.springaicommunity.typesafe.response.ChoiceAnswer;
import org.springframework.stereotype.Service;

/** Classifies a prompt and decides which Claude model tier should answer it. */
@Service
public class ModelRouterService {

  private static final Logger log = LoggerFactory.getLogger(ModelRouterService.class);

  static final String QUESTION = "tier";

  private final TypeSafeClient typeSafeClient;

  private final Choice tierChoice;

  /** Builds the tier-choice question from the available {@link ModelTier} options. */
  public ModelRouterService(TypeSafeClient typeSafeClient) {
    this.typeSafeClient = typeSafeClient;
    Choice.Builder choice = Choice
        .builder()
        .instructions("Which model tier is the cheapest one that can still answer the prompt "
            + "correctly? Prefer the cheaper tier unless the prompt clearly needs more "
            + "capability.");

    for (ModelTier model : ModelTier.values()) {
      choice.option(model.name(), model.modelDescription());
    }
    this.tierChoice = choice.build();
  }

  /** Asks the routing model which tier should handle the prompt. */
  public RoutingDecision route(String prompt) {
    ChoiceAnswer answer = this.typeSafeClient
        .systemOne(prompt, of(QUESTION, this.tierChoice))
        .choice(QUESTION);
    ModelTier tier = ModelTier.valueOf(answer.value());
    log.info("Routing to {} with confidence {} ", answer.value(), answer.confidence());
    return new RoutingDecision(tier, tier.modelId(), answer.confidence(), answer.probabilities());
  }
}
