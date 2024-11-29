package com.yiban.hadoop.hdfs.dev;

import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.IOUtils;

import java.io.*;
import java.net.URI;
import java.security.MessageDigest;


/**
 * @auther WEI.DUAN
 * @date 2020/3/15
 * @website http://blog.csdn.net/dwshmilyss
 */
public class HDFSDemo {
    public static FileSystem getFileSystem() throws Exception {
        Configuration conf = new Configuration();
        /*
         * dfs.client.block.write.replace-datanode-on-failure.enable=true
         * 如果在写入管道中存在一个DataNode或者网络故障时，
         * 那么DFSClient将尝试从管道中删除失败的DataNode，
         * 然后继续尝试剩下的DataNodes进行写入。
         * 结果，管道中的DataNodes的数量在减少。
         * enable ：启用特性，disable：禁用特性
         * 该特性是在管道中添加新的DataNodes。
         * 当集群规模非常小时，例如3个节点或更少时，集群管理员可能希望将策略在默认配置文件里面设置为NEVER或者禁用该特性。
         * 否则，因为找不到新的DataNode来替换，用户可能会经历异常高的管道写入错误,导致追加文件操作失败
         */
        conf.set("dfs.client.block.write.replace-datanode-on-failure.enable", "true");

        /*
         * dfs.client.block.write.replace-datanode-on-failure.policy=DEFAULT
         * 这个属性只有在dfs.client.block.write.replace-datanode-on-failure.enable设置true时有效：
         * ALWAYS ：当一个存在的DataNode被删除时，总是添加一个新的DataNode
         * NEVER  ：永远不添加新的DataNode
         * DEFAULT：副本数是r，DataNode的数时n，只要r >= 3时，或者floor(r/2)大于等于n时
         * r>n时再添加一个新的DataNode，并且这个块是hflushed/appended
         * 在追加文件的时候，单机测试因为datanode的数量只有1个，所以需要加上这个参数
         */
        conf.set("dfs.client.block.write.replace-datanode-on-failure.policy", "NEVER");
//        FileSystem fs = FileSystem.get(conf);
        FileSystem fs = FileSystem.get(URI.create("hdfs://localhost:9000"), conf);
        return fs;
    }

    //读取
    public static void readFile(String fileName) throws Exception {
        FileSystem fs = getFileSystem();
        Path path = new Path(fileName);
        FileStatus fileStatus = fs.getFileStatus(path);
        System.out.println(fileStatus.getBlockSize());
        System.out.println(fileStatus.getLen());
        System.out.println(fileStatus.getPath());
        System.out.println(fileStatus.getOwner());
        BlockLocation[] blkLocations = fs.getFileBlockLocations(fileStatus, 0, fileStatus.getLen());
        System.out.println("total block num:" + blkLocations.length);
        for (int i = 0; i < blkLocations.length; i++) {
            System.out.println(blkLocations[i].toString());
            System.out.println("文件在block中的偏移量" + blkLocations[i].getOffset()
                    + ", 长度" + blkLocations[i].getLength() + ",主机" + blkLocations[i].getCachedHosts().length + ",缓存主机:" + blkLocations[i].getHosts()[0]);
        }
        //在open()调用的时候会初始化DFSInputStream并在构造函数中调用openInfo()
        //openInfo()函数会获取LocatedBlocks的信息 LocatedBlocks类中有成员变量List<LocatedBlock> blocks，存储所有的block信息
        FSDataInputStream fsDataInputStream = fs.open(path);
        try {
            //DFSInputStream 继承 FSInputStream,  FSInputStream 继承 InputStream
            //所以copyBytes()最终会调用DFSInpuStream中的read函数
            IOUtils.copyBytes(fsDataInputStream, System.out, 4, false);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            IOUtils.closeStream(fsDataInputStream);
        }
    }

    public static void appendHDFS(FileSystem fs) throws Exception {
        String hdfs_path = "hdfs://localhost:9000/a.txt";//文件路径
        InputStream in = new BufferedInputStream(new FileInputStream(new File("/Users/edz/b.txt")));//要追加的文件流，file为文件
        OutputStream out = fs.append(new Path(hdfs_path));
        IOUtils.copyBytes(in, out, 4096, true);
    }


    //写入
    public static void writeFile(String fileName) throws Exception {
        FileSystem fileSystem = getFileSystem();
        Path path = new Path(fileName);
        FSDataOutputStream outputStream = fileSystem.create(path);
        FileInputStream inputStream = new FileInputStream(new File("d:/a.txt"));
        try {
            IOUtils.copyBytes(inputStream, outputStream, 1024, false);
        } finally {
            IOUtils.closeStream(inputStream);
            IOUtils.closeStream(outputStream);
        }
    }

    public static void testChecksum() throws Exception {
        // HDFS 文件路径
        String hdfsFilePath = "hdfs://localhost:9000/a.txt";
        // 本地文件路径
        String localFilePath = "/Users/edz/a.txt";

        // 配置 HDFS
        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", "hdfs://localhost:9000"); // 替换为你的 NameNode 地址
        FileSystem fs = FileSystem.get(conf);

        // 获取 HDFS 文件的校验和
        FileChecksum hdfsChecksum = fs.getFileChecksum(new Path(hdfsFilePath));
        String hdfsChecksumString = hdfsChecksum.toString();
        System.out.println("hdfsChecksumString = " + hdfsChecksumString);

        // 计算本地文件的校验和
        String localChecksum = calculateFileMD5(new File(localFilePath),"MD5");
        System.out.println("localChecksum = " + localChecksum);

        // 比较校验和
        if (hdfsChecksumString.equals(localChecksum)) {
            System.out.println("The downloaded file is complete.");
        } else {
            System.out.println("The downloaded file is incomplete or corrupted.");
        }

        fs.close();
    }

    // 计算本地文件的 MD5
    private static String calculateFileMD5(File file,String algorithm) throws Exception {
        MessageDigest digest = MessageDigest.getInstance(algorithm);
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }
        }

        StringBuilder hash = new StringBuilder();
        for (byte b : digest.digest()) {
            hash.append(String.format("%02x", b));
        }
        return hash.toString();
    }

    /**
     *
     * @param filePath
     * @return
     * @throws IOException
     */
    public static long calculateGuavaCRC32C(String filePath) throws IOException {
        Hasher hasher = Hashing.crc32c().newHasher();

        try (FileInputStream fis = new FileInputStream(filePath)) {
            byte[] buffer = new byte[8192];
            int bytesRead;

            while ((bytesRead = fis.read(buffer)) != -1) {
                hasher.putBytes(buffer, 0, bytesRead);
            }
        }

        return hasher.hash().padToLong();
    }



    public static void main(String[] args) {
        System.out.println(System.getProperty("java.version"));
        System.out.println(System.getProperty("java.home"));
        try {
//            readFile("/a.txt");
//            writeFile("/b.txt");
//            appendHDFS(getFileSystem());
            testChecksum();
//            String code = calculateFileMD5(new File("/Users/edz/a.txt"), "CRC32C");
//            String checksumHex = Long.toHexString(calculateGuavaCRC32C("/Users/edz/a.txt"));
//            System.out.println(checksumHex);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}