package com.yiban.mybatis.test;

import com.yiban.mybatis.dev.mapper.StudentMapper;
import com.yiban.mybatis.dev.entity.Student;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.session.TransactionIsolationLevel;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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

    @Test
    public void selectByStudent() {
        SqlSession sqlSession = sqlSessionFactory.openSession(TransactionIsolationLevel.READ_COMMITTED);
        StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);

        Student search = new Student();
        search.setName("明");

        System.out.println("只有名字时的查询");
        List<Student> studentsByName = studentMapper.selectByStudentSelective(search);
        for (int i = 0; i < studentsByName.size(); i++) {
            //这样的打印需要注意判断对象是否为null 如果为null 利用反射会抛异常
            System.out.println(ToStringBuilder.reflectionToString(studentsByName.get(i), ToStringStyle.MULTI_LINE_STYLE));
        }

        search.setName(null);
        search.setSex((byte) 1);
        System.out.println("只有性别时的查询");
        List<Student> studentsBySex = studentMapper.selectByStudentSelective(search);
        for (int i = 0; i < studentsBySex.size(); i++) {
            System.out.println(ToStringBuilder.reflectionToString(studentsBySex.get(i), ToStringStyle.MULTI_LINE_STYLE));
        }

        System.out.println("姓名和性别同时存在的查询");
        search.setName("明");
        List<Student> studentsByNameAndSex = studentMapper.selectByStudentSelective(search);
        for (int i = 0; i < studentsByNameAndSex.size(); i++) {
            System.out.println(ToStringBuilder.reflectionToString(studentsByNameAndSex.get(i), ToStringStyle.MULTI_LINE_STYLE));
        }

        sqlSession.commit();
        sqlSession.close();
    }

    @Test
    public void selectByIdOrName() {
        SqlSession sqlSession = null;
        sqlSession = sqlSessionFactory.openSession();
        StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);

        Student student = new Student();
        student.setName("小明");
        student.setStudentId(1);

        Student studentById = studentMapper.selectByIdOrName(student);
        Assert.assertNotNull(studentById);
        System.out.println("有 ID 则根据 Id 获取");
        System.out.println(ToStringBuilder.reflectionToString(studentById, ToStringStyle.MULTI_LINE_STYLE));

        student.setStudentId(0);
        Student studentByName = studentMapper.selectByIdOrName(student);
        System.out.println("没有 ID 则根据 name 获取");
        Assert.assertNotNull(studentByName);
        System.out.println(ToStringBuilder.reflectionToString(studentByName, ToStringStyle.MULTI_LINE_STYLE));

        student.setName(null);
        Student studentNull = studentMapper.selectByIdOrName(student);
        System.out.println("没有 ID 和 name, 返回 null");
        Assert.assertNull(studentNull);

        sqlSession.commit();
        sqlSession.close();
    }

    @Test
    public void selectByPrimaryKey() {
        SqlSession sqlSession = null;
        sqlSession = sqlSessionFactory.openSession();
        StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);

        Student studentById = studentMapper.selectByPrimaryKey(1);
        Assert.assertNotNull(studentById);
        System.out.println(ToStringBuilder.reflectionToString(studentById, ToStringStyle.MULTI_LINE_STYLE));
    }

    @Test
    public void selectBetweenCreatedTime() throws ParseException {
        SqlSession sqlSession = null;
        sqlSession = sqlSessionFactory.openSession();
        StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);

        Map<String, Object> paramsMap = new HashMap<>(2);
        /**
         * 下面三种方式都可以比较时间
         */
//        paramsMap.put("bTime", "2018-08-01");
//        paramsMap.put("eTime", "2018-09-01");

//        paramsMap.put("bTime", new Date(new SimpleDateFormat("yyyy-MM-dd").parse("2018-08-01").getTime()));
//        paramsMap.put("eTime", new Date(new SimpleDateFormat("yyyy-MM-dd").parse("2018-09-01").getTime()));


        paramsMap.put("bTime", new Timestamp(new SimpleDateFormat("yyyy-MM-dd").parse("2018-08-01").getTime()));
        paramsMap.put("eTime", new Timestamp(new SimpleDateFormat("yyyy-MM-dd").parse("2018-09-01").getTime()));
        List<Student> studentsByTime = studentMapper.selectBetweenCreatedTime(paramsMap);
        for (int i = 0; i < studentsByTime.size(); i++) {
            System.out.println(ToStringBuilder.reflectionToString(studentsByTime.get(i), ToStringStyle.MULTI_LINE_STYLE));
        }
    }


    @Test
    public void selectBetweenCreatedTimeAnno() throws ParseException {
        SqlSession sqlSession = null;
        sqlSession = sqlSessionFactory.openSession();
        StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);

        Date bTime = new Date(new SimpleDateFormat("yyyy-MM-dd").parse("2018-08-01").getTime());
        Date eTime = new Date(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2018-08-31 23:59:59").getTime());


        List<Student> studentsByTime = studentMapper.selectBetweenCreatedTimeAnno(bTime, eTime);
        for (int i = 0; i < studentsByTime.size(); i++) {
            System.out.println(ToStringBuilder.reflectionToString(studentsByTime.get(i), ToStringStyle.MULTI_LINE_STYLE));
        }
    }


    @Test
    public void selectByStudentWhereTag() {
        SqlSession sqlSession = null;
        sqlSession = sqlSessionFactory.openSession();
        StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);

        Student search = new Student();
        search.setName("明");

        System.out.println("只有名字时的查询");
        List<Student> studentsByName = studentMapper.selectByStudentSelectiveWhereTag(search);
        for (int i = 0; i < studentsByName.size(); i++) {
            System.out.println(ToStringBuilder.reflectionToString(studentsByName.get(i), ToStringStyle.MULTI_LINE_STYLE));
        }

        search.setSex((byte) 1);
        System.out.println("姓名和性别同时存在的查询");
        List<Student> studentsBySex = studentMapper.selectByStudentSelectiveWhereTag(search);
        for (int i = 0; i < studentsBySex.size(); i++) {
            System.out.println(ToStringBuilder.reflectionToString(studentsBySex.get(i), ToStringStyle.MULTI_LINE_STYLE));
        }

        System.out.println("姓名和性别都不存在时查询");
        search.setName(null);
        search.setSex(null);
        List<Student> studentsByNameAndSex = studentMapper.selectByStudentSelectiveWhereTag(search);
        for (int i = 0; i < studentsByNameAndSex.size(); i++) {
            System.out.println(ToStringBuilder.reflectionToString(studentsByNameAndSex.get(i), ToStringStyle.MULTI_LINE_STYLE));
        }

        sqlSession.commit();
        sqlSession.close();
    }


    @Test
    public void test() throws ParseException {
        System.out.println(new Timestamp(new SimpleDateFormat("yyyy-MM-dd").parse("2018-08-01").getTime()));
    }
}