package com.yiban.spring.core.test.bean;

import com.yiban.spring.spring_core.dev.cache.Account;
import com.yiban.spring.spring_core.dev.cache.AccountService;
import com.yiban.spring.spring_core.dev.cache.AccountService1;
import lombok.extern.log4j.Log4j2;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static org.junit.Assert.*;

/**
 * 测试类
 *
 * @auther WEI.DUAN
 * @date 2019/11/2
 * @website http://blog.csdn.net/dwshmilyss
 */
@Log4j2
public class AccountServiceTest {
    private AccountService accountService;
    private AccountService1 accountService1;

    @Before
    public void setUp() throws Exception {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        accountService = context.getBean("accountService", AccountService.class);
        accountService1 = context.getBean("accountService1", AccountService1.class);
    }

    @Test
    public void testInject() {
        assertNotNull(accountService);
        assertNotNull(accountService1);
    }

    @Test
    public void testGetAccountByName() throws Exception {

//        accountService.getAccountByName("dw");
//        accountService.getAccountByName("dw");
//        accountService.reload();
//        log.info("after reload.....");
//        accountService.getAccountByName("dw");
//        accountService.getAccountByName("dw");


        log.info("first query ... ");
        Account account = accountService1.getAccountByName("dw");
        log.info(account);
        log.info("second query ... ");
        account = accountService1.getAccountByName("dw");
        log.info(account);
    }
}