package com.linkflow.flink.dev.cep.demo1;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;

/**
 * @author david.duan
 * @packageName com.linkflow.flink.dev.cep.demo1
 * @className UserLoginLog
 * @date 2024/11/20
 * @description
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginLog {
    /**
     * 登陆id
     */
    private int loginId;
    /**
     * 登陆时间
     */
    private long loginTime;
    /**
     * 登陆状态 1--登陆失败 0--登陆成功
     */
    private int loginStatus;
    /**
     * 登陆用户名
     */
    private String userName;

}
