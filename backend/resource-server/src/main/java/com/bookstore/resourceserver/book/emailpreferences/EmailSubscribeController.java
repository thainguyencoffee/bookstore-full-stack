package com.bookstore.resourceserver.book.emailpreferences;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class EmailSubscribeController {

    private final EmailSubscribeService emailSubscribeService;

    @PostMapping(path = "/email-preferences", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public EmailPreferences emailPreferences(@Valid @RequestBody EmailPreferencesRequestDto dto) {
        return emailSubscribeService.subscribe(dto);
    }

    @DeleteMapping("/email-preferences/{email}/unsubscribe")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unsubscribe(@PathVariable String email) {
        emailSubscribeService.unsubscribe(email);
    }
}
