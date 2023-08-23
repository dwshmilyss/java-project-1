package com.yiban.spring.spring_boot.dev.redis.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 点赞数量 DTO。用于存储从 Redis 取出来的被点赞数量
 *
 * @auther WEI.DUAN
 * @date 2019/12/21
 * @website http://blog.csdn.net/dwshmilyss
 */
@Data
public class LikedCountDTO implements Serializable {

    private static final long serialVersionUID = -3278293647972989887L;
    private Integer count;
    private String userId;
    public LikedCountDTO() {
    }

    public LikedCountDTO(String userId, Integer count) {
        this.userId = userId;
        this.count = count;
    }
}