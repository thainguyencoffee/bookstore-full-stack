package com.bookstore.gateway.web;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "fallback", produces = MediaType.APPLICATION_JSON_VALUE)
public class FallbackController {

    @GetMapping("/backend")
    public String backendFallback() {
        return "Backend service is taking too long to respond. Please try again later.";
    }

}
