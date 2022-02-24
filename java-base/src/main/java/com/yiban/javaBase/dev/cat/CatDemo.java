package com.yiban.javaBase.dev.cat;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;

public class CatDemo {
    public static void main(String[] args) {
        Transaction transaction = Cat.newTransaction("ShopService", "Service3");
        try {

            transaction.setStatus(Transaction.SUCCESS);
        } catch (Exception e) {
            transaction.setStatus(e); // catch 到异常，设置状态，代表此请求失败
            Cat.logError(e); // 将异常上报到cat上
            // 也可以选择向上抛出： throw e;
        } finally {
            transaction.complete();
        }
    }
}
