package org.example.web;

import jakarta.validation.constraints.NotBlank;

public record CreateMemberRequest(@NotBlank String name) {
}
