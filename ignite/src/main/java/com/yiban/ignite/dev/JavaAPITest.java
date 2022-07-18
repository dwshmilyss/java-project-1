package com.yiban.ignite.dev;

import com.yiban.ignite.dev.task.RemoteTask;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.lang.IgniteRunnable;
import org.apache.ignite.resources.IgniteInstanceResource;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder;

import java.util.Collections;

public class JavaAPITest {
    public static void main(String[] args) {
        // Preparing IgniteConfiguration using Java APIs
        IgniteConfiguration cfg = new IgniteConfiguration();

        // The node will be started as a client node.
        cfg.setClientMode(true);

        // Classes of custom Java logic will be transferred over the wire from this app.
//        cfg.setPeerClassLoadingEnabled(true);

        // Setting up an IP Finder to ensure the client can locate the servers.
        TcpDiscoveryMulticastIpFinder ipFinder = new TcpDiscoveryMulticastIpFinder();
        ipFinder.setAddresses(Collections.singletonList("127.0.0.1:47500..47509"));
        cfg.setDiscoverySpi(new TcpDiscoverySpi().setIpFinder(ipFinder));

        // Starting the node
        Ignite ignite = Ignition.start(cfg);

        // Create an IgniteCache and put some values in it.
        IgniteCache<Integer, String> cache = ignite.getOrCreateCache("myCache");
        cache.put(1, "Hello");
        cache.put(2, "World!");

        System.out.println(">> Created the cache and add the values.");

        // Executing custom Java compute task on server nodes.
//        ignite.compute(ignite.cluster().forServers()).broadcast(() -> { System.out.println("Hello World"); });

        System.out.println(">> Compute task is executed, check for output on the server nodes.");
        System.out.println("cache.get(1) = " + cache.get(1));

        // Disconnect from the cluster.
        ignite.close();
    }
}
