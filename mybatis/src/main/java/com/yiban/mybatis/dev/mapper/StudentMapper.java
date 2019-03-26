package com.yiban.mybatis.dev.mapper;

import com.yiban.mybatis.dev.entity.Student;

import java.util.List;

/**
 * @auther WEI.DUAN
 * @date 2019/3/26
 * @website http://blog.csdn.net/dwshmilyss
 */
public interface StudentMapper {

    /**
     * 批量插入学生
     */
    int insertList(List<Student> students);
}