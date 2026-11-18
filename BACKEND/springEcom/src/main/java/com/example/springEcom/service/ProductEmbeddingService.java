
package com.example.springEcom.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import com.example.springEcom.model.Product;
import com.example.springEcom.repository.ProductRepository;

@Service
public class ProductEmbeddingService {

    private final ProductRepository productRepository;
    private final VectorStore vectorStore;

    public ProductEmbeddingService(
            ProductRepository productRepository,
            VectorStore vectorStore) {
        this.productRepository = productRepository;
        this.vectorStore = vectorStore;
    }

    // Create or replace the embedding for one product
    public void indexProduct(Product product) {

        String documentId = "product-" + product.getId();

        // Remove the previous embedding, if one exists
        vectorStore.delete(List.of(documentId));

       String content = "Product name: " + product.getName()
        + ". Category: " + product.getCategory()
        + ". Description: " + product.getDescription();

        Document document = Document.builder()
                .id(documentId)
                .text(content)
                .metadata("productId", product.getId())
                .build();

        vectorStore.add(List.of(document));
    }

    // Remove a product's embedding
    public void deleteProductEmbedding(int productId) {
        vectorStore.delete(List.of("product-" + productId));
    }

    // Index all products stored in MySQL
    public String indexProducts() {

        List<Product> products = productRepository.findAll();

        List<Document> documents = new ArrayList<>();
        List<String> documentIds = new ArrayList<>();

        for (Product product : products) {

            String documentId = "product-" + product.getId();

            documentIds.add(documentId);

            String content = "Product name: " + product.getName()
        + ". Category: " + product.getCategory()
        + ". Description: " + product.getDescription();

            Document document = Document.builder()
                    .id(documentId)
                    .text(content)
                    .metadata("productId", product.getId())
                    .build();

            documents.add(document);
        }

        if (!documentIds.isEmpty()) {
            vectorStore.delete(documentIds);
            vectorStore.add(documents);
        }

        return products.size() + " products indexed successfully";
    }

    // Semantic product search
    public List<Document> searchProducts(String query) {
        return vectorStore.similaritySearch(query);
    }
}
