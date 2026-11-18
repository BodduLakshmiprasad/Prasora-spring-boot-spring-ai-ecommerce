package com.example.springEcom.controller;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.springEcom.model.Product;
import com.example.springEcom.service.ProductService;
import org.springframework.http.ResponseEntity;
import java.util.List;
import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
@RequestMapping("/products")
@CrossOrigin(origins = "http://localhost:5173")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Product addProduct(
            @RequestParam("name") String name,
            @RequestParam("price") double price,
            @RequestParam("category") String category,
            @RequestParam("image") MultipartFile image)
            throws IOException {

        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        product.setCategory(category);

        return service.addProduct(product, image);
    }
    // Fetch one product with its image data
@GetMapping("/{id}")
public Product getProductById(@PathVariable int id) {
    return service.getProductById(id);
}

// Fetch only the image for a product
@GetMapping("/{id}/image")
public ResponseEntity<byte[]> getProductImage(@PathVariable int id) {
    Product product = service.getProductById(id);

    return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(
                    product.getImageType()))
            .body(product.getImageData());
}

// Update product details and optionally its image
@PutMapping(value = "/{id}",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public Product updateProduct(
        @PathVariable int id,
        @RequestParam("name") String name,
        @RequestParam("price") double price,
        @RequestParam("category") String category,
        @RequestParam(value = "image", required = false)
                MultipartFile image) throws IOException {

    Product updated = new Product();
    updated.setName(name);
    updated.setPrice(price);
    updated.setCategory(category);

    return service.updateProduct(id, updated, image);
}

// Delete product
@DeleteMapping("/{id}")
public String deleteProduct(@PathVariable int id) {
    service.deleteProduct(id);
    return "Product deleted successfully";
}
@GetMapping
public List<Product> getAllProducts() {
    return service.getAllProducts();
}
}