package com.yiban.mybatis.dev.mapper;

import com.yiban.mybatis.dev.entity.Student;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @auther WEI.DUAN
 * @date 2019/3/26
 * @website http://blog.csdn.net/dwshmilyss
 * 如果是普通的java工程，则接口的名称要和xml的名称一致
 */
public interface StudentMapper {

    /**
     * 批量插入学生
     */
    int insertList(List<Student> students);

    /**
     * 根据输入的学生信息进行条件检索
     * 1. 当只输入用户名时， 使用用户名进行模糊检索；
     * 2. 当只输入邮箱时， 使用性别进行完全匹配
     * 3. 当用户名和性别都存在时， 用这两个条件进行查询匹配的用
     * 4. 当两者都不存在时，返回全部数据
     * @param student
     * @return
     */
    List<Student> selectByStudentSelective(Student student);


    /**
     * 1.当有ID时，根据ID检索
     * 2.当name不为空时，根据name模糊匹配
     * 3.当两者都存在时，用这两个条件进行查询匹配的用
     * 4.当两者都不存在时，返回空
     * @param student
     * @return
     */
    Student selectByIdOrName(Student student);

    Student selectByPrimaryKey(int id);

    /**
     * 获取一段时间内的用户
     * 不指定日期类型，可以传字符串，Date,Timestamp
     * @param params
     * @return
     */
    List<Student> selectBetweenCreatedTime(Map<String, Object> params);

    /**
     * 指定日期类型 只能传Date
     * @param bTime
     * @param eTime
     * @return
     */
    List<Student> selectBetweenCreatedTimeAnno(@Param("bTime")Date bTime,@Param("eTime")Date eTime);

    /**
     * 更新非空属性
     */
    int updateByPrimaryKeySelective(Student record);

    /**
     * 非空字段才进行插入
     */
    int insertSelective(Student record);


    /**
     * 根据输入的学生信息进行条件检索
     * 1. 当只输入用户名时， 使用用户名进行模糊检索；
     * 2. 当只输入邮箱时， 使用性别进行完全匹配
     * 3. 当用户名和性别都存在时， 用这两个条件进行查询匹配的用
     */
    List<Student> selectByStudentSelectiveWhereTag(Student student);


    /**
     * 获取 id 集合中的用户信息
     * @param ids
     * @return
     */
    List<Student> selectByStudentIdList(@Param("ids") List<Integer> ids);

}