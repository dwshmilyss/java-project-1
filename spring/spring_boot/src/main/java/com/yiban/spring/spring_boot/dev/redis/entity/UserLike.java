package com.yiban.spring.spring_boot.dev.redis.entity;

import com.yiban.spring.spring_boot.dev.redis.utils.LikedStatesEnum;
import lombok.Data;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
/**
 * @auther WEI.DUAN
 * @date 2019/12/21
 * @website http://blog.csdn.net/dwshmilyss
 * 用户点赞表对应的实体类
 */
@Entity
@Data
public class UserLike {
    /**
     *主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 被点赞用户ID
     */
    private String likedUserId;

    /**
     * 点赞用户ID
     */
    private String likedPostId;

    /**
     * 点赞状态 默认未点赞
     */
    private Integer status = LikedStatesEnum.UNLIKE.getCode();

    public UserLike() {
    }

    public UserLike(String likedUserId, String likedPostId, Integer status) {
        this.likedUserId = likedUserId;
        this.likedPostId = likedPostId;
        this.status = status;
    }
}