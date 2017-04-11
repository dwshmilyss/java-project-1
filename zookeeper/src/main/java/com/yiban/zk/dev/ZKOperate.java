package main.java.com.yiban.zk.dev;

import org.apache.log4j.Logger;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

/**
 * Created by duanwei on 2017/3/20.
 */
public class ZKOperate {
    Logger logger = Logger.getLogger(ZKOperate.class);
    MyZooKeeper myZooKeeper = new MyZooKeeper();

    /**
     * <p>创建zNode节点, String create(path<节点路径>, data[]<节点内容>, List(ACL访问控制列表), CreateMode<zNode创建类型>) </p><br/>
     * <pre>
     *     节点创建类型(CreateMode)
     *     1、PERSISTENT:持久化节点
     *     2、PERSISTENT_SEQUENTIAL:顺序自动编号持久化节点，这种节点会根据当前已存在的节点数自动加 1
     *     3、EPHEMERAL:临时节点客户端,session超时这类节点就会被自动删除
     *     4、EPHEMERAL_SEQUENTIAL:临时自动编号节点
     * </pre>
     *
     * @param path zNode节点路径
     * @param data zNode数据内容
     * @return 创建成功返回true, 反之返回false.
     */
    public boolean createZNode(String path, String data) {
        try {
            String zkPath = MyZooKeeper.zooKeeper.create(path, data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            logger.info("ZooKeeper创建节点成功，节点地址：" + zkPath);
            return true;
        } catch (KeeperException e) {
            logger.error("创建节点失败：" + e.getMessage() + "，path:" + path, e);
        } catch (InterruptedException e) {
            logger.error("创建节点失败：" + e.getMessage() + "，path:" + path, e);
        }
        return false;
    }

    /**
     * <p>删除一个zMode节点, void delete(path<节点路径>, stat<数据版本号>)</p><br/>
     * <pre>
     *     说明
     *     1、版本号不一致,无法进行数据删除操作.
     *     2、如果版本号与znode的版本号不一致,将无法删除,是一种乐观加锁机制;如果将版本号设置为-1,不会去检测版本,直接删除.
     * </pre>
     *
     * @param path    zNode节点路径
     * @param version 版本号
     * @return 删除成功返回true, 反之返回false.
     */
    public boolean deteleZNode(String path, int version) {
        try {
            MyZooKeeper.zooKeeper.delete(path, version);
            logger.info("ZooKeeper删除节点成功，节点地址：" + path);
            return true;
        } catch (InterruptedException e) {
            logger.error("删除节点失败：" + e.getMessage() + "，path:" + path, e);
        } catch (KeeperException e) {
            logger.error("删除节点失败：" + e.getMessage() + "，path:" + path, e);
        }
        return false;
    }

    /**
     * <p>更新指定节点数据内容, setData(path<节点路径>, data[]<节点内容>, version<数据版本号>)</p>
     *
     * @param path    zNode节点路径
     * @param data    zNode数据内容
     * @param version 设置某个znode上的数据时如果为-1，跳过版本检查
     * @return 更新成功返回true, 返回返回false
     */
    public boolean updateZNodeData(String path, String data, int version) {
        try {
            Stat stat = MyZooKeeper.zooKeeper.setData(path, data.getBytes(), version);
            logger.info("更新数据成功, path：" + path + ", stat: " + stat);
            return true;
        } catch (KeeperException e) {
            logger.error("更新节点数据失败：" + e.getMessage() + "，path:" + path, e);
        } catch (InterruptedException e) {
            logger.error("更新节点数据失败：" + e.getMessage() + "，path:" + path, e);
        }
        return false;
    }

    /**
     * <p>读取指定节点数据内容,byte[] getData(path<节点路径>, watcher<监视器>, stat<数据版本号>)</p>
     *
     * @param path zNode节点路径
     * @return 节点存储的值, 有值返回, 无值返回null
     */
    public String readData(String path) {
        String data = null;
        //stat存储状态信息，因为java的返回值只能有一个，所以要传一个对象进去，以便获取数据后添加到对象中
        Stat stat = new Stat();
        try {
            data = new String(MyZooKeeper.zooKeeper.getData(path, false, stat));
            logger.info("读取数据成功, path:" + path + ", content:" + data);
        } catch (KeeperException e) {
            logger.error("读取数据失败,发生KeeperException! path: " + path
                    + ", errMsg:" + e.getMessage(), e);
        } catch (InterruptedException e) {
            logger.error("读取数据失败,发生InterruptedException! path: " + path
                    + ", errMsg:" + e.getMessage(), e);
        }
        return data;
    }

    /**
     * <p>获取某个节点下的所有子节点,List getChildren(path<节点路径>, watcher<监视器>)该方法有多个重载</p>
     *
     * @param path zNode节点路径
     * @return 子节点路径集合 说明,这里返回的值为节点名
     * <pre>
     *     eg.
     *     /node
     *     /node/child1
     *     /node/child2
     *     getChild( "node" )户的集合中的值为["child1","child2"]
     * </pre>
     * @throws KeeperException
     * @throws InterruptedException
     */
    public List<String> getChild(String path) {
        try {
            List<String> list = MyZooKeeper.zooKeeper.getChildren(path, false);
            if (list.isEmpty()) {
                logger.info("中没有节点" + path);
            }
            return list;
        } catch (KeeperException e) {
            logger.error("读取子节点数据失败,发生KeeperException! path: " + path
                    + ", errMsg:" + e.getMessage(), e);
        } catch (InterruptedException e) {
            logger.error("读取子节点数据失败,发生InterruptedException! path: " + path
                    + ", errMsg:" + e.getMessage(), e);
        }
        return null;
    }

    /**
     * <p>判断某个zNode节点是否存在, Stat exists(path<节点路径>, watch<并设置是否监控这个目录节点，这里的 watcher 是在创建 ZooKeeper 实例时指定的 watcher>)</p>
     *
     * @param path zNode节点路径
     * @return 存在返回true, 反之返回false
     */
    public boolean isExists(String path) {
        try {
            Stat stat = MyZooKeeper.zooKeeper.exists(path, false);
            return null != stat;
        } catch (KeeperException e) {
            logger.error("读取数据失败,发生KeeperException! path: " + path
                    + ", errMsg:" + e.getMessage(), e);
        } catch (InterruptedException e) {
            logger.error("读取数据失败,发生InterruptedException! path: " + path
                    + ", errMsg:" + e.getMessage(), e);
        }
        return false;
    }


    @Test
    public void listAllChildren() throws IOException {
        ZooKeeper zooKeeper = new ZooKeeper("10.21.3.77:2181", 2000, null);
        listChildren(zooKeeper, "/");
    }

    /**
     * 递归获取传入path的所有子节点
     *
     * @param path
     * @param zooKeeper
     */
    private void listChildren(ZooKeeper zooKeeper, String path) {
        logger.info("path = " + path);
        try {
            List<String> children = zooKeeper.getChildren(path, false);
            recursionGetChildren(path, children);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 递归获取传入path的所有子节点
     *
     * @param path
     */
    public void listChildren(String path) {
        logger.info("path = " + path);
        try {
            List<String> children = MyZooKeeper.zooKeeper.getChildren(path, false);
            recursionGetChildren(path, children);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void recursionGetChildren(String path, List<String> children) {
        if (children == null || children.isEmpty()) {
            return;
        } else {
            for (String str : children
                    ) {
                if (path.equals("/")) {
                    path = "";
                }
                listChildren(path + "/" + str);
            }
        }
    }

    @Test
    public void watch() throws Exception {
        final ZooKeeper zooKeeper = new ZooKeeper("10.21.3.77:2181", 2000, null);
        final Stat stat = new Stat();
        //创建watcher对象
        Watcher watcher = new Watcher() {
            public void process(WatchedEvent event) {
                String path = event.getPath();
                Event.KeeperState keeperState = event.getState();
                Event.EventType eventType = event.getType();
                System.out.println(path + "，数据被观察到！ " + eventType);
                //对已经被delete的znode要单独做处理，不然观察者会抛出异常
                if (eventType == Event.EventType.NodeDeleted) {
                    System.out.println(path + "，被删除了！");
                    return;
                }
                try {
                    zooKeeper.getData("/test/aa", this, stat);
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        System.out.println("ok");
        byte[] data = zooKeeper.getData("/test/aa", watcher, stat);
        System.out.println(new String(data));
        while (true) {
            Thread.sleep(5000);
        }
    }

    @Test
    public void createAllZnode() throws Exception {
        ZooKeeper zooKeeper = new ZooKeeper("10.21.3.77:2181", 2000, null);
        cucursionCreateZnode(zooKeeper,"","/test/a/b/c","aa");
    }

    public void cucursionCreateZnode(ZooKeeper zooKeeper,String prefix, String path, String data) throws Exception {
        if (path.matches("^/.*")){
            if (path.split("/").length >= 2) {
                String temp1 = path.substring(1,path.length());
                String temp2 = temp1.substring(0,temp1.indexOf("/") == -1?temp1.length():temp1.indexOf("/"));
                String temp3 = temp1.substring(temp1.indexOf("/") == -1?temp1.length():temp1.indexOf("/"));
                zooKeeper.create(  prefix+"/"+temp2,data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
                if (temp3.contains("/")){
                    cucursionCreateZnode(zooKeeper, prefix+"/"+temp2, temp3,data);
                }
            }else {
                zooKeeper.create(path,data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
                return;
            }
        }else {
            System.out.println("format error");
        }
    }
}
