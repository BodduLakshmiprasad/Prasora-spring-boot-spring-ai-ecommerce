
package com.example.springEcom.controller;

import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.web.bind.annotation.*;

import com.example.springEcom.service.ProductEmbeddingService;

@RestController
@RequestMapping("/ai/products")
public class ProductIndexController {

    private final ProductEmbeddingService embeddingService;

    public ProductIndexController(
            ProductEmbeddingService embeddingService) {
        this.embeddingService = embeddingService;
    }

    // Index products from MySQL into VectorStore
    @PostMapping("/index")
    public String indexProducts() {
        return embeddingService.indexProducts();
    }

    // Search products using semantic similarity
    @GetMapping("/search")
    public List<Document> searchProducts(
            @RequestParam String query) {

        return embeddingService.searchProducts(query);
    }
}
