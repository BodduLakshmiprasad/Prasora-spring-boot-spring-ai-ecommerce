
package com.example.springEcom.controller;

import org.springframework.web.bind.annotation.*;

import com.example.springEcom.service.ProductDescriptionService;

@RestController
@RequestMapping("/ai/products")
@CrossOrigin(origins = "http://localhost:5173")
public class ProductDescriptionController {

    private final ProductDescriptionService descriptionService;

    public ProductDescriptionController(
            ProductDescriptionService descriptionService) {
        this.descriptionService = descriptionService;
    }

    public record DescriptionRequest(
            String name,
            String category,
            String details) {
    }

    public record DescriptionResponse(String description) {
    }

    @PostMapping("/description")
    public DescriptionResponse generateDescription(
            @RequestBody DescriptionRequest request) {

        String description = descriptionService.generateDescription(
                request.name(),
                request.category(),
                request.details());

        return new DescriptionResponse(description);
    }
}

