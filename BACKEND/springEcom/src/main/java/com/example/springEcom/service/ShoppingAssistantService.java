
package com.example.springEcom.service;

import java.util.List;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import com.example.springEcom.model.Product;
import com.example.springEcom.repository.ProductRepository;

@Service
public class ShoppingAssistantService {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    private final ProductRepository productRepository;

    public ShoppingAssistantService(
            ChatClient.Builder chatClientBuilder,
            VectorStore vectorStore,
            ProductRepository productRepository) {

        this.chatClient = chatClientBuilder.build();
        this.vectorStore = vectorStore;
        this.productRepository = productRepository;
    }

    public String askQuestion(String question) {

        // 1. Find relevant product documents
        List<Document> documents =
                vectorStore.similaritySearch(question);

        if (documents == null || documents.isEmpty()) {
            return "I couldn't find relevant products. Please try another search.";
        }

        // 2. Fetch actual products from MySQL
        StringBuilder productContext = new StringBuilder();

        for (Document document : documents) {

            Object productIdValue =
                    document.getMetadata().get("productId");

            if (productIdValue == null) {
                continue;
            }

            int productId =
                    Integer.parseInt(String.valueOf(productIdValue));

            productRepository.findById(productId).ifPresent(product -> {

                productContext.append("Product ID: ")
                        .append(product.getId())
                        .append("\nName: ")
                        .append(product.getName())
                        .append("\nCategory: ")
                        .append(product.getCategory())
                        .append("\nPrice: ")
                        .append(product.getPrice())
                        .append("\n\n");
            });
        }

        if (productContext.isEmpty()) {
            return "I couldn't find matching products in the catalogue.";
        }

        // 3. Send the question and actual product data to Llama
        return chatClient.prompt()
                .system("""
                        You are an AI shopping assistant for an e-commerce store.

                        Recommend products using only the supplied product information.
                        Never invent product names, prices, specifications or availability.
                        If the supplied products do not answer the customer's question,
                        clearly explain that the catalogue information is insufficient.
                        Be helpful, concise and clear.
                        """)
                .user("""
                        Customer question:
                        %s

                        Products retrieved from our MySQL catalogue:
                        %s

                        Recommend the most relevant products to the customer.
                        """.formatted(question, productContext))
                .call()
                .content();
    }
}
