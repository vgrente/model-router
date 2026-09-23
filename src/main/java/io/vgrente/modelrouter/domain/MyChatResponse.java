package io.vgrente.modelrouter.domain;

/** Chat response returned to the caller, paired with the routing decision that produced it. */
public record MyChatResponse(String response, RoutingDecision routingDecision) {
}
