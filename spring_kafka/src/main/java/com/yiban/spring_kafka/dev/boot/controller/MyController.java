package com.yiban.spring_kafka.dev.boot.controller;

import com.yiban.spring_kafka.dev.boot.domain.User;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @auther WEI.DUAN
 * @date 2019/7/1
 * @website http://blog.csdn.net/dwshmilyss
 */
@RestController
@RequestMapping(value = "/my")
public class MyController {
    @PostMapping("/find")
    public String find(@RequestBody String userId) {
        System.out.println(userId);
        return userId;
    }

    @PostMapping("/find1")
    public String find1(@RequestParam String userId,@RequestParam String userName ) {
        System.out.println(userId + " = " + userName);
        return userId + " = " + userName;
    }

    @GetMapping("/find2")
    public String find2(@RequestParam String userId) {
        System.out.println(userId);
        return userId;
    }

    @PostMapping("/addmap")
    public String addUser1(@RequestBody Map map) {
        System.out.println(map);
        return map.toString();
    }

    @PostMapping("/adduser")
    public String addUser(@RequestBody User user) {
        System.out.println(user);
        return "Success";
    }

    @ResponseBody
    @PostMapping("/finduser")
    public User findUser(@RequestBody User user) {
        System.out.println(user);
        return user;
    }
}