package com.wave.product_service.service;

import com.wave.product.Product.ProductDto;
import com.wave.product_service.entity.Product;
import com.wave.product_service.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ProductService 
{
    private final ProductRepository productRepository;

    public Product save(Product product)
    {
        return productRepository.save(product);
    }

    public Product save(ProductDto productDto)
    {
        return save(new Product(productDto));
    }
}
