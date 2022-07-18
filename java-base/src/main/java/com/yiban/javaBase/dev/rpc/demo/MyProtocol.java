package com.yiban.javaBase.dev.rpc.demo;

import org.apache.hadoop.ipc.VersionedProtocol;

import java.io.IOException;

/**
 * 自定义的RPC协议
 */
public interface MyProtocol extends VersionedProtocol {
    public static final long versionID = 1L ;
    public String echo() throws IOException;
}
