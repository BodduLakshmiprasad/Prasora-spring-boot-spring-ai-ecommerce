
package com.example.springEcom.service;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.springEcom.model.Product;
import com.example.springEcom.repository.ProductRepository;

@Service
public class ProductService {

    private static final Logger logger =
            LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository repo;
    private final ProductEmbeddingService embeddingService;
    private final ProductDescriptionService descriptionService;

    public ProductService(
            ProductRepository repo,
            ProductEmbeddingService embeddingService,
            ProductDescriptionService descriptionService) {

        this.repo = repo;
        this.embeddingService = embeddingService;
        this.descriptionService = descriptionService;
    }

    // Add product with automatic AI description
    public Product addProduct(Product product, MultipartFile image)
            throws IOException {

        product.setImageName(image.getOriginalFilename());
        product.setImageType(image.getContentType());
        product.setImageData(image.getBytes());

        try {
            String description =
                    descriptionService.generateDescription(
                            product.getName(),
                            product.getCategory());

            product.setDescription(description);

        } catch (Exception e) {
            logger.error("AI description generation failed", e);
        }

        Product savedProduct = repo.save(product);

        try {
            embeddingService.indexProduct(savedProduct);
        } catch (Exception e) {
            logger.error("Product embedding generation failed for ID: {}",
                    savedProduct.getId(), e);
        }

        return savedProduct;
    }

    // Get all products
    public List<Product> getAllProducts() {
        return repo.findAll();
    }

    // Get product by ID
    public Product getProductById(int id) {
        return repo.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Product not found: " + id));
    }

    // Update product
    public Product updateProduct(
            int id,
            Product updatedProduct,
            MultipartFile image) throws IOException {

        Product existing = getProductById(id);

        boolean productDetailsChanged =
                !existing.getName().equals(updatedProduct.getName())
                || !existing.getCategory().equals(
                        updatedProduct.getCategory());

        existing.setName(updatedProduct.getName());
        existing.setPrice(updatedProduct.getPrice());
        existing.setCategory(updatedProduct.getCategory());

        if (image != null && !image.isEmpty()) {
            existing.setImageName(image.getOriginalFilename());
            existing.setImageType(image.getContentType());
            existing.setImageData(image.getBytes());
        }

        if (productDetailsChanged) {
            try {
                String description =
                        descriptionService.generateDescription(
                                existing.getName(),
                                existing.getCategory());

                existing.setDescription(description);

            } catch (Exception e) {
                logger.error(
                        "AI description refresh failed for product ID: {}",
                        id, e);
            }
        }

        Product savedProduct = repo.save(existing);

        try {
            embeddingService.indexProduct(savedProduct);
        } catch (Exception e) {
            logger.error("Product embedding refresh failed for ID: {}",
                    id, e);
        }

        return savedProduct;
    }

    // Delete product
    public void deleteProduct(int id) {

        Product product = getProductById(id);

        repo.delete(product);

        try {
            embeddingService.deleteProductEmbedding(id);
        } catch (Exception e) {
            logger.error("Product embedding deletion failed for ID: {}",
                    id, e);
        }
    }
}

