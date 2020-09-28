package com.springmq.service;

import com.springmq.entity.Product;

import java.util.List;

public interface ConcurrencyService {
    public void manageRobbing(String mobile);
    public List<Product> findAll();

}
