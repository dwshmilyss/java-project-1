package com.yiban.ignite.dev;

import org.apache.ignite.Ignition;
import org.apache.ignite.client.ClientCache;
import org.apache.ignite.client.ClientRetryAllPolicy;
import org.apache.ignite.client.IgniteClient;
import org.apache.ignite.client.ThinClientKubernetesAddressFinder;
import org.apache.ignite.configuration.ClientConfiguration;
import org.apache.ignite.kubernetes.configuration.KubernetesConnectionConfiguration;

/**
 * @Description: TODO
 * @Author: David.duan
 * @Date: 2022/6/13
 **/
public class ThinClientDemo {
    public static void main(String[] args) {
        ClientConfiguration cfg = new ClientConfiguration();
        KubernetesConnectionConfiguration kcfg = new KubernetesConnectionConfiguration();
        kcfg.setNamespace("devtest");
        kcfg.setServiceName("ignite");
        cfg.setAddressesFinder(new ThinClientKubernetesAddressFinder(kcfg));
        cfg.setRetryPolicy(new ClientRetryAllPolicy());
        IgniteClient igniteClient = Ignition.startClient(cfg);
        ClientCache<String, String> cache=igniteClient.getOrCreateCache("test_cache");
        cache.put("dw", "123");
        System.out.println("value = " + cache.get("dw"));
    }
}
