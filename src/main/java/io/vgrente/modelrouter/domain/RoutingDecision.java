package io.vgrente.modelrouter.domain;

import io.vgrente.modelrouter.service.ModelTier;
import java.util.Map;

/** The model tier chosen for a prompt, along with the router's confidence in that choice. */
public record RoutingDecision(
    ModelTier modelTier,
    String model,
    double answerConfidence,
    Map<String, Double> answerProbabilities) {
}
