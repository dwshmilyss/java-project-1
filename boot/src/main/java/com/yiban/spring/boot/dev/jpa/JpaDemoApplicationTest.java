package com.yiban.spring.boot.dev.jpa;

import com.yiban.spring.boot.dev.jpa.entity.Product;
import com.yiban.spring.boot.dev.jpa.repository.ProductRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
/**
 * @auther WEI.DUAN
 * @date 2019/12/21
 * @website http://blog.csdn.net/dwshmilyss
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class JpaDemoApplicationTest {
    @Autowired
    private ProductRepository productRepository;

    @Test
    public void testInsert() throws Exception {
        Product product = new Product();
        product.setId(22L);
        product.setProductName("test");
        product.setTypeName("test");
        product.setSaleCount(19);

        productRepository.save(product);
    }
}