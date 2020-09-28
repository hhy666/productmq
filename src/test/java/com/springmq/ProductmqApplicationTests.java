package com.springmq;

import com.springmq.entity.Product;
import com.springmq.service.ConcurrencyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class ProductmqApplicationTests {
    @Autowired
    private ConcurrencyService concurrencyService;

    @Test
    void contextLoads() {
    }
    @Test
    public void findAll(){
        List<Product> product = concurrencyService.findAll();

        product.forEach(pro -> System.out.println(pro));
    }
}
