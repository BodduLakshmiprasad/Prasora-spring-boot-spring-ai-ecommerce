
package com.example.springEcom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.springEcom.model.Product;

public interface ProductRepository
        extends JpaRepository<Product, Integer> {

}

