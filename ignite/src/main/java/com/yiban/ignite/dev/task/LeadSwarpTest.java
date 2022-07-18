package com.yiban.ignite.dev.task;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.client.ClientCache;
import org.apache.ignite.client.IgniteClient;
import org.apache.ignite.client.ThinClientKubernetesAddressFinder;
import org.apache.ignite.configuration.ClientConfiguration;
import org.apache.ignite.configuration.ClientConnectorConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.kubernetes.configuration.KubernetesConnectionConfiguration;
import org.apache.ignite.spi.communication.tcp.TcpCommunicationSpi;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.kubernetes.TcpDiscoveryKubernetesIpFinder;
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder;

import java.io.InputStream;
import java.util.Collections;

public class LeadSwarpTest {
    public static void main(String[] args) {
        for (int i = 0; i < 1000; i++) {
            test3();
        }
    }

    public static void test3() {
        System.out.println("test1...");
        KubernetesConnectionConfiguration kcfg = new KubernetesConnectionConfiguration();
        kcfg.setNamespace("leadswarp");
        kcfg.setServiceName("ignite");
        TcpDiscoveryKubernetesIpFinder tcpDiscoveryKubernetesIpFinder = new TcpDiscoveryKubernetesIpFinder(kcfg);
        IgniteConfiguration cfg = new IgniteConfiguration();
        cfg.setDiscoverySpi(new TcpDiscoverySpi().setIpFinder(tcpDiscoveryKubernetesIpFinder));
        System.out.println("prepare ...");
        cfg.setClientMode(true);
        Ignite client = Ignition.start(cfg);
        System.out.println("start ...");
        IgniteCache<Integer, String> cache = client.getOrCreateCache("test_cache");
        cache.put(1, "first test value");
        System.out.println("cache.get(1) = " + cache.get(1));
        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            System.out.println("close ...");
            client.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void test1() {
        System.out.println("test1...");
        KubernetesConnectionConfiguration kcfg = new KubernetesConnectionConfiguration();
        kcfg.setNamespace("leadswarp");
        kcfg.setServiceName("ignite");
//        TcpDiscoveryKubernetesIpFinder tcpDiscoveryKubernetesIpFinder = new TcpDiscoveryKubernetesIpFinder(cfg);
        ClientConfiguration ccfg = new ClientConfiguration();
//        ccfg.setAddressesFinder(new ThinClientKubernetesAddressFinder(kcfg));
        System.out.println("prepare ...");
        IgniteClient client = Ignition.startClient(ccfg);
        System.out.println("start ...");
        ClientCache<Integer, String> cache = client.getOrCreateCache("test_cache");
        cache.put(1, "first test value");
        System.out.println("cache.get(1) = " + cache.get(1));
        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            System.out.println("close ...");
            client.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void test2() {
        InputStream is = LeadSwarpTest.class.getClassLoader().getResourceAsStream("node-configuration.xml");
//        InputStream is = LeadSwarpTest.class.getClassLoader().getResourceAsStream("weijiqun-config-2.xml");
//        String cluserConfigPath = LeadSwarpTest.class.getClassLoader().getResource("weijiqun-config-2.xml").getPath();
//        System.out.println("cluserConfigPath = " + cluserConfigPath);
        Ignite ignite = Ignition.start(is);
        IgniteCache<Integer, String> cache = ignite.getOrCreateCache("myCache");
        cache.put(1, "Hello");
        cache.put(2, "World!");
        System.out.println("cache.get(1) = " + cache.get(1));
        System.out.println("cache.get(2) = " + cache.get(2));
        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ignite.close();
    }
}
