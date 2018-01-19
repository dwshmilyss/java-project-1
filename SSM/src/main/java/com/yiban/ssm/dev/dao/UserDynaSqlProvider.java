package com.yiban.ssm.dev.dao;

import com.yiban.ssm.dev.entity.User;
import org.apache.ibatis.jdbc.SQL;

import java.util.Map;

import static com.yiban.ssm.dev.utils.HrmConstants.USERTABLE;
import static org.apache.ibatis.jdbc.SelectBuilder.WHERE;

/**
 * @auther WEI.DUAN
 * @date 2018/1/11
 * @website http://blog.csdn.net/dwshmilyss
 */
public class UserDynaSqlProvider {
    //分页动态查询
    public String selectWhitParam() {
        return selectWhitParam();
    }

    //分页动态查询
    public String selectWhitParam(final Map<String, Object> params) {
        String sql=new SQL(){
            {
                SELECT("*");
                FROM(USERTABLE);
                if (params.get("user")!=null) {
                    User user=(User) params.get("user");
                    if (user.getUsername()!=null && !user.getUsername().equals("")) {
                        WHERE(" username LIKE CONCAT('%',#{user.username},'%') ");
                    }
                    if (user.getStatus()!=null && !user.getStatus().equals("")) {
                        WHERE(" status LIKE CONCAT('%',#{user.status},'%') ");
                    }
                }
            }
        }.toString();
        if (params.get("pageModel")!=null) {
            sql += " limit #{pageModel.firstLimitParam} , #{pageModel.pageSize} ";
        }
        return sql;
    }

    //动态查询总数量
    public String count(final Map<String, Object> params) {
        return new SQL(){
            {
                SELECT("*");
                FROM(USERTABLE);
                if (params.get("user") !=null) {
                    User user=(User) params.get("user");
                    if (user.getUsername()!=null && !user.getUsername().equals("")) {
                        WHERE(" username LIKE CONCAT('%',#{user.username},'%') ");
                    }
                    if (user.getStatus()!=null && !user.getStatus().equals("")) {
                        WHERE(" status LIKE CONCAT('%',#{user.status},'%') ");
                    }
                }
            }
        }.toString();
    }

    //动态插入
    public String inserUser(final User user) {
        return new SQL(){
            {
                INSERT_INTO(USERTABLE);
                if (user.getUsername()!=null && !user.getUsername().equals("")) {
                    VALUES("username", "#{username}");
                }
                if (user.getStatus()!=null && !user.getStatus().equals("")) {
                    VALUES("status", "#{status}");
                }
                if (user.getLoginname()!=null && !user.getLoginname().equals("")) {
                    VALUES("loginname", "#{loginname}");
                }
                if (user.getPassword()!=null && !user.getPassword().equals("")) {
                    VALUES("password", "#{password}");
                }
            }
        }.toString();
    }

    //动态更新
    public String updateUser(final User user) {
        return new SQL(){
            {
                UPDATE(USERTABLE);
                if (user.getUsername()!=null) {
                    SET(" username = #{username} ");
                }
                if (user.getLoginname()!=null) {
                    SET(" loginname = #{loginname} ");
                }
                if (user.getPassword()!=null) {
                    SET(" password = #{password} ");
                }
                if (user.getStatus()!=null) {
                    SET(" status = #{status} ");
                }
                if (user.getCreateDate()!=null) {
                    SET(" create_date = #{createDate} ");
                }
                WHERE(" id = #{id} ");
            }
        }.toString();
    }
}
