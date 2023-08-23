package com.yiban.spring.spring_boot.dev.jpa;

import com.yiban.spring.spring_boot.dev.jpa.entity.User;
import com.yiban.spring.spring_boot.dev.jpa.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
/**
 * @auther WEI.DUAN
 * @date 2019/12/21
 * @website http://blog.csdn.net/dwshmilyss
 */
//@RunWith(SpringRunner.class)
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ComponentScan("com.yiban.spring.boot.dev.jpa")
@EnableJpaRepositories
public class JpaDemoApplicationTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    public void testInsert() throws Exception {
//        Product product = new Product();
//        product.setId(22L);
//        product.setProductName("test");
//        product.setTypeName("test");
//        product.setSaleCount(19);

        User user = new User("dw",36);

        userRepository.save(user);
    }


//    public void testSelect() throws Exception
//    {
//        Optional<User> obj = userRepository.findById(1L);
//
//        User user = obj.get();
//        System.out.println("testSelect():" + user.getUserName() + "," + user.getRemark());
//    }
//
//    // JPA 自定义查询
////    @Test
//    public void testSelectCustom() throws Exception
//    {
//        User user = userRepository.findByUserName("a01");
//
//        System.out.println("testSelect():" + user.getUserName() + "," + user.getRemark());
//    }
//
//    // JPA Update
////    @Test
//    public void testUpdate() throws Exception
//    {
//        User userUpdate = userRepository.findByUserName("a01");
//        userUpdate.setRemark("rmkA01_XXX");
//        userRepository.save(userUpdate);
//    }
//
//    // JPA Delete
////    @Test
//    public void testDelete() throws Exception
//    {
//        userRepository.delete(userRepository.findByUserName("a01"));
//    }
//
//    // MyBatis Insert
////    @Test
//    public void testMyBatisInsert() throws Exception
//    {
//        int affectedRows = userRepository.myBatisUpdateSQL("insert into User(pass_word, reg_time, remark, sex, user_name) values ('123123', current_timestamp, 'remark_a100', 0, 'A100')");
//        System.out.println(affectedRows);
//    }
//
//    // MyBatis Select
////    @Test
//    public void testSelectSQL() throws Exception
//    {
//        List<LinkedHashMap<String,Object>> items = userRepository.myBatisSelectSQL("SELECT * FROM User");
//        for (LinkedHashMap<String, Object> hashMap : items)
//        {
//            for (String key : hashMap.keySet())
//            {
//                System.out.println(key + ":" + hashMap.get(key));
//            }
//            System.out.println("--------------");
//        }
//    }
//
//    // MyBatis Update
////    @Test
//    public void testMyBatisUpdate() throws Exception
//    {
//        int affectedRows = userRepository.myBatisUpdateSQL("update User SET remark = 'remark_a100XXX' WHERE user_name = 'A100'");
//        System.out.println(affectedRows);
//    }
//
//    // MyBatis Delete
////    @Test
//    public void testMyBatisDelete() throws Exception
//    {
//        userRepository.myBatisUpdateSQL("delete from User WHERE user_name = 'A100'");
//    }
}