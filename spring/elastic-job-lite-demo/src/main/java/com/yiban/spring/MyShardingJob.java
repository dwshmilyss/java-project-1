package com.yiban.spring;

import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.simple.job.SimpleJob;

/**
 * @author david.duan
 * @packageName com.yiban.spring
 * @className MyShardingJob
 * @date 2024/10/30
 * @description
 */
public class MyShardingJob implements SimpleJob {
    @Override
    public void execute(ShardingContext shardingContext) {
        // 获取当前分片项
        int shardingItem = shardingContext.getShardingItem();
        String jobParameter = shardingContext.getJobParameter();

        // 根据分片项处理不同逻辑
        switch (shardingItem) {
            case 0:
                System.out.println("处理分片 0 的数据, 参数: " + jobParameter);
                // 实现分片 0 的任务逻辑
                break;
            case 1:
                System.out.println("处理分片 1 的数据, 参数: " + jobParameter);
                // 实现分片 1 的任务逻辑
                break;
            case 2:
                System.out.println("处理分片 2 的数据, 参数: " + jobParameter);
                // 实现分片 2 的任务逻辑
                break;
            default:
                System.out.println("没有分配到的分片: " + shardingItem);
        }
    }
}
