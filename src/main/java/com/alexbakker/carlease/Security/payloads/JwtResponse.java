package com.alexbakker.carlease.Security.payloads;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class JwtResponse {
    String token;
    String username;
    List<String> roles;
}
