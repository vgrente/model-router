package io.vgrente.modelrouter.domain;

import jakarta.validation.constraints.NotBlank;

/** Incoming chat request carrying the user's prompt. */
public record MyChatRequest(@NotBlank String prompt) {
}
