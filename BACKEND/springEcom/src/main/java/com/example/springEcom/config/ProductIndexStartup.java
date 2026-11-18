
package com.example.springEcom.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.example.springEcom.service.ProductEmbeddingService;

@Component
public class ProductIndexStartup {

    private static final Logger logger =
            LoggerFactory.getLogger(ProductIndexStartup.class);

    private final ProductEmbeddingService embeddingService;

    public ProductIndexStartup(
            ProductEmbeddingService embeddingService) {
        this.embeddingService = embeddingService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void indexProductsOnStartup() {
        try {
            String result = embeddingService.indexProducts();
            logger.info("AI product indexing: {}", result);
        } catch (Exception e) {
            logger.error(
                    "Automatic product indexing failed. Check whether Ollama is running.",
                    e);
        }
    }
}
