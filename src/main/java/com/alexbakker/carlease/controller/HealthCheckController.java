package com.alexbakker.carlease.controller;

import com.alexbakker.carlease.model.Customer;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/")
public class HealthCheckController {

    @GetMapping("/healthcheck")
    public String healthCheck() {
        return "I find your lack of faith disturbing!";
    }
}
