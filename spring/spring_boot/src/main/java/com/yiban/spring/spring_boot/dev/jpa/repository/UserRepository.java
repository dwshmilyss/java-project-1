package com.yiban.spring.spring_boot.dev.jpa.repository;

import com.yiban.spring.spring_boot.dev.jpa.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    User findByName(String name);
}
