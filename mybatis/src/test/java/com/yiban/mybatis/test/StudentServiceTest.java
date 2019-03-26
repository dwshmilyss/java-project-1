package com.yiban.mybatis.test;

import com.yiban.mybatis.dev.mapper.StudentMapper;
import com.yiban.mybatis.dev.entity.Student;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;

/**
 * @auther WEI.DUAN
 * @date 2019/3/26
 * @website http://blog.csdn.net/dwshmilyss
 */
public class StudentServiceTest {
    private static SqlSessionFactory sqlSessionFactory;

    @BeforeClass
    public static void init() {
        try {
            Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void insertList() {
        SqlSession sqlSession = null;
        sqlSession = sqlSessionFactory.openSession();
        StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);

        List<Student> students = new LinkedList<>();
        Student stu1 = new Student();
        stu1.setName("批量01");
        stu1.setPhone("13888888881");
        stu1.setLocked((byte) 0);
        stu1.setEmail("13888888881@138.com");
        stu1.setSex((byte) 1);
        students.add(stu1);

        Student stu2 = new Student();
        stu2.setName("批量02");
        stu2.setPhone("13888888882");
        stu2.setLocked((byte) 0);
        stu2.setEmail("13888888882@138.com");
        stu2.setSex((byte) 0);
        students.add(stu2);

        System.out.println(studentMapper.insertList(students));
        sqlSession.commit();
        sqlSession.close();
    }
}