package com.yiban.ssm.dev.dao;

import com.yiban.ssm.dev.entity.User;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;
import java.util.Map;

import static com.yiban.ssm.dev.utils.HrmConstants.USERTABLE;

/**
 * @Select注解，这个不用解释了吧，就相当于存放查询语句的一个注解，定义在某一个方法上，效果相当于在配置文件里面编写查询语句。
 * @SelectProvider注解，用于生成查询用的sql语句，有别于@Select注解。结构上看，@SelectProvide指定了一个Class及其方法，通过调用Class上的这个方法来获得sql语句。
 */
public interface UserDao {
    /**
     * @param loginname
     * @param password
     * @return
     */
    //登录用户名和密码查询员工
    @Select("select * from "+USERTABLE+" where loginname = #{loginname} and password = #{password} ")
    User selectByLoginnmameAndPassword(
            @Param("loginname") String loginname,
            @Param("password") String password
    );

    //根据Id查询用户
    @Select("select * from "+USERTABLE+" where id = #{id} ")
    User selectById(Integer id);

    //根据Id删除用户
    @Delete("delete from "+USERTABLE+" where id = #{id} ")
    void deleteById(Integer id);

    //动态修改用户
    @SelectProvider(method = "updateUser", type = UserDynaSqlProvider.class)
    void update(User user);

    //动态查询
    @SelectProvider(method = "selectWhitParam", type = UserDynaSqlProvider.class)
    List<User> selectByPage(Map<String, Object> params);

    //根据参数查询用户总数
    @SelectProvider(method = "count", type = UserDynaSqlProvider.class)
    Integer count(Map<String, Object> params);

    //动态插入用户
    @SelectProvider(method = "inserUser", type = UserDynaSqlProvider.class)
    void save(User user);
}
