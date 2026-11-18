
package com.example.springEcom.controller;

import org.springframework.web.bind.annotation.*;

import com.example.springEcom.service.ShoppingAssistantService;

@RestController
@RequestMapping("/ai")
public class ShoppingAssistantController {

    private final ShoppingAssistantService assistantService;

    public ShoppingAssistantController(
            ShoppingAssistantService assistantService) {
        this.assistantService = assistantService;
    }

    public record ChatRequest(String question) {}

    @PostMapping("/chat")
    public String chat(@RequestBody ChatRequest request) {

        return assistantService.askQuestion(request.question());
    }
}
