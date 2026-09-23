package io.vgrente.modelrouter.exception;

/** Thrown when the routed model does not return a usable answer. */
public class NoModelAnswerException extends RuntimeException {
  public NoModelAnswerException(String message) {
    super(message);
  }
}
