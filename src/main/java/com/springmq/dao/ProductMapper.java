package com.springmq.dao;

import com.springmq.entity.Product;

import java.util.List;

public interface ProductMapper {
   public Product selectByproductNo(String productNo);
   public void updateTotal(String productNo);
   public List<Product> findAll();
}
