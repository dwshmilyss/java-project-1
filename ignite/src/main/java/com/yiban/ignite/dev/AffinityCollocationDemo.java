package com.yiban.ignite.dev;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.affinity.AffinityKeyMapped;
import org.apache.ignite.configuration.CacheConfiguration;

/**
 * @Description: TODO
 * @Author: David.duan
 * @Date: 2022/6/24
 **/
public class AffinityCollocationDemo {
    static class Person {
        private int id;
        private String companyId;
        private String name;

        public Person(int id, String companyId, String name) {
            this.id = id;
            this.companyId = companyId;
            this.name = name;
        }

        public int getId() {
            return id;
        }
    }

    static class PersonKey {
        private int id;

        @AffinityKeyMapped
        private String companyId;

        public PersonKey(int id, String companyId) {
            this.id = id;
            this.companyId = companyId;
        }
    }

    static class Company {
        private String id;
        private String name;

        public Company(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() {
            return id;
        }
    }

    public void configureAffinityKeyWithAnnotation() {
        CacheConfiguration<PersonKey, Person> personCfg = new CacheConfiguration<PersonKey, Person>("persons");
        personCfg.setBackups(1);

        CacheConfiguration<String, Company> companyCfg = new CacheConfiguration<>("companies");
        companyCfg.setBackups(1);

        try (Ignite ignite = Ignition.start()) {
            IgniteCache<PersonKey, Person> personCache = ignite.getOrCreateCache(personCfg);
            IgniteCache<String, Company> companyCache = ignite.getOrCreateCache(companyCfg);

            Company c1 = new Company("company1", "My company");
            Person p1 = new Person(1, c1.getId(), "John");

            // Both the p1 and c1 objects will be cached on the same node
            personCache.put(new PersonKey(p1.getId(), c1.getId()), p1);
            companyCache.put("company1", c1);

            // Get the person object
            p1 = personCache.get(new PersonKey(1, "company1"));
            System.out.println("p1.name = " + p1.name + ",p1.companyId = " + p1.companyId);
        }
    }

    public static void main(String[] args) {
        AffinityCollocationDemo affinityCollocationDemo = new AffinityCollocationDemo();
        affinityCollocationDemo.configureAffinityKeyWithAnnotation();
    }
}
