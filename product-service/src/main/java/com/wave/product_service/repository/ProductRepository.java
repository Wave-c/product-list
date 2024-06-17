package com.wave.product_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wave.product_service.entity.Product;

public interface ProductRepository extends JpaRepository<Product, String>
{
    
}
