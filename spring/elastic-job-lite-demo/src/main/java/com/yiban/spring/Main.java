package com.yiban.spring;

import org.apache.shardingsphere.elasticjob.lite.api.bootstrap.impl.ScheduleJobBootstrap;
import org.apache.shardingsphere.elasticjob.api.JobConfiguration;
import org.apache.shardingsphere.elasticjob.reg.zookeeper.ZookeeperConfiguration;
import org.apache.shardingsphere.elasticjob.reg.zookeeper.ZookeeperRegistryCenter;

import javax.sql.DataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class Main {
    //这个好像不需要
//    private static DataSource getDataSource() {
//        HikariConfig config = new HikariConfig();
//        config.setJdbcUrl("jdbc:mysql://localhost:3306/elasticjob");
//        config.setUsername("root");
//        config.setPassword("120653");
//        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
//        return new HikariDataSource(config);
//    }

    public static void main(String[] args) {
        // 创建注册中心配置
        ZookeeperConfiguration zkConfig = new ZookeeperConfiguration("localhost:2181", "elastic-job-demo");
        ZookeeperRegistryCenter regCenter = new ZookeeperRegistryCenter(zkConfig);
        regCenter.init();

        // 定义作业配置
        JobConfiguration jobConfig = JobConfiguration.newBuilder("myShardingJob", 3)
                .cron("0/10 * * * * ?")  // 每10秒执行一次
                .shardingItemParameters("0=A,1=B,2=C")  // 配置分片参数
                .jobParameter("My job parameter")  // 传递的作业参数
                .build();

        // 配置作业调度
        new ScheduleJobBootstrap(regCenter, new MyShardingJob(), jobConfig).schedule();
    }
}