package com.wave.product_service.entity;

import java.util.Date;

import com.wave.product.Product.ProductDto;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "product")
@NoArgsConstructor
public class Product
{
    @Id
    private String id;

    private String title;
    private String note;
    private float count;

    private Date created_at = new Date();

    public Product(ProductDto productGrpcDto)
    {
        this.id = productGrpcDto.getId();
        this.title = productGrpcDto.getTitle();
        this.note = productGrpcDto.getNote();
        this.count = productGrpcDto.getCount();
    }
}
