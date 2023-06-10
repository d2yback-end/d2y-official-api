package com.d2y.d2yofficialapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;

public record RegisterResponse(
    String username,
    String email,
    @JsonProperty("full_name") String fullName,
    List<String> roles,
    @JsonProperty("created") LocalDateTime created) {

}
