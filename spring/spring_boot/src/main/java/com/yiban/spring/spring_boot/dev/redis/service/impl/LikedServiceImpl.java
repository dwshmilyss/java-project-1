package com.yiban.spring.spring_boot.dev.redis.service.impl;

import com.yiban.spring.spring_boot.dev.redis.entity.UserLike;
import com.yiban.spring.spring_boot.dev.redis.service.LikedService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @auther WEI.DUAN
 * @date 2019/12/21
 * @website http://blog.csdn.net/dwshmilyss
 */
@Service
@Slf4j
public class LikedServiceImpl implements LikedService {


    @Override
    public UserLike save(UserLike userlike) {
        return null;
    }

    @Override
    public List<UserLike> saveAll(List<UserLike> list) {
        return null;
    }

    @Override
    public Page<UserLike> getLikedListByLikedUserId(String likedUserId, Pageable pageable) {
        return null;
    }

    @Override
    public Page<UserLike> getLikedListByLikedPostId(String likedPostId, Pageable pageable) {
        return null;
    }

    @Override
    public UserLike getByLikedUserIdAndLikedPostId(String likedUserId, String likedPostId) {
        return null;
    }

    @Override
    public void transLikedFromRedis2DB() {

    }

    @Override
    public void transLikedCountFromRedis2DB() {

    }
}