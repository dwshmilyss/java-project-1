package com.yiban.ignite.test;

import com.yiban.ignite.dev.entity.Emp;
import com.yiban.ignite.dev.entity.Person;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.client.ClientCache;
import org.apache.ignite.client.IgniteClient;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.ClientConnectorConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.communication.tcp.TcpCommunicationSpi;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.StopWatch;

import java.sql.*;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class JDBCTest1 {

    public Connection conn;
    public PreparedStatement pstmt;
    public String url = "jdbc:ignite:thin://localhost:10800,localhost:10900;partitionAwareness=true";
//    public String url = "jdbc:ignite:thin://ignite;partitionAwareness=true";
    public Ignite ignite;
    public IgniteClient igniteClient;

    @Before
    public void init() {
        try {
            Class.forName("org.apache.ignite.IgniteJdbcThinDriver");
            conn = DriverManager.getConnection(url);

//            //胖客户端(针对单节点可以，但是集群不行)
//            IgniteConfiguration cfg = new IgniteConfiguration();
//            cfg.setClientMode(true);
//            cfg.setPeerClassLoadingEnabled(true);
//            //下面两者二选一即可，即组播和静态IP发现都可以的
//            TcpDiscoveryMulticastIpFinder ipFinder = new TcpDiscoveryMulticastIpFinder();
////            TcpDiscoveryVmIpFinder ipFinder = new TcpDiscoveryVmIpFinder();
//            ipFinder.setAddresses(Collections.singletonList("localhost:47500..47509"));
//            cfg.setDiscoverySpi(new TcpDiscoverySpi().setLocalPort(47500).setLocalPortRange(10).setIpFinder(ipFinder));
//            // 不可以和服务端的节点冲突
//            cfg.setCommunicationSpi(new TcpCommunicationSpi().setLocalPort(47100));
//            cfg.setClientConnectorConfiguration(new ClientConnectorConfiguration().setPort(10700));
//            ignite = Ignition.start(cfg);

//            //胖客户端(集群版，但是本地伪集群测试失败)
//            String cluserConfigPath = JDBCTest.class.getClassLoader().getResource("cluster-config.xml").getPath();
//            System.out.println("cluserConfigPath = " + cluserConfigPath);
//            ignite = Ignition.start(cluserConfigPath);

//            //瘦客户端
//            ClientConfiguration clientConfiguration = new ClientConfiguration();
//            clientConfiguration.setPartitionAwarenessEnabled(true);
//            clientConfiguration.setAddresses("localhost:10800");
//            igniteClient = Ignition.startClient(clientConfiguration);

            //加配置的胖客户端(只有这种写法不会报错)
//            IgniteConfiguration igniteConfiguration = new IgniteConfiguration();
//            igniteConfiguration.setClientMode(true);
//            //配置集群发现
//            igniteConfiguration.setDiscoverySpi(new TcpDiscoverySpi().setLocalPort(47500).setLocalPortRange(10)
//                    .setIpFinder(new TcpDiscoveryVmIpFinder().setAddresses(Arrays.asList("localhost:47500..47509"))));
//            //基本配置
//            igniteConfiguration.setCommunicationSpi(new TcpCommunicationSpi().setLocalPort(48100));
//            igniteConfiguration.setDeploymentMode(DeploymentMode.SHARED);
//            igniteConfiguration.setPeerClassLoadingEnabled(true);
//            igniteConfiguration.setPeerClassLoadingLocalClassPathExclude("com.org.ignite.*");
//            igniteConfiguration.setIncludeEventTypes(EventType.EVT_TASK_STARTED, EventType.EVT_TASK_FINISHED, EventType.EVT_TASK_FAILED);
//            igniteConfiguration.setPublicThreadPoolSize(64);
//            igniteConfiguration.setSystemThreadPoolSize(32);
//            //添加cache配置
//            List<CacheConfiguration> cacheConf = new ArrayList<>();
//            CacheConfiguration<String, Integer> conf = new CacheConfiguration<String, Integer>("test")
//                    .setCacheMode(CacheMode.REPLICATED)
//                    .setIndexedTypes(String.class, Integer.class)
//                    .setAtomicityMode(CacheAtomicityMode.ATOMIC)
//                    .setCopyOnRead(false)
//                    .setBackups(1);
//            cacheConf.add(conf);
//            igniteConfiguration.setCacheConfiguration(cacheConf.toArray(new CacheConfiguration[]{}));
//            ignite = Ignition.start(igniteConfiguration);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test(){
        System.out.println(new Random().nextInt(3));
    }

    @Test
    public void testThinClient() {
//        igniteClient.destroyCache("people");

        ClientCache<Long,Person> cache = igniteClient.getOrCreateCache("people");
        cache.getConfiguration().setSqlSchema("public");
        cache.put(1L,new Person(1L,"aaa"));
        System.out.println(cache.get(1L).toString());
    }

    @Test
    public void testObjectFirstInsert() {
        CacheConfiguration<Long, Person> personCacheCfg = new CacheConfiguration<Long, Person>();
        personCacheCfg.setName("people");
        personCacheCfg.setSqlSchema("public");
        personCacheCfg.setIndexedTypes(Long.class, Person.class);
        IgniteCache<Long, Person> cache = ignite.createCache(personCacheCfg);
        cache.put(1L,new Person(1L,"dww"));
    }

    @Test
    public void testInsertEmp() {
        IgniteCache<Long, Emp> cache = ignite.cache("SQL_PUBLIC_EMP1");
        cache.put(3L,new Emp(3L,3003,"ccc"));
    }

    @Test
    public void testObjectInsertWithFatClient() {
        IgniteCache<Long, Person> cache = ignite.getOrCreateCache("people");
        cache.put(2L,new Person(2L,"zhangsan"));
        System.out.println("cache.get(2L) = " + cache.get(2L));
    }

    @Test
    public void testGetFromSql() {
//        ClientCache<Long, Emp> cache = igniteClient.cache("SQL_PUBLIC_EMP1");
//        System.out.println("cache.get(3L).getName() = " + cache.get(3L).getEname());
//        ClientCache<Long, Person> cache = igniteClient.cache("SQL_PUBLIC_EMP1");
//        System.out.println("cache.get(1L).getName() = " + cache.get(1L).getName());
//        ClientCache<Long, Emp> cache = igniteClient.cache("EMPcache");
//        System.out.println("cache.get(1L).getName() = " + cache.get(1L).getEname());
        IgniteCache<Long, Emp> cache = ignite.cache("EMPcache");
        System.out.println("cache.get(1L).getName() = " + cache.get(1L).getEname());

    }

    @Test
    public void testObjectSelect() {
        ClientCache<Long, Emp> cache = igniteClient.cache("aaa");
        SqlFieldsQuery sql = new SqlFieldsQuery("select id,empno,ename from emp1");
        // Iterate over the result set.
        try (QueryCursor<List<?>> cursor = cache.query(sql)) {
            for (List<?> row : cursor)
                System.out.println("id=" + row.get(0)+",empno = " + row.get(1));
        }
    }

    @Test
    public void testInsert() {
        try {
            String insertSql = "insert into emp (id,empno,ename) values(?,?,?);";
            for (long i = 1006; i <= 20000; i++) {
                pstmt = conn.prepareStatement(insertSql);
                pstmt.setLong(1,i);
                pstmt.setLong(2,i);
                pstmt.setString(3,"zhangsan"+i);
                int effectNum = pstmt.executeUpdate();
                System.out.println("effectNum = " + effectNum);
            }
//            conn.setAutoCommit(false);
//            conn.commit();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


    @Test
    public void testMergeIdentity() {
        String[] strings = {"anonumousId", "uuid", "externalId"};
        try {
//            conn.setAutoCommit(false);
            String insertSql = "insert into identity_test (id,type,contact_id,udc_channel_id,channel_id,identity_id,date_created) values(?,?,?,?,?,?,?);";
            int i = 8065401;
            for (; i <= 50000000; i++) {
                pstmt = conn.prepareStatement(insertSql);
                pstmt.setString(1, UUID.randomUUID().toString());
                pstmt.setString(2,strings[new Random().nextInt(3)]);
                pstmt.setLong(3,i);
                pstmt.setLong(4,i);
                pstmt.setLong(5,37L);
                pstmt.setLong(6,35821L);
                pstmt.setLong(7,1631615246362L);
//                pstmt.addBatch();
                int effectNum = pstmt.executeUpdate();
                System.out.println("effectNum = " + effectNum + ",total = " + i);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
//                if (i % 100 == 0) {
//                    pstmt.executeBatch();
//                    System.out.println("total = " + i);
//                    conn.commit();
//                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            try {
                pstmt.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testMerge() {
        try {
            String insertSql = "merge into emp (id,empno,ename) values(?,?,?);";
            for (long i = 5; i <= 20000; i++) {
                pstmt = conn.prepareStatement(insertSql);
                pstmt.setLong(1,i);
                pstmt.setLong(2,i);
                pstmt.setString(3,"zhangsan");
                int effectNum = pstmt.executeUpdate();
                System.out.println("effectNum = " + effectNum);
            }
//            conn.setAutoCommit(false);
//            conn.commit();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            try {
                pstmt.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testSelectById() {
        String selectSql = "select * from emp1;";
        try {
            pstmt = conn.prepareStatement(selectSql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                System.out.println("EMPNO = " + rs.getString("EMPNO") + ",ENAME = " + rs.getString("ENAME"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            try {
                pstmt.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testSelectByJDBC() {
        StopWatch watch = new StopWatch();
        watch.start();
        String selectSql = "select count(1) as cn from identity_test where type = 'anonumousId';";
        try {
            pstmt = conn.prepareStatement(selectSql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                System.out.println("count is = " + rs.getString("cn"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            try {
                pstmt.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        watch.stop();
        System.out.println(watch.getTotalTimeMillis());
    }

    @Test
    public void testByGrid() {
        StopWatch watch = new StopWatch();
        watch.start();

        watch.stop();
        System.out.println(watch.getTotalTimeMillis());
    }
}
