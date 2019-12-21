package com.yiban.spring.boot.dev.jpa.repository;

import com.yiban.spring.boot.dev.jpa.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product,Long> {
    Product findByName(String productName);
}
