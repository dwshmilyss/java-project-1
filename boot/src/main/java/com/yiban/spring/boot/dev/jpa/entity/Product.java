package com.yiban.spring.boot.dev.jpa.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @auther WEI.DUAN
 * @date 2019/12/21
 * @website http://blog.csdn.net/dwshmilyss
 */
@Entity
@Data
public class Product implements Serializable {
    private static final long serialVersionUID = 8395452327477423962L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true)
    private String productName;

    @Column(nullable = true)
    private String typeName;

    @Column(nullable = true)
    private int saleCount;
}