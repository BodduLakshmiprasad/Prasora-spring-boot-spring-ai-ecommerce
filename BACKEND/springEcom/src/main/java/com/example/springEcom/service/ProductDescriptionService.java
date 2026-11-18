
package com.example.springEcom.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class ProductDescriptionService {

    private final ChatClient chatClient;

    public ProductDescriptionService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public String generateDescription(String name, String category) {

        return chatClient.prompt()
                .system("""
                    You are an e-commerce product description writer.

                    Write an attractive and professional product
                    description using only the supplied product name
                    and category.

                    Do not invent specifications, warranty details,
                    materials, features or technical capabilities.

                    Keep the description concise, around 50 words.
                    Return only the description.
                    """)
                .user("""
                    Product name: %s
                    Product category: %s

                    Generate a suitable product description.
                    """.formatted(name, category))
                .call()
                .content();
    }

    public String generateDescription(String name, String category, String details) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'generateDescription'");
    }
}


