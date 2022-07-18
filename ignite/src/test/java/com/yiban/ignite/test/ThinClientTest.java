package com.yiban.ignite.test;

import com.yiban.ignite.dev.entity.Emp;
import com.yiban.ignite.dev.entity.Person;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.CacheWriteSynchronizationMode;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.client.*;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.ClientConfiguration;
import org.apache.ignite.kubernetes.configuration.KubernetesConnectionConfiguration;
import org.junit.jupiter.api.Test;

import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * @Description: TODO
 * @Author: David.duan
 * @Date: 2022/6/13
 **/
public class ThinClientTest {

    @Test
    public void testWithCDP2_k8s() {
        ClientConfiguration cfg = new ClientConfiguration();
        KubernetesConnectionConfiguration kcfg = new KubernetesConnectionConfiguration();
        kcfg.setNamespace("devtest");
        kcfg.setServiceName("ignite");
        cfg.setAddressesFinder(new ThinClientKubernetesAddressFinder(kcfg));
        kcfg.setAccountToken(ThinClientTest.class.getClassLoader().getResource("token").getPath());
        cfg.setRetryPolicy(new ClientRetryAllPolicy());
        IgniteClient igniteClient = Ignition.startClient(cfg);
        ClientCache<String, String> cache = igniteClient.getOrCreateCache("test_cache");
        cache.put("dw", "123");
        assertThat(cache.get("dw"), is("123"));
    }

    @Test
    public void testWithJavaClient() {
        //瘦客户端
        ClientConfiguration clientConfiguration = new ClientConfiguration();
        clientConfiguration.setPartitionAwarenessEnabled(true);
        //在我们连接Ignite集群的时候，默认配置下，Ignite会监听端口号10800，等待thin client的连接
        clientConfiguration.setAddresses("127.0.0.1:10800");
        IgniteClient igniteClient = Ignition.startClient(clientConfiguration);
        ClientCache<String, String> cache = igniteClient.getOrCreateCache("test");
        cache.put("dw", "123");
        assertThat(cache.get("dw"), is("123"));
    }

    @Test
    public void testWithJavaClientWithConf() {
        //瘦客户端
        ClientConfiguration clientConfiguration = new ClientConfiguration();
        clientConfiguration.setPartitionAwarenessEnabled(true);
        //在我们连接Ignite集群的时候，用的端口号10800。默认配置下，Ignite会监听该端口，等待thin client的连接
        clientConfiguration.setAddresses("localhost:10800");
        // 缓存配置
        ClientCacheConfiguration cacheCfg = new ClientCacheConfiguration()
                .setName("test1")
                .setCacheMode(CacheMode.PARTITIONED)
                .setWriteSynchronizationMode(CacheWriteSynchronizationMode.PRIMARY_SYNC)
                .setOnheapCacheEnabled(false); // 堆内缓存是否开启

        try (IgniteClient igniteClient = Ignition.startClient(clientConfiguration)) {
            ClientCache<String, String> cache = igniteClient.getOrCreateCache(cacheCfg);
            cache.put("dw", "123");
            assertThat(cache.get("dw"), is("123"));
        } catch (ClientConnectionException ex) {
            throw ex;
        }
    }

    @org.junit.Test
    public void testObjectSelect() {
        ClientConfiguration clientConfiguration = new ClientConfiguration();
        clientConfiguration.setPartitionAwarenessEnabled(true);
        //在我们连接Ignite集群的时候，用的端口号10800。默认配置下，Ignite会监听该端口，等待thin client的连接
        clientConfiguration.setAddresses("127.0.0.1:10800");
        IgniteClient igniteClient = Ignition.startClient(clientConfiguration);
        ClientCacheConfiguration clientCacheConfiguration = new ClientCacheConfiguration();
        clientCacheConfiguration.setSqlSchema("public");
        clientCacheConfiguration.setName("emp");
        ClientCache<Long, Emp> cache = igniteClient.getOrCreateCache(clientCacheConfiguration);
        SqlFieldsQuery sql = new SqlFieldsQuery("select id,empNo,empName from emp");
        // Iterate over the result set.
        try (QueryCursor<List<?>> cursor = cache.query(sql)) {
            for (List<?> row : cursor)
                System.out.println("id=" + row.get(0)+",empNo = " + row.get(1)+",empName = " + row.get(2));
        }
    }
}
