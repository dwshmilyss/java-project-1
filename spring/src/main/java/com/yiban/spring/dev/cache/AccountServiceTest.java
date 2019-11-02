package com.yiban.spring.dev.cache;

import lombok.extern.log4j.Log4j;
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
@Log4j
public class AccountServiceTest {
    private AccountService accountService;

    @Before
    public void setUp() throws Exception {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        accountService = context.getBean("accountService", AccountService.class);
    }

    @Test
    public void testInject() {
        assertNotNull(accountService);
    }

    @Test
    public void testGetAccountByName() throws Exception {
        accountService.getAccountByName("dw");
        accountService.getAccountByName("dw");
        accountService.reload();
        log.info("after reload.....");
        accountService.getAccountByName("dw");
        accountService.getAccountByName("dw");
    }
}