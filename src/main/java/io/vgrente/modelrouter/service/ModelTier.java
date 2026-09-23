package io.vgrente.modelrouter.service;

/** The Claude model tiers a prompt can be routed to, cheapest first. */
public enum ModelTier {

  HAIKU("claude-haiku-4-5",
      "Quick, low-stakes requests: greetings, one-line facts, simple formatting, short rewrites. "
          + "A small, fast model answers these well."),

  SONNET("claude-sonnet-4-5",
      "Everyday tasks: summarize a passage, explain a concept, write a short function, "
          + "answer a clear question in a few sentences."),

  OPUS("claude-opus-5",
      "Complex professional work: multi-step reasoning, code that spans several files, "
          + "detailed analysis, careful long-form writing."),

  FABLE("claude-fable-5",
      "The hardest problems: deep research, system architecture, tricky debugging, formal "
          + "proofs, long tasks where a mistake is costly.");

  private final String modelId;

  private final String modelDescription;

  ModelTier(String modelId, String modelDescription) {
    this.modelId = modelId;
    this.modelDescription = modelDescription;
  }

  public String modelDescription() {
    return modelDescription;
  }

  public String modelId() {
    return modelId;
  }
}
