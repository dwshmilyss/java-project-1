package com.yiban.spring.spring_core.dev.cache;

import com.google.common.base.Optional;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * 带缓存注解的service
 *
 * @auther WEI.DUAN
 * @date 2019/11/2
 * @website http://blog.csdn.net/dwshmilyss
 */
@Service
@Log4j2
public class AccountService1 {
    // 使用了一个缓存名叫 accountCache
    @Cacheable(value = "accountCache")
    public Account getAccountByName(String accountName) {
        // 方法内部实现不考虑缓存逻辑，直接实现业务
        log.info("cache querying account... " + accountName);
        Optional<Account> accountOptional = getFromDB(accountName);
        if (!accountOptional.isPresent()) {
            throw new IllegalStateException(String.format("can not find account by account name : [%s]", accountName));
        }
        return accountOptional.get();
    }

    private Optional<Account> getFromDB(String accountName) {
        log.info("db querying account... " + accountName);
        //Todo query data from database
        return Optional.fromNullable(new Account(accountName));
    }

}