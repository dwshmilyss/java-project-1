package com.yiban.ignite.test;

import com.yiban.ignite.dev.entity.Emp;
import com.yiban.ignite.dev.entity.Person;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.client.ClientCacheConfiguration;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.ClientConnectorConfiguration;
import org.apache.ignite.configuration.DeploymentMode;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.events.EventType;
import org.apache.ignite.spi.communication.tcp.TcpCommunicationSpi;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * @Description: TODO
 * @Author: David.duan
 * @Date: 2022/6/16
 **/
public class ThickClientTest {
    @Test
    public void testWithCDP2_k8s() {

    }

    @Test
    public void test() {
        IgniteConfiguration igniteConfiguration = new IgniteConfiguration();
        igniteConfiguration.setClientMode(true);
        //配置集群发现
        igniteConfiguration.setDiscoverySpi(new TcpDiscoverySpi().setLocalPort(47500).setLocalPortRange(10)
                .setIpFinder(new TcpDiscoveryVmIpFinder().setAddresses(Arrays.asList("localhost:47500..47509"))));
        //基本配置
        igniteConfiguration.setCommunicationSpi(new TcpCommunicationSpi().setLocalPort(47100));
        igniteConfiguration.setDeploymentMode(DeploymentMode.SHARED);
        igniteConfiguration.setPeerClassLoadingLocalClassPathExclude("com.org.ignite.*");
        igniteConfiguration.setIncludeEventTypes(EventType.EVT_TASK_STARTED, EventType.EVT_TASK_FINISHED, EventType.EVT_TASK_FAILED);
        igniteConfiguration.setPublicThreadPoolSize(64);
        igniteConfiguration.setSystemThreadPoolSize(32);
        //添加cache配置
        List<CacheConfiguration> cacheConf = new ArrayList<>();
        CacheConfiguration<String, String> conf = new CacheConfiguration<String, String>("test*")
                .setCacheMode(CacheMode.REPLICATED)
                .setIndexedTypes(String.class, Integer.class)
                .setAtomicityMode(CacheAtomicityMode.ATOMIC)
                .setCopyOnRead(false)
                .setBackups(1);
        cacheConf.add(conf);
        igniteConfiguration.setCacheConfiguration(cacheConf.toArray(new CacheConfiguration[]{}));
        Ignite ignite = Ignition.start(igniteConfiguration);

        IgniteCache<String, String> cache = ignite.getOrCreateCache("t1");
        cache.put("dw", "123");
        assertThat(cache.get("dw"), is("123"));
    }

    @Test
    public void testStaticIpFinder() {
        IgniteConfiguration cfg = new IgniteConfiguration();
        cfg.setClientMode(true);
        cfg.setIgniteInstanceName("thick clinet");
        TcpDiscoveryVmIpFinder ipFinder = new TcpDiscoveryVmIpFinder();
        ipFinder.setAddresses(Collections.singletonList("localhost:47500..47509"));
        cfg.setDiscoverySpi(new TcpDiscoverySpi().setLocalPort(47500).setLocalPortRange(10).setIpFinder(ipFinder));
        Ignite ignite = Ignition.start(cfg);
        IgniteCache<String, String> cache = ignite.getOrCreateCache("test");
        try {
            Thread.sleep(600 * 1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        cache.put("dw", "123");
        assertThat(cache.get("dw"), is("123"));
    }

    @Test
    public void testMulticastIpFinder() {
        CacheConfiguration<String, String> cacheConfiguration = new CacheConfiguration<>();
        cacheConfiguration.setCacheMode(CacheMode.PARTITIONED);
        cacheConfiguration.setBackups(1);
        cacheConfiguration.setName("test");
        cacheConfiguration.setOnheapCacheEnabled(true);

        IgniteConfiguration cfg = new IgniteConfiguration();
        cfg.setClientMode(true);

        TcpDiscoveryMulticastIpFinder ipFinder = new TcpDiscoveryMulticastIpFinder();
        ipFinder.setMulticastGroup("228.10.10.157");
        //不知道为什么只有组播的情况下还得配置一个IP list 才能连上，用./ignitevisorcmd.sh的时候选择配置文件也需要这样配置
        ipFinder.setAddresses(Arrays.asList("localhost:47500..47509"));

        TcpDiscoverySpi spi = new TcpDiscoverySpi();
//        spi.setLocalPort(47500); // 这是默认值，不需要配置
//        spi.setLocalPortRange(10);//这里默认是100
        spi.setIpFinder(ipFinder);
        cfg.setDiscoverySpi(spi);
        //java      30519  edz  241u  IPv6 0xb3101a5707878ce1      0t0  TCP *:47101 (LISTEN)
        //java      30519  edz  269u  IPv6 0xb3101a57078793e1      0t0  TCP *:10801 (LISTEN)
        //这里会启动这2个端口
        TcpCommunicationSpi commSpi = new TcpCommunicationSpi();
        commSpi.setLocalPort(47200);
        cfg.setDiscoverySpi(spi);
        cfg.setCommunicationSpi(commSpi);
        try (Ignite ignite = Ignition.start(cfg)) {
            ignite.addCacheConfiguration(cacheConfiguration);
            IgniteCache<String, String> cache = ignite.getOrCreateCache(cacheConfiguration);
//            IgniteCache<String, String> cache = ignite.getOrCreateCache("test");
            try {
                Thread.sleep(600 * 1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            cache.put("dw", "123");
            assertThat(cache.get("dw"), is("123"));
        }
    }


    @Test
    public void testCreateTable() {
        IgniteConfiguration cfg = new IgniteConfiguration();
        cfg.setClientMode(true);
        cfg.setIgniteInstanceName("thick clinet");
        TcpDiscoveryMulticastIpFinder ipFinder = new TcpDiscoveryMulticastIpFinder();
        ipFinder.setAddresses(Collections.singletonList("localhost:47500..47509"));
        cfg.setDiscoverySpi(new TcpDiscoverySpi().setLocalPort(47500).setLocalPortRange(10).setIpFinder(ipFinder));
        Ignite ignite = Ignition.start(cfg);
        CacheConfiguration<Long, Emp> empCacheCfg = new CacheConfiguration<Long, Emp>();
        empCacheCfg.setName("emp");
        empCacheCfg.setSqlSchema("public");
        empCacheCfg.setIndexedTypes(Long.class, Emp.class);
        IgniteCache<Long, Emp> cache = ignite.getOrCreateCache(empCacheCfg);
        cache.put(1L, new Emp(1, 1, "dw"));
        cache.put(2L, new Emp(2, 2, "dww"));
        cache.put(3L, new Emp(3, 3, "dwww"));
        assertThat(cache.get(1L).getEmpName(), Matchers.is("dw"));
    }
}
