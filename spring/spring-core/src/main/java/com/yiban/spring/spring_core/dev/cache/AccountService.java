package com.yiban.spring.spring_core.dev.cache;

import com.google.common.base.Optional;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

/**
 * service
 *
 * @auther WEI.DUAN
 * @date 2019/11/1
 * @website http://blog.csdn.net/dwshmilyss
 */
@Service
@Log4j2
public class AccountService {
    private CacheContext<Account> accountCacheContext = new CacheContext<>();

    public Account getAccountByName(String accountName) {
        Account account = accountCacheContext.getKey(accountName);
        if (account != null) {
            log.info(String.format("get from cache... {%s}", accountName));
            return account;
        }
        Optional<Account> accountOptional = getFromDB(accountName);
        //如果没有在DB中找到
        if (!accountOptional.isPresent()) {
            throw new IllegalStateException(String.format("can not find account by account name : [%s]", accountName));
        }
        account = accountOptional.get();
        accountCacheContext.addOrUpdateKey(accountName,account);
        return account;
    }

    private Optional<Account> getFromDB(String accoutName) {
        log.info("query from DB ... " + accoutName);
        return Optional.fromNullable(new Account(accoutName));
    }

    public void reload() {
        accountCacheContext.evictCache();
    }
}