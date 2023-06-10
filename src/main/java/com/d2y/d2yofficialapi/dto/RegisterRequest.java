package com.d2y.d2yofficialapi.dto;

import com.d2y.d2yofficialapi.models.Role;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;
import java.util.List;

public record RegisterRequest(
    String username,
    String password,
    String email,
    @JsonProperty("full_name") String fullName,
    List<String> roles) {

}