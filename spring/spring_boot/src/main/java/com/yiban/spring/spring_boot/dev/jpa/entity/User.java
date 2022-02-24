package com.yiban.spring.spring_boot.dev.jpa.entity;
import lombok.Data;

import javax.persistence.*;
import	java.io.Serializable;

/**
 * @auther WEI.DUAN
 * @date 2019/12/23
 * @website http://blog.csdn.net/dwshmilyss
 */
@Entity
@Data
//@Table(name = "User")
public class User implements Serializable {


    private static final long serialVersionUID = 4988784820900279807L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String name;

    private int age;


    public User(String name,int age) {
        this.name = name;
        this.age = age;
    }
}
