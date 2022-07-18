package com.yiban.zk.dev.lock;

public interface Lock {
    boolean lock() throws Exception;

    boolean unlock(Thread thread,String lockedPath);
}
