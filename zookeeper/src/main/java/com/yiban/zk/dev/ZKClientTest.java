package com.yiban.zk.dev;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.Stat;

import java.util.List;

/**
 * Created by duanwei on 2017/3/20.
 */
public class ZKClientTest {
    public static void main(String[] args) {
        // 定义父子类节点路径
        String rootPath = "/TestZookeeper";
        String child1Path = rootPath + "/hello1";
        String child2Path = rootPath + "/word1";

        //ZKOperate操作API 封装了zk的一些基本操作
        ZKOperate zkOperate = ZKOperate.getInstance();

        // 连接zk服务器
        MyZooKeeper zooKeeper = new MyZooKeeper();
        zooKeeper.connect("10.21.3.77:2181",30000);
        //自定义的创建节点方法
//        System.out.println(zkOperate.createZNode("/aa", "test for data"));
        try {
            //zookeeper原生的创建节点方法
//            MyZooKeeper.zooKeeper.create("/aa", new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
//            MyZooKeeper.zooKeeper.create("/aa/loc1", String.valueOf(10).getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
//            MyZooKeeper.zooKeeper.create("/aa/loc2", String.valueOf(11).getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            MyZooKeeper.zooKeeper.create("/aa/loc3", String.valueOf(12).getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT_SEQUENTIAL);

//            byte[] bytes1 = MyZooKeeper.zooKeeper.getData("/aa/loc2", false, new Stat());
//            byte[] bytes2 = MyZooKeeper.zooKeeper.getData("/aa/loc10000000000", false, new Stat());
//            System.out.println("bytes1 = " + new String(bytes1));
//            System.out.println("bytes2 = " + new String(bytes2));
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        // 创建节点数据
//        if ( zkWatchAPI.createZNode(rootPath, "<父>节点数据" ) ) {
//            System.out.println( "节点[" + rootPath + "]数据内容[" + zkWatchAPI.readData( rootPath ) + "]" );
//        }
//        // 创建子节点, 读取 + 删除
//        if ( zkWatchAPI.createZNode( child1Path, "<父-子(1)>节点数据" ) ) {
//            System.out.println( "节点[" + child1Path + "]数据内容[" + zkWatchAPI.readData( child1Path ) + "]" );
//            zkWatchAPI.deteleZNode(child1Path,-1);
//            System.out.println( "节点[" + child1Path + "]删除值后[" + zkWatchAPI.readData( child1Path ) + "]" );
//        }
//
//        // 创建子节点, 读取 + 修改
//        if ( zkWatchAPI.createZNode(child2Path, "<父-子(2)>节点数据" ) ) {
//            System.out.println( "节点[" + child2Path + "]数据内容[" + zkWatchAPI.readData( child2Path ) + "]" );
//            zkWatchAPI.updateZNodeData(child2Path, "<父-子(2)>节点数据,更新后的数据" ,-1);
//            System.out.println( "节点[" + child2Path+ "]数据内容更新后[" + zkWatchAPI.readData( child2Path ) + "]" );
//        }
//
//        // 获取子节点
//        List<String> childPaths = zkWatchAPI.getChild(rootPath);
//        if(null != childPaths){
//            System.out.println( "节点[" + rootPath + "]下的子节点数[" + childPaths.size() + "]" );
//            for(String childPath : childPaths){
//                System.out.println(" |--节点名[" +  childPath +  "]");
//            }
//        }
//        // 判断节点是否存在
//        System.out.println( "检测节点[" + rootPath + "]是否存在:" + zkWatchAPI.isExists(rootPath)  );
//        System.out.println( "检测节点[" + child1Path + "]是否存在:" + zkWatchAPI.isExists(child1Path)  );
//        System.out.println( "检测节点[" + child2Path + "]是否存在:" + zkWatchAPI.isExists(child2Path)  );


        zooKeeper.close();
    }
}
